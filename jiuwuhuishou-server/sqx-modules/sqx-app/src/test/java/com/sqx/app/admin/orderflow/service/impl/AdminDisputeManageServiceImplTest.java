package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputePageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeResolveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminDisputeVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminDisputeManageServiceImplTest {

    @Mock
    private IOrderDisputeService iOrderDisputeService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private AdminDisputeManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderDispute.class, RecycleOrder.class, RecycleOrderLog.class, OrderSettlement.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminDisputeVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminDisputeVo.class));
        when(adminLookupSupport.resolveOrderNo(anyLong())).thenReturn("RO001");
    }

    @Test
    void pageInfoShouldShortCircuitWhenUserOrOrderIdsEmpty() {
        when(adminLookupSupport.resolveUserIds("a", null)).thenReturn(Collections.emptyList());
        AdminDisputePageBo pageBo = new AdminDisputePageBo();
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

        OrderDispute dispute = new OrderDispute();
        dispute.setId(1L);
        dispute.setOrderId(10L);
        dispute.setUserId(100L);
        Page<OrderDispute> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(dispute));
        when(iOrderDisputeService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminDisputePageBo pageBo = new AdminDisputePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setStatus("PENDING");
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        Page<OrderDispute> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iOrderDisputeService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminDisputeInfoBo infoBo = new AdminDisputeInfoBo();
        infoBo.setId(1L);
        when(iOrderDisputeService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void infoShouldReturnVoWithAndWithoutOrder() {
        AdminDisputeInfoBo infoBo = new AdminDisputeInfoBo();
        infoBo.setId(1L);

        OrderDispute dispute = new OrderDispute();
        dispute.setId(1L);
        dispute.setOrderId(10L);
        dispute.setUserId(100L);
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);

        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertNull(service.info(infoBo).getOrderStatus());

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.DISPUTE_PENDING.getCode());
        order.setStatusDesc("争议处理中");
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        AdminDisputeVo vo = service.info(infoBo);
        assertEquals(RecycleOrderStatus.DISPUTE_PENDING.getCode(), vo.getOrderStatus());
    }

    @Test
    void resolveShouldValidateDisputeAndOrder() {
        AdminDisputeResolveBo resolveBo = new AdminDisputeResolveBo();
        resolveBo.setId(1L);
        resolveBo.setAction("cancel");

        when(iOrderDisputeService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.resolve(resolveBo));

        OrderDispute dispute = new OrderDispute();
        dispute.setId(1L);
        dispute.setOrderId(10L);
        dispute.setStatus("RESOLVED");
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);
        assertThrows(BizException.class, () -> service.resolve(resolveBo));

        dispute.setStatus("PENDING");
        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.resolve(resolveBo));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        assertThrows(BizException.class, () -> service.resolve(resolveBo));
    }

    @Test
    void resolveShouldCancelOrder() {
        OrderDispute dispute = pendingDispute();
        RecycleOrder order = disputeOrder();
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        AdminDisputeResolveBo resolveBo = new AdminDisputeResolveBo();
        resolveBo.setId(1L);
        resolveBo.setAction("cancel");
        resolveBo.setRemark("用户放弃");

        service.resolve(resolveBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.CANCELLED.getCode(), orderCaptor.getValue().getStatus());
        verify(iOrderDisputeService).updateById(any(OrderDispute.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
        verify(payoutService, never()).createAndComplete(any(), any(), any());
    }

    @Test
    void resolveShouldAdjustAmount() {
        OrderDispute dispute = pendingDispute();
        RecycleOrder order = disputeOrder();
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        AdminDisputeResolveBo missingAmount = new AdminDisputeResolveBo();
        missingAmount.setId(1L);
        missingAmount.setAction("adjust");
        assertThrows(BizException.class, () -> service.resolve(missingAmount));

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(5L);
        settlement.setOrderId(10L);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);

        AdminDisputeResolveBo resolveBo = new AdminDisputeResolveBo();
        resolveBo.setId(1L);
        resolveBo.setAction("adjust");
        resolveBo.setActualAmount(new BigDecimal("150.00"));
        resolveBo.setRemark("重新估价");

        service.resolve(resolveBo);

        verify(iOrderSettlementService).updateById(any(OrderSettlement.class));
        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));
        verify(payoutService, never()).createAndComplete(any(), any(), any());

        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> service.resolve(resolveBo));
    }

    @Test
    void resolveShouldMaintainOriginalPriceWithSettlement() {
        OrderDispute dispute = pendingDispute();
        RecycleOrder order = disputeOrder();
        order.setActualAmount(new BigDecimal("200.00"));
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(5L);
        settlement.setOrderId(10L);
        settlement.setActualAmount(new BigDecimal("180.00"));
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);

        AdminDisputeResolveBo resolveBo = new AdminDisputeResolveBo();
        resolveBo.setId(1L);
        resolveBo.setAction("PROCEED");

        service.resolve(resolveBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode(), orderCaptor.getValue().getStatus());
        assertEquals(Integer.valueOf(0), orderCaptor.getValue().getEstimateAmountConfirmed());
        verify(iOrderSettlementService).updateById(any(OrderSettlement.class));
        verify(payoutService, never()).createAndComplete(any(), any(), any());
        verify(iOrderDisputeService).updateById(any(OrderDispute.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
    }

    @Test
    void resolveShouldMaintainOriginalPriceWithoutSettlement() {
        OrderDispute dispute = pendingDispute();
        RecycleOrder order = disputeOrder();
        order.setActualAmount(new BigDecimal("200.00"));
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        AdminDisputeResolveBo resolveBo = new AdminDisputeResolveBo();
        resolveBo.setId(1L);
        resolveBo.setAction("PROCEED");

        service.resolve(resolveBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode(), orderCaptor.getValue().getStatus());
        verify(payoutService, never()).createAndComplete(any(), any(), any());
        verify(iOrderSettlementService, never()).updateById(any());
    }

    @Test
    void resolveShouldThrowWhenSettlementMissingForAdjust() {
        OrderDispute dispute = pendingDispute();
        RecycleOrder order = disputeOrder();
        when(iOrderDisputeService.getById(1L)).thenReturn(dispute);
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        AdminDisputeResolveBo resolveBo = new AdminDisputeResolveBo();
        resolveBo.setId(1L);
        resolveBo.setAction("adjust");
        resolveBo.setActualAmount(new BigDecimal("150.00"));
        assertThrows(BizException.class, () -> service.resolve(resolveBo));
    }

    private OrderDispute pendingDispute() {
        OrderDispute dispute = new OrderDispute();
        dispute.setId(1L);
        dispute.setOrderId(10L);
        dispute.setStatus("PENDING");
        return dispute;
    }

    private RecycleOrder disputeOrder() {
        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.DISPUTE_PENDING.getCode());
        return order;
    }
}
