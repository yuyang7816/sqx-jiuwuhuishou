package com.sqx.app.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.*;
import com.sqx.app.admin.order.domain.vo.AdminOrderDetailVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderVo;
import com.sqx.app.admin.order.service.AdminOrderRescheduleManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderException;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleFulfillType;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.entity.OrderReview;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.GenUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminOrderManageServiceImplTest {

    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private IPickupTaskService iPickupTaskService;
    @Mock
    private IPickupTaskLogService iPickupTaskLogService;
    @Mock
    private IUserService iUserService;
    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private IOrderDisputeService iOrderDisputeService;
    @Mock
    private PayoutService payoutService;
    @Mock
    private AdminOrderRescheduleManageService adminOrderRescheduleManageService;
    @Mock
    private IOrderExceptionService iOrderExceptionService;
    @Mock
    private IOrderReviewService iOrderReviewService;
    @Mock
    private com.sqx.app.express.sf.SfExpressOrderService sfExpressOrderService;
    @Mock
    private com.sqx.app.express.aliyun.AliyunLogisticsService aliyunLogisticsService;

    @InjectMocks
    private AdminOrderManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(
                RecycleOrder.class,
                RecycleOrderLog.class,
                PickupTask.class,
                PickupTaskLog.class,
                OrderSettlement.class,
                OrderDispute.class,
                OrderException.class,
                OrderReview.class
        );
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminOrderVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminOrderVo.class));
        when(iOrderDisputeService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iOrderReviewService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
    }

    @Test
    void pageInfoShouldShortCircuitWhenUserOrOrderIdsEmpty() {
        when(adminLookupSupport.resolveUserIds("a", null)).thenReturn(Collections.emptyList());
        AdminOrderPageBo pageBo = pageBo();
        pageBo.setUserName("a");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds("x")).thenReturn(Collections.emptyList());
        pageBo.setUserName(null);
        pageBo.setOrderNo("x");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldMapRecordsIncludingMailAndConfirmedFlags() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setEstimateAmountConfirmed(1);

        RecycleOrder visitOrder = order(2L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        visitOrder.setFulfillType(RecycleFulfillType.VISIT.getCode());
        visitOrder.setEstimateAmountConfirmed(0);

        RecycleOrder payoutOrder = order(3L, RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        payoutOrder.setEstimateAmountConfirmed(0);

        Page<RecycleOrder> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(mailOrder, visitOrder, payoutOrder));
        when(iRecycleOrderService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        AdminOrderPageBo pageBo = pageBo();
        pageBo.setStatusKey("paying");
        pageBo.setOrderType("appliance");
        pageBo.setFulfillType("mail");
        pageBo.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        pageBo.setUserId(100L);

        assertEquals(3, service.pageInfo(pageBo).getRecords().size());
        AdminOrderVo mailVo = service.pageInfo(pageBo).getRecords().get(0);
        assertEquals("顺丰速运", mailVo.getExpressCompany());
        assertTrue(mailVo.getEstimateAmountConfirmed());
        assertTrue(service.pageInfo(pageBo).getRecords().get(2).getEstimateAmountConfirmed());

        Page<RecycleOrder> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iRecycleOrderService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(emptyPage);
        pageBo.setStatusKey("unknown-key");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissingAndReturnDetail() {
        AdminOrderInfoBo infoBo = new AdminOrderInfoBo();
        infoBo.setId(1L);
        when(iRecycleOrderService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        RecycleOrder order = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setUserId(100L);
        when(iRecycleOrderService.getById(1L)).thenReturn(order);

        RecycleOrderLog log = new RecycleOrderLog();
        log.setId(1L);
        log.setOrderId(1L);
        log.setActionType("STATUS_CHANGE");
        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(log));
        when(adminOrderRescheduleManageService.listByOrderId(1L)).thenReturn(Collections.emptyList());

        OrderException exception = new OrderException();
        exception.setId(10L);
        exception.setOrderId(1L);
        exception.setExceptionType("延迟");
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(exception));

        OrderDispute dispute = new OrderDispute();
        dispute.setId(11L);
        dispute.setOrderId(1L);
        dispute.setReason("价格不对");
        dispute.setDescription("实际低于预估");
        dispute.setStatus("PENDING");
        when(iOrderDisputeService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(dispute));

        OrderReview review = new OrderReview();
        review.setId(12L);
        review.setOrderId(1L);
        review.setScore(4);
        review.setComplaintType("服务态度");
        review.setContent("态度一般");
        when(iOrderReviewService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(review));

        AdminOrderDetailVo detail = service.info(infoBo);
        assertEquals(1, detail.getLogs().size());
        assertEquals(1, detail.getExceptions().size());
        assertEquals("RO1", detail.getExceptions().get(0).getOrderNo());
        assertEquals(1, detail.getDisputes().size());
        assertEquals("价格不对", detail.getDisputes().get(0).getReason());
        assertEquals(1, detail.getReviews().size());
        assertEquals("服务态度", detail.getReviews().get(0).getComplaintType());
        assertNotNull(detail.getLatestDispute());
        assertNotNull(detail.getLatestException());
        assertNotNull(detail.getLatestReview());
        assertTrue(detail.getUserFeedbackSummary().contains("争议"));
        assertTrue(detail.getUserFeedbackSummary().contains("评价"));

        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iOrderDisputeService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iOrderReviewService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.info(infoBo).getLogs().isEmpty());

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        mailOrder.setContactMobile("15200000002");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);
        when(aliyunLogisticsService.listExpressTracks(eq("SF0217121758386"), any(), any()))
                .thenReturn(Collections.emptyList());
        try (MockedStatic<com.sqx.common.redis.utils.ConfigUtil> config =
                     mockStatic(com.sqx.common.redis.utils.ConfigUtil.class)) {
            config.when(() -> com.sqx.common.redis.utils.ConfigUtil.getConfigValueOrDefault(anyString()))
                    .thenReturn("4008006688");
            assertNotNull(service.info(infoBo).getExpressTracks());
            when(aliyunLogisticsService.listExpressTracks(eq("SF0217121758386"), any(), any()))
                    .thenThrow(new BizException("凭证未配置"));
            assertTrue(service.info(infoBo).getExpressTracks().isEmpty());
        }
    }

    @Test
    void pageInfoShouldFillLatestUserFeedback() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        RecycleOrder order = order(1L, RecycleOrderStatus.DISPUTE_PENDING.getCode());
        Page<RecycleOrder> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(order));
        when(iRecycleOrderService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        OrderDispute dispute = new OrderDispute();
        dispute.setId(21L);
        dispute.setOrderId(1L);
        dispute.setReason("价格争议");
        dispute.setDescription("金额偏低");
        when(iOrderDisputeService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(dispute));

        OrderReview review = new OrderReview();
        review.setId(22L);
        review.setOrderId(1L);
        review.setScore(2);
        review.setComplaintType("价格争议");
        review.setContent("不满意");
        when(iOrderReviewService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(review));

        OrderException exception = new OrderException();
        exception.setId(23L);
        exception.setOrderId(1L);
        exception.setExceptionType("上门迟到");
        exception.setDescription("超时未到");
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(exception));

        AdminOrderVo vo = service.pageInfo(pageBo()).getRecords().get(0);
        assertEquals("价格争议", vo.getLatestDispute().getReason());
        assertEquals("不满意", vo.getLatestReview().getContent());
        assertEquals("上门迟到", vo.getLatestException().getExceptionType());
        assertTrue(vo.getUserFeedbackSummary().contains("争议：价格争议"));
        assertTrue(vo.getUserFeedbackSummary().contains("评价"));
        assertTrue(vo.getUserFeedbackSummary().contains("异常"));

        // duplicate + null orderId 覆盖 loadLatestByOrderId continue
        OrderException nullOrder = new OrderException();
        nullOrder.setId(24L);
        OrderException dup = new OrderException();
        dup.setId(25L);
        dup.setOrderId(1L);
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(exception, nullOrder, dup));
        assertEquals("上门迟到", service.pageInfo(pageBo()).getRecords().get(0).getLatestException().getExceptionType());

        // vo.id 为空 → orderIds 为空提前返回
        RecycleOrder noId = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        noId.setId(null);
        Page<RecycleOrder> noIdPage = new Page<>(1, 10);
        noIdPage.setRecords(Collections.singletonList(noId));
        when(iRecycleOrderService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(noIdPage);
        assertNull(service.pageInfo(pageBo()).getRecords().get(0).getLatestException());
    }

    @Test
    void cancelShouldValidateAndAppendLog() {
        AdminOrderCancelBo cancelBo = new AdminOrderCancelBo();
        cancelBo.setId(1L);
        when(iRecycleOrderService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.cancel(cancelBo));

        RecycleOrder cancelled = order(1L, RecycleOrderStatus.CANCELLED.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(cancelled);
        assertThrows(BizException.class, () -> service.cancel(cancelBo));

        RecycleOrder completed = order(1L, RecycleOrderStatus.COMPLETED.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(completed);
        assertThrows(BizException.class, () -> service.cancel(cancelBo));

        RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        cancelBo.setReason("用户要求");
        service.cancel(cancelBo);

        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));

        RecycleOrder orderWithoutReason = order(1L, RecycleOrderStatus.WAIT_AUDIT.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(orderWithoutReason);
        cancelBo.setReason(null);
        service.cancel(cancelBo);
        verify(iRecycleOrderLogService, times(2)).save(any(RecycleOrderLog.class));

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);
        service.cancel(cancelBo);
        verify(sfExpressOrderService).cancelMailOrder(mailOrder);
    }

    @Test
    void updateStatusShouldTransitionAndLog() {
        AdminOrderUpdateStatusBo updateBo = new AdminOrderUpdateStatusBo();
        updateBo.setId(1L);
        updateBo.setTargetStatus(RecycleOrderStatus.WAIT_ASSIGN.getCode());
        updateBo.setRemark("审核通过");

        RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_AUDIT.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);

        service.updateStatus(updateBo);

        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));

        RecycleOrder orderWithoutRemark = order(1L, RecycleOrderStatus.WAIT_AUDIT.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(orderWithoutRemark);
        updateBo.setRemark(null);
        service.updateStatus(updateBo);
        verify(iRecycleOrderLogService, times(2)).save(any(RecycleOrderLog.class));
    }

    @Test
    void assignPickupShouldRejectMailOrderAndInvalidStaff() {
        AdminOrderAssignPickupBo assignBo = new AdminOrderAssignPickupBo();
        assignBo.setOrderId(1L);
        assignBo.setStaffId(100L);

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);
        assertThrows(BizException.class, () -> service.assignPickup(assignBo));

        RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iUserService.getById(100L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.assignPickup(assignBo));
    }

    @Test
    void assignPickupShouldCreateNewTaskWhenWaitAssign() {
        try (MockedStatic<GenUtil> genUtil = mockStatic(GenUtil.class)) {
            genUtil.when(GenUtil::genOrderNo).thenReturn("123456");

            RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
            order.setCategoryName("冰箱");
            order.setAppointmentTime(LocalDateTime.now());
            when(iRecycleOrderService.getById(1L)).thenReturn(order);
            when(iUserService.getById(100L)).thenReturn(staff(100L, "员工A"));
            when(iPickupTaskService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            AdminOrderAssignPickupBo assignBo = new AdminOrderAssignPickupBo();
            assignBo.setOrderId(1L);
            assignBo.setStaffId(100L);
            service.assignPickup(assignBo);

            verify(iPickupTaskService).save(any(PickupTask.class));
            verify(iPickupTaskLogService).save(any(PickupTaskLog.class));
            verify(iRecycleOrderService).updateById(any(RecycleOrder.class));
            verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
        }
    }

    @Test
    void assignPickupShouldReassignExistingTaskForOtherStatus() {
        RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iUserService.getById(100L)).thenReturn(staff(100L, "员工B"));

        PickupTask task = new PickupTask();
        task.setId(5L);
        task.setOrderId(1L);
        task.setStatus(PickupTaskStatus.PENDING.name());
        when(iPickupTaskService.getOne(any(LambdaQueryWrapper.class))).thenReturn(task);

        AdminOrderAssignPickupBo assignBo = new AdminOrderAssignPickupBo();
        assignBo.setOrderId(1L);
        assignBo.setStaffId(100L);
        service.assignPickup(assignBo);

        verify(iPickupTaskService).updateById(any(PickupTask.class));
        verify(iRecycleOrderService, never()).updateById(any(RecycleOrder.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
    }

    @Test
    void assignPickupShouldFinishTaskWhenInProgress() {
        RecycleOrder order = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iUserService.getById(100L)).thenReturn(staff(100L, "员工C"));

        PickupTask pendingTask = new PickupTask();
        pendingTask.setId(5L);
        pendingTask.setOrderId(1L);
        pendingTask.setStatus(PickupTaskStatus.PENDING.name());
        when(iPickupTaskService.getOne(any(LambdaQueryWrapper.class))).thenReturn(pendingTask);

        AdminOrderAssignPickupBo assignBo = new AdminOrderAssignPickupBo();
        assignBo.setOrderId(1L);
        assignBo.setStaffId(100L);
        service.assignPickup(assignBo);

        verify(iPickupTaskService, atLeastOnce()).updateById(any(PickupTask.class));
        verify(iPickupTaskLogService, atLeastOnce()).save(any(PickupTaskLog.class));
        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));

        PickupTask doneTask = new PickupTask();
        doneTask.setId(6L);
        doneTask.setOrderId(1L);
        doneTask.setStatus(PickupTaskStatus.DONE.name());
        when(iPickupTaskService.getOne(any(LambdaQueryWrapper.class))).thenReturn(doneTask);
        service.assignPickup(assignBo);
    }

    @Test
    void assignPickupShouldUseDefaultItemSummaryWhenCategoryBlank() {
        try (MockedStatic<GenUtil> genUtil = mockStatic(GenUtil.class)) {
            genUtil.when(GenUtil::genOrderNo).thenReturn("999");

            RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
            order.setCategoryName("");
            when(iRecycleOrderService.getById(1L)).thenReturn(order);
            when(iUserService.getById(100L)).thenReturn(staff(100L, "员工D"));
            when(iPickupTaskService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            AdminOrderAssignPickupBo assignBo = new AdminOrderAssignPickupBo();
            assignBo.setOrderId(1L);
            assignBo.setStaffId(100L);
            service.assignPickup(assignBo);

            ArgumentCaptor<PickupTask> captor = ArgumentCaptor.forClass(PickupTask.class);
            verify(iPickupTaskService).save(captor.capture());
            assertEquals("待确认", captor.getValue().getItemSummary());
        }
    }

    @Test
    void updateEstimateShouldValidateAndUpdateSettlement() {
        AdminOrderUpdateEstimateBo updateBo = new AdminOrderUpdateEstimateBo();
        updateBo.setId(1L);
        updateBo.setActualAmount(new BigDecimal("88.00"));
        updateBo.setRemark("现场复核");

        RecycleOrder wrongStatus = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(wrongStatus);
        assertThrows(BizException.class, () -> service.updateEstimate(updateBo));

        RecycleOrder order = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        order.setEstimateAmount(new BigDecimal("100.00"));
        order.setActualAmount(new BigDecimal("80.00"));
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        service.updateEstimate(updateBo);
        verify(iRecycleOrderService).updateById(argThat(updated ->
                new BigDecimal("88.00").compareTo(updated.getActualAmount()) == 0
                        && new BigDecimal("100.00").compareTo(updated.getEstimateAmount()) == 0));
        verify(iOrderSettlementService, never()).updateById(any());

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(5L);
        settlement.setOrderId(1L);
        settlement.setEstimateAmount(new BigDecimal("100.00"));
        settlement.setActualAmount(new BigDecimal("70.00"));
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);

        RecycleOrder withSettlement = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        withSettlement.setEstimateAmount(new BigDecimal("100.00"));
        when(iRecycleOrderService.getById(1L)).thenReturn(withSettlement);
        service.updateEstimate(updateBo);
        verify(iOrderSettlementService).updateById(argThat(updated ->
                new BigDecimal("88.00").compareTo(updated.getActualAmount()) == 0
                        && new BigDecimal("100.00").compareTo(updated.getEstimateAmount()) == 0));

        updateBo.setRemark(null);
        service.updateEstimate(updateBo);
    }

    @Test
    void confirmPayoutShouldResolveAmountAndStartPayout() {
        AdminOrderConfirmPayoutBo confirmBo = new AdminOrderConfirmPayoutBo();
        confirmBo.setId(1L);
        confirmBo.setRemark("确认金额");

        RecycleOrder wrongStatus = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(wrongStatus);
        assertThrows(BizException.class, () -> service.confirmPayout(confirmBo));

        RecycleOrder orderFromEstimate = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        orderFromEstimate.setEstimateAmount(new BigDecimal("50.00"));
        when(iRecycleOrderService.getById(1L)).thenReturn(orderFromEstimate);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        service.confirmPayout(confirmBo);
        verify(payoutService).startPayout(eq(orderFromEstimate), eq(new BigDecimal("50.00")), isNull());

        RecycleOrder orderFromActual = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        orderFromActual.setActualAmount(new BigDecimal("60.00"));
        when(iRecycleOrderService.getById(1L)).thenReturn(orderFromActual);
        service.confirmPayout(confirmBo);
        verify(payoutService).startPayout(eq(orderFromActual), eq(new BigDecimal("60.00")), isNull());

        RecycleOrder orderFromSettlementActual = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        OrderSettlement settlementActual = new OrderSettlement();
        settlementActual.setActualAmount(new BigDecimal("70.00"));
        when(iRecycleOrderService.getById(1L)).thenReturn(orderFromSettlementActual);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlementActual);
        service.confirmPayout(confirmBo);
        verify(payoutService).startPayout(eq(orderFromSettlementActual), eq(new BigDecimal("70.00")), isNull());

        RecycleOrder orderFromSettlementEstimate = order(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        orderFromSettlementEstimate.setEstimateAmount(new BigDecimal("55.00"));
        OrderSettlement settlementEstimate = new OrderSettlement();
        settlementEstimate.setEstimateAmount(new BigDecimal("55.00"));
        when(iRecycleOrderService.getById(1L)).thenReturn(orderFromSettlementEstimate);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlementEstimate);
        confirmBo.setRemark(null);
        service.confirmPayout(confirmBo);
        verify(payoutService).startPayout(eq(orderFromSettlementEstimate), eq(new BigDecimal("55.00")), isNull());
    }

    @Test
    void completeShouldValidateAndFinishPayout() {
        AdminOrderCompleteBo completeBo = new AdminOrderCompleteBo();
        completeBo.setId(1L);

        RecycleOrder wrongStatus = order(1L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(wrongStatus);
        assertThrows(BizException.class, () -> service.complete(completeBo));

        RecycleOrder processing = order(1L, RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        RecycleOrder completed = order(1L, RecycleOrderStatus.COMPLETED.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(processing, completed);

        completeBo.setRemark("到账确认");
        service.complete(completeBo);
        verify(payoutService).finishPayout(processing, null);
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));

        completeBo.setRemark(null);
        when(iRecycleOrderService.getById(1L)).thenReturn(processing, completed);
        service.complete(completeBo);
    }

    @Test
    void handleShouldValidateStatusAndResolvePaths() {
        AdminOrderHandleBo handleBo = new AdminOrderHandleBo();
        handleBo.setId(1L);

        RecycleOrder wrong = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(wrong);
        assertThrows(BizException.class, () -> service.handle(handleBo));

        handleBo.setHandleType("RESOLVED");
        handleBo.setRemark("继续服务");
        RecycleOrder exceptionOrder = order(1L, RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(exceptionOrder);
        service.handle(handleBo);
        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));

        handleBo.setHandleType("已处理");
        RecycleOrder disputeOrder = order(1L, RecycleOrderStatus.DISPUTE_PENDING.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(disputeOrder);
        OrderDispute dispute = new OrderDispute();
        dispute.setId(9L);
        dispute.setStatus("PENDING");
        when(iOrderDisputeService.getOne(any(LambdaQueryWrapper.class))).thenReturn(dispute);
        service.handle(handleBo);
        verify(iOrderDisputeService).updateById(any(OrderDispute.class));

        RecycleOrder disputeWithoutRecord = order(1L, RecycleOrderStatus.DISPUTE_PENDING.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(disputeWithoutRecord);
        when(iOrderDisputeService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        service.handle(handleBo);

        handleBo.setHandleType("UNRESOLVED");
        handleBo.setRemark(null);
        RecycleOrder unresolvedException = order(1L, RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(unresolvedException);
        service.handle(handleBo);

        RecycleOrder unresolvedDispute = order(1L, RecycleOrderStatus.DISPUTE_PENDING.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(unresolvedDispute);
        when(iOrderDisputeService.getOne(any(LambdaQueryWrapper.class))).thenReturn(dispute);
        service.handle(handleBo);

        handleBo.setHandleType("");
        RecycleOrder blankHandleType = order(1L, RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(blankHandleType);
        service.handle(handleBo);
    }

    @Test
    void assignPickupShouldSkipFinishWhenTaskAlreadyDone() {
        RecycleOrder order = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iUserService.getById(100L)).thenReturn(staff(100L, "员工E"));

        PickupTask doneTask = new PickupTask();
        doneTask.setId(5L);
        doneTask.setOrderId(1L);
        doneTask.setStatus(PickupTaskStatus.DONE.name());
        when(iPickupTaskService.getOne(any(LambdaQueryWrapper.class))).thenReturn(doneTask);

        AdminOrderAssignPickupBo assignBo = new AdminOrderAssignPickupBo();
        assignBo.setOrderId(1L);
        assignBo.setStaffId(100L);
        service.assignPickup(assignBo);

        verify(iPickupTaskService, times(1)).updateById(any(PickupTask.class));
        verify(iPickupTaskLogService, never()).save(argThat(log ->
                PickupTaskAction.FINISH.name().equals(log.getActionType())));
    }

    @Test
    void updateExpressNoShouldValidateMailOrderAndPersist() {
        AdminOrderUpdateExpressBo updateBo = new AdminOrderUpdateExpressBo();
        updateBo.setId(1L);
        updateBo.setExpressNo(" SF9988776655 ");

        RecycleOrder visitOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(visitOrder);
        assertThrows(BizException.class, () -> service.updateExpressNo(updateBo));

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF1122334455");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);

        service.updateExpressNo(updateBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals("SF9988776655", orderCaptor.getValue().getExpressNo());
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));

        updateBo.setExpressNo("SF9988776655");
        service.updateExpressNo(updateBo);
        verify(iRecycleOrderService, times(1)).updateById(any(RecycleOrder.class));

        updateBo.setExpressNo("  ");
        assertThrows(BizException.class, () -> service.updateExpressNo(updateBo));

        updateBo.setExpressNo("SF0000000001");
        mailOrder.setExpressNo("");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);
        service.updateExpressNo(updateBo);
        verify(iRecycleOrderLogService, atLeastOnce()).save(argThat(log ->
                log.getContent() != null && log.getContent().contains("设置快递单号")));
    }

    @Test
    void confirmMailReceiveShouldMoveToPendingConfirm() {
        AdminOrderConfirmMailReceiveBo confirmBo = new AdminOrderConfirmMailReceiveBo();
        confirmBo.setId(1L);

        RecycleOrder visitOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(visitOrder);
        assertThrows(BizException.class, () -> service.confirmMailReceive(confirmBo));

        RecycleOrder mailNoExpress = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailNoExpress.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailNoExpress.setExpressNo("");
        mailNoExpress.setStatusDesc("待收货");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailNoExpress);
        assertThrows(BizException.class, () -> service.confirmMailReceive(confirmBo));

        RecycleOrder waitShip = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        waitShip.setFulfillType(RecycleFulfillType.MAIL.getCode());
        waitShip.setExpressNo("SF123");
        waitShip.setStatusDesc("待顺丰寄件取件");
        when(iRecycleOrderService.getById(1L)).thenReturn(waitShip);
        assertThrows(BizException.class, () -> service.confirmMailReceive(confirmBo));

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF1234567890");
        mailOrder.setStatusDesc("待收货");
        mailOrder.setEstimateAmount(new BigDecimal("100.00"));
        mailOrder.setUserId(9L);
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);
        when(iOrderSettlementService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        service.confirmMailReceive(confirmBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService, atLeastOnce()).updateById(orderCaptor.capture());
        RecycleOrder updated = orderCaptor.getAllValues().get(0);
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode(), updated.getStatus());
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getLabel(), updated.getStatusDesc());
        assertNull(updated.getActualAmount());
        verify(iOrderSettlementService).save(argThat(settlement ->
                settlement.getActualAmount() == null
                        && new BigDecimal("100.00").compareTo(settlement.getEstimateAmount()) == 0));
        verify(iRecycleOrderLogService).save(argThat(log ->
                log.getContent() != null && log.getContent().contains("确认收货")));
    }

    @Test
    void listExpressTracksShouldValidateAndQueryByExpressNo() {
        AdminOrderInfoBo infoBo = new AdminOrderInfoBo();
        infoBo.setId(1L);

        RecycleOrder visitOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(visitOrder);
        assertThrows(BizException.class, () -> service.listExpressTracks(infoBo));

        RecycleOrder mailNoExpress = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailNoExpress.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailNoExpress.setExpressNo("");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailNoExpress);
        assertThrows(BizException.class, () -> service.listExpressTracks(infoBo));

        RecycleOrder mailOrder = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        mailOrder.setContactMobile("15200000002");
        when(iRecycleOrderService.getById(1L)).thenReturn(mailOrder);
        when(aliyunLogisticsService.listExpressTracks(eq("SF0217121758386"), any(), any()))
                .thenReturn(Collections.emptyList());

        try (MockedStatic<com.sqx.common.redis.utils.ConfigUtil> config =
                     mockStatic(com.sqx.common.redis.utils.ConfigUtil.class)) {
            config.when(() -> com.sqx.common.redis.utils.ConfigUtil.getConfigValueOrDefault(anyString()))
                    .thenReturn("4008006688");
            assertTrue(service.listExpressTracks(infoBo).isEmpty());
            verify(aliyunLogisticsService).listExpressTracks(eq("SF0217121758386"), any(), any());
        }
    }

    @Test
    void pageInfoShouldQueryWithoutStatusKeyFilter() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        Page<RecycleOrder> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iRecycleOrderService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        AdminOrderPageBo pageBo = pageBo();
        pageBo.setStatusKey(null);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldReturnEmptyExceptionsWhenOrderIdNull() {
        AdminOrderInfoBo infoBo = new AdminOrderInfoBo();
        infoBo.setId(1L);

        RecycleOrder order = order(1L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setId(null);
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(adminOrderRescheduleManageService.listByOrderId(null)).thenReturn(Collections.emptyList());

        assertTrue(service.info(infoBo).getExceptions().isEmpty());
    }

    @Test
    void finishPickupTaskShouldReturnEarlyWhenTaskNull() throws Exception {
        java.lang.reflect.Method method = AdminOrderManageServiceImpl.class
                .getDeclaredMethod("finishPickupTask", PickupTask.class);
        method.setAccessible(true);
        method.invoke(service, new Object[]{null});

        verify(iPickupTaskService, never()).updateById(any(PickupTask.class));
        verify(iPickupTaskLogService, never()).save(any(PickupTaskLog.class));
    }

    @Test
    void fillLatestUserFeedbackShouldReturnEarlyOnEmptyList() throws Exception {
        java.lang.reflect.Method method = AdminOrderManageServiceImpl.class
                .getDeclaredMethod("fillLatestUserFeedback", java.util.List.class);
        method.setAccessible(true);
        method.invoke(service, Collections.emptyList());
        verify(iOrderDisputeService, never()).list(any(LambdaQueryWrapper.class));
    }

    @Test
    void joinFeedbackPartsShouldReturnNullWhenAllBlank() throws Exception {
        java.lang.reflect.Method method = AdminOrderManageServiceImpl.class
                .getDeclaredMethod("joinFeedbackParts", String.class, String[].class);
        method.setAccessible(true);
        Object result = method.invoke(service, "争议", new String[]{"", " ", null});
        assertNull(result);
    }

    private AdminOrderPageBo pageBo() {
        AdminOrderPageBo pageBo = new AdminOrderPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        return pageBo;
    }

    private RecycleOrder order(Long id, String status) {
        RecycleOrder order = new RecycleOrder();
        order.setId(id);
        order.setOrderNo("RO" + id);
        order.setUserId(100L);
        order.setStatus(status);
        order.setStatusDesc(status);
        order.setFulfillType(RecycleFulfillType.VISIT.getCode());
        order.setEstimateAmount(new BigDecimal("100.00"));
        return order;
    }

    private UserEntity staff(Long staffId, String name) {
        UserEntity staff = new UserEntity();
        staff.setUserId(staffId);
        staff.setUserName(name);
        staff.setUserType(3);
        return staff;
    }
}
