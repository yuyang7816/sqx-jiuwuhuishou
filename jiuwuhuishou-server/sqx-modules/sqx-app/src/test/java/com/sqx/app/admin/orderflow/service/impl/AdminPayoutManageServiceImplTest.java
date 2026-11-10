package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutCreateBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutPageBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminPayoutVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.db.service.IOrderPayoutLogService;
import com.sqx.app.payout.db.service.IOrderPayoutService;
import com.sqx.app.payout.domain.entity.OrderPayout;
import com.sqx.app.payout.domain.entity.OrderPayoutLog;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminPayoutManageServiceImplTest {

    @Mock
    private IOrderPayoutService iOrderPayoutService;
    @Mock
    private IOrderPayoutLogService iOrderPayoutLogService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private AdminPayoutManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderPayout.class, OrderPayoutLog.class, RecycleOrder.class, OrderSettlement.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminPayoutVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminPayoutVo.class));
        when(adminLookupSupport.resolveOrderNo(anyLong())).thenReturn("RO001");
    }

    @Test
    void pageInfoShouldShortCircuitWhenUserOrOrderIdsEmpty() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        AdminPayoutPageBo pageBo = new AdminPayoutPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds("x")).thenReturn(Collections.emptyList());
        pageBo.setUserName(null);
        pageBo.setOrderNo("x");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldReturnRecordsAndEmptyList() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        OrderPayout payout = new OrderPayout();
        payout.setId(1L);
        payout.setOrderId(10L);
        payout.setUserId(100L);
        Page<OrderPayout> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(payout));
        when(iOrderPayoutService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminPayoutPageBo pageBo = new AdminPayoutPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setStatus("PENDING");
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        Page<OrderPayout> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iOrderPayoutService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminPayoutInfoBo infoBo = new AdminPayoutInfoBo();
        infoBo.setId(1L);
        when(iOrderPayoutService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void infoShouldReturnDetailWithOrderAndLogs() {
        AdminPayoutInfoBo infoBo = new AdminPayoutInfoBo();
        infoBo.setId(1L);

        OrderPayout payout = new OrderPayout();
        payout.setId(1L);
        payout.setOrderId(10L);
        payout.setUserId(100L);
        when(iOrderPayoutService.getById(1L)).thenReturn(payout);

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus("COMPLETED");
        order.setStatusDesc("已完成");
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        OrderPayoutLog log = new OrderPayoutLog();
        log.setStatus("SUCCESS");
        log.setContent("打款成功");
        log.setCreateTime(LocalDateTime.now());
        when(iOrderPayoutLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(log));

        AdminPayoutVo vo = service.info(infoBo);
        assertEquals("COMPLETED", vo.getOrderStatus());
        assertEquals(1, vo.getLogs().size());

        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        when(iOrderPayoutLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        AdminPayoutVo noDetail = service.info(infoBo);
        assertNull(noDetail.getOrderStatus());
        assertTrue(noDetail.getLogs().isEmpty());
    }

    @Test
    void createShouldResolveOrderByIdOrOrderNoAndValidateStatus() {
        AdminPayoutCreateBo byId = new AdminPayoutCreateBo();
        byId.setOrderId(10L);
        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.create(byId));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        assertThrows(BizException.class, () -> service.create(byId));

        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setActualAmount(new BigDecimal("100.00"));
        service.create(byId);
        verify(payoutService).startPayout(eq(order), eq(new BigDecimal("100.00")), isNull());

        AdminPayoutCreateBo blankNo = new AdminPayoutCreateBo();
        assertThrows(BizException.class, () -> service.create(blankNo));

        AdminPayoutCreateBo byNo = new AdminPayoutCreateBo();
        byNo.setOrderNo(" RO001 ");
        when(iRecycleOrderService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> service.create(byNo));

        when(iRecycleOrderService.getOne(any(LambdaQueryWrapper.class))).thenReturn(order);
        service.create(byNo);
        verify(payoutService, times(2)).startPayout(any(), any(), isNull());
    }

    @Test
    void createShouldResolveAmountFromSettlementAndEstimate() {
        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setEstimateAmount(new BigDecimal("50.00"));
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        OrderSettlement settlement = new OrderSettlement();
        settlement.setEstimateAmount(new BigDecimal("60.00"));
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);

        AdminPayoutCreateBo createBo = new AdminPayoutCreateBo();
        createBo.setOrderId(10L);
        service.create(createBo);
        verify(payoutService).startPayout(eq(order), eq(new BigDecimal("60.00")), isNull());

        settlement.setActualAmount(new BigDecimal("70.00"));
        service.create(createBo);
        verify(payoutService).startPayout(eq(order), eq(new BigDecimal("70.00")), isNull());
    }

    @Test
    void confirmShouldValidateAndCompletePayout() {
        AdminPayoutConfirmBo confirmBo = new AdminPayoutConfirmBo();
        confirmBo.setId(1L);
        when(iOrderPayoutService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        OrderPayout payout = new OrderPayout();
        payout.setId(1L);
        payout.setOrderId(10L);
        payout.setAmount(new BigDecimal("80.00"));
        payout.setStatus("SUCCESS");
        when(iOrderPayoutService.getById(1L)).thenReturn(payout);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        payout.setStatus("PENDING");
        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        service.confirm(confirmBo);
        verify(payoutService).finishPayout(order, null);
    }

    @Test
    void createShouldFallbackToOrderEstimateAmount() {
        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setEstimateAmount(new BigDecimal("45.00"));
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        AdminPayoutCreateBo createBo = new AdminPayoutCreateBo();
        createBo.setOrderId(10L);
        service.create(createBo);

        verify(payoutService).startPayout(order, new BigDecimal("45.00"), null);
    }
}
