package com.sqx.app.payout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.db.service.IOrderPayoutLogService;
import com.sqx.app.payout.db.service.IOrderPayoutService;
import com.sqx.app.payout.domain.bo.PayoutInfoBo;
import com.sqx.app.payout.domain.entity.OrderPayout;
import com.sqx.app.payout.domain.entity.OrderPayoutLog;
import com.sqx.app.payout.domain.vo.PayoutInfoVo;
import com.sqx.app.referral.user.service.ReferralUserCommissionService;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PayoutServiceTest {

    @Mock
    private IOrderPayoutService iOrderPayoutService;
    @Mock
    private IOrderPayoutLogService iOrderPayoutLogService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private ReferralUserCommissionService referralUserCommissionService;
    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;

    @InjectMocks
    private PayoutServiceImpl payoutService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(
                OrderPayout.class,
                OrderPayoutLog.class,
                RecycleOrder.class,
                RecycleOrderLog.class,
                UserWallet.class,
                UserWalletTransaction.class
        );
    }

    @Test
    void infoShouldMapStatusesAndShowWalletChannel() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.COMPLETED.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);

        OrderPayout payout = payout(1L, "SUCCESS");
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(payout);
        OrderPayoutLog log1 = payoutLog(1L, "a");
        OrderPayoutLog log2 = payoutLog(1L, "b");
        when(iOrderPayoutLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(log1, log2));

        PayoutInfoBo bo = new PayoutInfoBo();
        bo.setOrderId(1L);
        PayoutInfoVo vo = payoutService.info(10L, bo);
        assertEquals("已到账", vo.getStatusText());
        assertEquals("用户钱包", vo.getPayChannelText());
        assertEquals("用户钱包", vo.getAccountText());
        assertTrue(vo.getLogs().get(1).getActive());

        payout.setStatus("PROCESSING");
        when(iOrderPayoutLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertEquals("打款中", payoutService.info(10L, bo).getStatusText());
        assertTrue(payoutService.info(10L, bo).getLogs().isEmpty());

        payout.setStatus("PENDING");
        assertEquals("待打款", payoutService.info(10L, bo).getStatusText());
        payout.setStatus("OTHER");
        assertEquals("处理中", payoutService.info(10L, bo).getStatusText());
    }

    @Test
    void infoShouldRejectMissingOrderOrPayout() {
        when(iRecycleOrderService.getById(1L)).thenReturn(null);
        PayoutInfoBo bo = new PayoutInfoBo();
        bo.setOrderId(1L);
        assertThrows(BizException.class, () -> payoutService.info(10L, bo));

        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, 99L, RecycleOrderStatus.COMPLETED.getCode()));
        assertThrows(BizException.class, () -> payoutService.info(10L, bo));

        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, 10L, RecycleOrderStatus.COMPLETED.getCode()));
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> payoutService.info(10L, bo));
    }

    @Test
    void createAndCompleteShouldShortCircuitWhenAlreadySuccess() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(payout(1L, "SUCCESS"));
        payoutService.createAndComplete(order, new BigDecimal("10"), 10L);
        verify(iOrderPayoutService, never()).save(any());
        verify(iRecycleOrderService, never()).updateById(any());
        verify(iUserWalletService, never()).incr(any(), any());
    }

    @Test
    void createAndCompleteShouldCompleteExistingThenCreateNew() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        OrderPayout existing = payout(1L, "PROCESSING");
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        stubWalletCredit();
        payoutService.createAndComplete(order, new BigDecimal("10"), 10L);
        assertEquals(RecycleOrderStatus.COMPLETED.getCode(), order.getStatus());
        verify(referralUserCommissionService).saveUserCommission(1L);
        verify(iUserWalletService).incr(eq(10L), eq(new BigDecimal("10")));
        verify(iUserWalletTransactionService).save(any(UserWalletTransaction.class));

        RecycleOrder fresh = ownedOrder(2L, 10L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        doAnswer(inv -> {
            OrderPayout p = inv.getArgument(0);
            p.setId(88L);
            return true;
        }).when(iOrderPayoutService).save(any(OrderPayout.class));
        payoutService.createAndComplete(fresh, new BigDecimal("11"), 10L);
        assertEquals(RecycleOrderStatus.COMPLETED.getCode(), fresh.getStatus());
        verify(iOrderPayoutService).save(any(OrderPayout.class));
    }

    @Test
    void startPayoutShouldCoverExistingAndNewBranches() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(payout(1L, "SUCCESS"));
        assertThrows(BizException.class, () -> payoutService.startPayout(order, new BigDecimal("1"), 1L));

        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(payout(1L, "PROCESSING"));
        payoutService.startPayout(order, new BigDecimal("1"), 1L);
        verify(iRecycleOrderService, never()).updateById(any());

        OrderPayout pending = payout(1L, "PENDING");
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(pending);
        payoutService.startPayout(order, new BigDecimal("2"), 1L);
        assertEquals("PROCESSING", pending.getStatus());
        assertEquals("WALLET", pending.getPayChannel());
        assertEquals(RecycleOrderStatus.PAYOUT_PROCESSING.getCode(), order.getStatus());

        RecycleOrder order2 = ownedOrder(2L, 10L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        doAnswer(inv -> {
            OrderPayout p = inv.getArgument(0);
            p.setId(99L);
            assertEquals("WALLET", p.getPayChannel());
            return true;
        }).when(iOrderPayoutService).save(any(OrderPayout.class));
        payoutService.startPayout(order2, new BigDecimal("3"), 1L);
        assertEquals(RecycleOrderStatus.PAYOUT_PROCESSING.getCode(), order2.getStatus());
    }

    @Test
    void finishPayoutShouldValidateCompleteAndCreditWallet() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> payoutService.finishPayout(order, 1L));

        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(payout(1L, "SUCCESS"));
        payoutService.finishPayout(order, 1L);
        verify(referralUserCommissionService, never()).saveUserCommission(any());
        verify(iUserWalletService, never()).incr(any(), any());

        OrderPayout processing = payout(1L, "PROCESSING");
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(processing);
        stubWalletCredit();
        payoutService.finishPayout(order, 1L);
        assertEquals(RecycleOrderStatus.COMPLETED.getCode(), order.getStatus());
        assertEquals("WALLET", processing.getPayChannel());
        verify(referralUserCommissionService).saveUserCommission(1L);
        verify(iUserWalletService).incr(eq(10L), eq(new BigDecimal("10")));
        verify(iUserWalletTransactionService).save(argThat(tx ->
                "回收订单打款到账".equals(tx.getTitle())
                        && tx.getBizId().equals(1L)
                        && tx.getDirection() == 1
                        && tx.getAmount().compareTo(new BigDecimal("10")) == 0
        ));
    }

    @Test
    void completePayoutShouldSkipOrderTransitionWhenNotProcessing() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        OrderPayout existing = payout(1L, "PENDING");
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        stubWalletCredit();
        payoutService.createAndComplete(order, new BigDecimal("1"), 1L);
        assertEquals(RecycleOrderStatus.WAIT_PAYOUT.getCode(), order.getStatus());
        verify(referralUserCommissionService, never()).saveUserCommission(any());
        verify(iUserWalletService).incr(eq(10L), eq(new BigDecimal("10")));
    }

    @Test
    void finishPayoutShouldSkipWalletCreditWhenAlreadyRecorded() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        OrderPayout processing = payout(1L, "PROCESSING");
        when(iOrderPayoutService.getOne(any(LambdaQueryWrapper.class))).thenReturn(processing);
        when(iUserWalletTransactionService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);

        payoutService.finishPayout(order, 1L);

        verify(iUserWalletService, never()).incr(any(), any());
        verify(iUserWalletTransactionService, never()).save(any());
        assertEquals(RecycleOrderStatus.COMPLETED.getCode(), order.getStatus());
    }

    private void stubWalletCredit() {
        when(iUserWalletTransactionService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        UserWallet wallet = new UserWallet();
        wallet.setUserId(10L);
        wallet.setAmount(BigDecimal.ZERO);
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);
        when(iUserWalletTransactionService.save(any(UserWalletTransaction.class))).thenReturn(true);
    }

    private RecycleOrder ownedOrder(Long id, Long userId, String status) {
        RecycleOrder order = new RecycleOrder();
        order.setId(id);
        order.setOrderNo("RO" + id);
        order.setUserId(userId);
        order.setStatus(status);
        return order;
    }

    private OrderPayout payout(Long orderId, String status) {
        OrderPayout payout = new OrderPayout();
        payout.setId(orderId);
        payout.setOrderId(orderId);
        payout.setUserId(10L);
        payout.setPayoutNo("PO" + orderId);
        payout.setAmount(new BigDecimal("10"));
        payout.setStatus(status);
        return payout;
    }

    private OrderPayoutLog payoutLog(Long payoutId, String content) {
        OrderPayoutLog log = new OrderPayoutLog();
        log.setPayoutId(payoutId);
        log.setContent(content);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }
}
