package com.sqx.app.settlement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.bo.SettlementConfirmBo;
import com.sqx.app.settlement.domain.bo.SettlementDisputeBo;
import com.sqx.app.settlement.domain.bo.SettlementInfoBo;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import com.sqx.app.settlement.domain.vo.SettlementConfirmVo;
import com.sqx.app.settlement.domain.vo.SettlementInfoVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private IOrderDisputeService iOrderDisputeService;
    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderSettlement.class, OrderDispute.class, RecycleOrder.class, RecycleOrderLog.class);
    }

    @Test
    void infoShouldPreferSettlementActualAmount() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        order.setActualAmount(new BigDecimal("1"));
        OrderSettlement settlement = settlement(order.getId(), "PENDING", new BigDecimal("9"), new BigDecimal("8"));
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);

        SettlementInfoBo bo = new SettlementInfoBo();
        bo.setOrderId(1L);
        SettlementInfoVo vo = settlementService.info(10L, bo);
        assertEquals(new BigDecimal("8"), vo.getActualAmount());
        assertFalse(vo.getConfirmed());
    }

    @Test
    void infoShouldFallbackToOrderAndNotUseEstimate() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        order.setActualAmount(new BigDecimal("7"));
        OrderSettlement settlement = settlement(order.getId(), "CONFIRMED", new BigDecimal("9"), null);
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);

        SettlementInfoBo bo = new SettlementInfoBo();
        bo.setOrderId(1L);
        assertEquals(new BigDecimal("7"), settlementService.info(10L, bo).getActualAmount());
        assertTrue(settlementService.info(10L, bo).getConfirmed());

        order.setActualAmount(null);
        assertNull(settlementService.info(10L, bo).getActualAmount());
    }

    @Test
    void infoShouldRejectMissingOrderOrSettlement() {
        when(iRecycleOrderService.getById(1L)).thenReturn(null);
        SettlementInfoBo bo = new SettlementInfoBo();
        bo.setOrderId(1L);
        assertThrows(BizException.class, () -> settlementService.info(10L, bo));

        RecycleOrder other = ownedOrder(1L, 99L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(other);
        assertThrows(BizException.class, () -> settlementService.info(10L, bo));

        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode()));
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> settlementService.info(10L, bo));
    }

    @Test
    void confirmShouldValidateAndTriggerPayout() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        SettlementConfirmBo bo = new SettlementConfirmBo();
        bo.setOrderId(1L);
        assertThrows(BizException.class, () -> settlementService.confirm(10L, bo));

        order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        OrderSettlement confirmed = settlement(1L, "CONFIRMED", new BigDecimal("9"), new BigDecimal("8"));
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(confirmed);
        assertThrows(BizException.class, () -> settlementService.confirm(10L, bo));

        OrderSettlement pending = settlement(1L, "PENDING", new BigDecimal("9"), null);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(pending);
        order.setActualAmount(null);
        assertThrows(BizException.class, () -> settlementService.confirm(10L, bo));

        order.setActualAmount(new BigDecimal("6.6"));

        SettlementConfirmVo vo = settlementService.confirm(10L, bo);
        assertEquals(RecycleOrderStatus.PAYOUT_PROCESSING.getStatusKey(), vo.getOrderStatusKey());
        assertEquals("已确认成交，等待平台打款", vo.getMessage());
        verify(payoutService).startPayout(eq(order), eq(new BigDecimal("6.6")), eq(10L));
        verify(payoutService, never()).createAndComplete(any(), any(), any());
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
        assertEquals(RecycleOrderStatus.WAIT_PAYOUT.getCode(), order.getStatus());
    }

    @Test
    void disputeShouldValidateAndPersist() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        SettlementDisputeBo bo = new SettlementDisputeBo();
        bo.setOrderId(1L);
        bo.setReason("价格不对");
        bo.setDescription("详情");
        assertThrows(BizException.class, () -> settlementService.dispute(10L, bo));

        order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        settlementService.dispute(10L, bo);

        ArgumentCaptor<OrderDispute> disputeCaptor = ArgumentCaptor.forClass(OrderDispute.class);
        verify(iOrderDisputeService).save(disputeCaptor.capture());
        assertEquals("价格不对", disputeCaptor.getValue().getReason());
        assertEquals(RecycleOrderStatus.DISPUTE_PENDING.getCode(), order.getStatus());
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
    }

    private RecycleOrder ownedOrder(Long id, Long userId, String status) {
        RecycleOrder order = new RecycleOrder();
        order.setId(id);
        order.setUserId(userId);
        order.setStatus(status);
        return order;
    }

    private OrderSettlement settlement(Long orderId, String status, BigDecimal estimate, BigDecimal actual) {
        OrderSettlement settlement = new OrderSettlement();
        settlement.setOrderId(orderId);
        settlement.setSettlementNo("ST1");
        settlement.setStatus(status);
        settlement.setEstimateAmount(estimate);
        settlement.setActualAmount(actual);
        return settlement;
    }
}
