package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementSaveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminSettlementVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
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
class AdminSettlementManageServiceImplTest {

    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private AdminSettlementManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderSettlement.class, RecycleOrder.class, RecycleOrderLog.class);
    }

    @BeforeEach
    void setUp() {
        clearInvocations(iOrderSettlementService, iRecycleOrderService, iRecycleOrderLogService, payoutService);
        doAnswer(inv -> {
            AdminSettlementVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminSettlementVo.class));
        when(adminLookupSupport.resolveOrderNo(anyLong())).thenReturn("RO001");
    }

    @Test
    void pageInfoShouldShortCircuitWhenUserOrOrderIdsEmpty() {
        when(adminLookupSupport.resolveUserIds("a", null)).thenReturn(Collections.emptyList());
        AdminSettlementPageBo pageBo = new AdminSettlementPageBo();
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

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(1L);
        settlement.setOrderId(10L);
        settlement.setUserId(100L);
        Page<OrderSettlement> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(settlement));
        when(iOrderSettlementService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminSettlementPageBo pageBo = new AdminSettlementPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setStatus("PENDING");
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        Page<OrderSettlement> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iOrderSettlementService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminSettlementInfoBo infoBo = new AdminSettlementInfoBo();
        infoBo.setId(1L);
        when(iOrderSettlementService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void infoShouldReturnVoWithAndWithoutOrder() {
        AdminSettlementInfoBo infoBo = new AdminSettlementInfoBo();
        infoBo.setId(1L);

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(1L);
        settlement.setOrderId(10L);
        settlement.setUserId(100L);
        when(iOrderSettlementService.getById(1L)).thenReturn(settlement);

        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertNull(service.info(infoBo).getOrderStatus());

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus("WAIT_PAYOUT");
        order.setStatusDesc("待打款");
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        AdminSettlementVo vo = service.info(infoBo);
        assertEquals("WAIT_PAYOUT", vo.getOrderStatus());
        assertEquals("待打款", vo.getOrderStatusDesc());
    }

    @Test
    void saveShouldRejectConfirmedAndUpdateAmounts() {
        AdminSettlementSaveBo saveBo = new AdminSettlementSaveBo();
        saveBo.setId(1L);
        saveBo.setActualAmount(new BigDecimal("120.00"));
        saveBo.setRemark("现场议价");

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(1L);
        settlement.setOrderId(10L);
        settlement.setStatus("CONFIRMED");
        when(iOrderSettlementService.getById(1L)).thenReturn(settlement);
        assertThrows(BizException.class, () -> service.save(saveBo));

        settlement.setStatus("PENDING");
        when(iOrderSettlementService.getById(1L)).thenReturn(settlement);
        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.save(saveBo));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        clearInvocations(iOrderSettlementService, iRecycleOrderService, iRecycleOrderLogService);
        service.save(saveBo);
        verify(iOrderSettlementService).updateById(any(OrderSettlement.class));
        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));

        saveBo.setRemark(null);
        service.save(saveBo);
        verify(iOrderSettlementService, times(2)).updateById(any(OrderSettlement.class));
    }

    @Test
    void confirmShouldValidateAndStartPayout() {
        AdminSettlementConfirmBo confirmBo = new AdminSettlementConfirmBo();
        confirmBo.setId(1L);
        confirmBo.setRemark("用户电话确认");

        when(iOrderSettlementService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(1L);
        settlement.setOrderId(10L);
        settlement.setStatus("CONFIRMED");
        when(iOrderSettlementService.getById(1L)).thenReturn(settlement);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        settlement.setStatus("PENDING");
        settlement.setEstimateAmount(new BigDecimal("90.00"));
        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        assertThrows(BizException.class, () -> service.confirm(confirmBo));

        order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        order.setActualAmount(new BigDecimal("100.00"));
        service.confirm(confirmBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.WAIT_PAYOUT.getCode(), orderCaptor.getValue().getStatus());
        assertEquals(1, orderCaptor.getValue().getEstimateAmountConfirmed());
        verify(payoutService).startPayout(any(RecycleOrder.class), eq(new BigDecimal("100.00")), isNull());
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));

        OrderSettlement pendingSettlement = new OrderSettlement();
        pendingSettlement.setId(2L);
        pendingSettlement.setOrderId(11L);
        pendingSettlement.setStatus("PENDING");
        pendingSettlement.setEstimateAmount(new BigDecimal("90.00"));
        when(iOrderSettlementService.getById(2L)).thenReturn(pendingSettlement);

        RecycleOrder fallbackOrder = new RecycleOrder();
        fallbackOrder.setId(11L);
        fallbackOrder.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        when(iRecycleOrderService.getById(11L)).thenReturn(fallbackOrder);

        AdminSettlementConfirmBo fallbackConfirm = new AdminSettlementConfirmBo();
        fallbackConfirm.setId(2L);
        service.confirm(fallbackConfirm);
        verify(payoutService).startPayout(any(RecycleOrder.class), eq(new BigDecimal("90.00")), isNull());
    }
}
