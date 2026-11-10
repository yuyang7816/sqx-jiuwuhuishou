package com.sqx.app.pickup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.express.sf.SfExpressOrderService;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.service.AppointmentOptionService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.bo.PickupTaskCancelBo;
import com.sqx.app.pickup.domain.bo.PickupTaskFinishBo;
import com.sqx.app.pickup.domain.bo.PickupTaskInfoBo;
import com.sqx.app.pickup.domain.bo.PickupTaskPageBo;
import com.sqx.app.pickup.domain.bo.PickupTaskStartBo;
import com.sqx.app.pickup.domain.bo.PickupTaskUpdateActualBo;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.app.pickup.domain.vo.PickupTaskDetailVo;
import com.sqx.app.pickup.domain.vo.PickupTaskFinishVo;
import com.sqx.app.pickup.domain.vo.PickupTaskStatsVo;
import com.sqx.app.pickup.domain.vo.PickupTaskVo;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PickupTaskServiceTest {

    @Mock
    private IPickupTaskService iPickupTaskService;
    @Mock
    private IPickupTaskLogService iPickupTaskLogService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private IUserService iUserService;
    @Mock
    private SfExpressOrderService sfExpressOrderService;
    @Mock
    private AppointmentOptionService appointmentOptionService;

    @InjectMocks
    private PickupTaskServiceImpl pickupTaskService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(PickupTask.class, PickupTaskLog.class, RecycleOrder.class,
                RecycleOrderLog.class, OrderSettlement.class, UserEntity.class);
    }

    @Test
    void statsShouldReturnCountsForStaff() {
        stubStaff(10L);
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(1L, 2L, 3L);

        PickupTaskStatsVo vo = pickupTaskService.stats(10L);
        assertEquals(1L, vo.getPendingCount());
        assertEquals(2L, vo.getRunningCount());
        assertEquals(3L, vo.getDoneCount());
    }

    @Test
    void statsShouldRejectNonStaff() {
        when(iUserService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> pickupTaskService.stats(1L));

        UserEntity user = new UserEntity();
        user.setUserId(2L);
        user.setUserType(1);
        when(iUserService.getById(2L)).thenReturn(user);
        assertThrows(BizException.class, () -> pickupTaskService.stats(2L));
    }

    @Test
    void pageShouldFilterAndMap() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.PENDING.name());
        task.setStatus("UNKNOWN");
        Page<PickupTask> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(task));
        when(iPickupTaskService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        PickupTaskPageBo bo = new PickupTaskPageBo();
        bo.setStaffId(10L);
        bo.setStatusKey("pending");
        bo.setPage(1L);
        bo.setLimit(10L);
        IPage<PickupTaskVo> result = pickupTaskService.page(bo);
        assertEquals(1, result.getRecords().size());

        page.setRecords(Collections.emptyList());
        bo.setStatusKey("all");
        assertTrue(pickupTaskService.page(bo).getRecords().isEmpty());
    }

    @Test
    void infoShouldBuildDetailWithLogs() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);
        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setEstimateAmount(new BigDecimal("128.00"));
        order.setActualAmount(new BigDecimal("110.50"));
        when(iRecycleOrderService.getById(100L)).thenReturn(order);

        PickupTaskLog blank = log(1L, null, "x");
        PickupTaskLog bad = log(1L, "NOPE", "y");
        PickupTaskLog finish = log(1L, PickupTaskAction.FINISH.name(), "z");
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(blank, bad, finish));

        PickupTaskInfoBo bo = new PickupTaskInfoBo();
        bo.setId(1L);
        PickupTaskDetailVo detail = pickupTaskService.info(10L, bo);
        assertEquals("RE100", detail.getRecycleOrderNo());
        assertEquals(new BigDecimal("128.00"), detail.getEstimateAmount());
        assertEquals(new BigDecimal("110.50"), detail.getActualAmount());
        assertTrue(detail.getCanEditActual());
        assertEquals(3, detail.getLogs().size());
        assertTrue(detail.getCanFinish());
        assertFalse(detail.getCanStart());
    }

    @Test
    void updateActualShouldUpdateOrderAndSettlement() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setEstimateAmount(new BigDecimal("100.00"));
        when(iRecycleOrderService.getById(100L)).thenReturn(order);

        OrderSettlement settlement = new OrderSettlement();
        settlement.setId(9L);
        settlement.setOrderId(100L);
        settlement.setActualAmount(new BigDecimal("100.00"));
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(settlement);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        PickupTaskUpdateActualBo updateBo = new PickupTaskUpdateActualBo();
        updateBo.setId(1L);
        updateBo.setActualAmount(new BigDecimal("88.80"));
        updateBo.setRemark("现场议价");

        PickupTaskDetailVo detail = pickupTaskService.updateActual(10L, updateBo);
        assertEquals(new BigDecimal("88.80"), detail.getActualAmount());
        assertEquals("现场议价", detail.getActualAmountRemark());
        assertEquals(new BigDecimal("88.80"), order.getActualAmount());
        assertEquals("现场议价", order.getActualAmountRemark());
        assertEquals(new BigDecimal("88.80"), settlement.getActualAmount());
        verify(iRecycleOrderService).updateById(order);
        verify(iOrderSettlementService).updateById(settlement);
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
        verify(iPickupTaskLogService).save(any(PickupTaskLog.class));
    }

    @Test
    void updateActualShouldRequireRemarkWhenAmountChanged() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setEstimateAmount(new BigDecimal("100.00"));
        order.setActualAmount(new BigDecimal("100.00"));
        when(iRecycleOrderService.getById(100L)).thenReturn(order);

        PickupTaskUpdateActualBo updateBo = new PickupTaskUpdateActualBo();
        updateBo.setId(1L);
        updateBo.setActualAmount(new BigDecimal("80.00"));
        assertThrows(BizException.class, () -> pickupTaskService.updateActual(10L, updateBo));
    }

    @Test
    void updateActualShouldAllowFirstFillWithoutRemark() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setEstimateAmount(new BigDecimal("100.00"));
        order.setActualAmount(null);
        when(iRecycleOrderService.getById(100L)).thenReturn(order);
        when(iOrderSettlementService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        PickupTaskUpdateActualBo updateBo = new PickupTaskUpdateActualBo();
        updateBo.setId(1L);
        updateBo.setActualAmount(new BigDecimal("88.00"));

        PickupTaskDetailVo detail = pickupTaskService.updateActual(10L, updateBo);
        assertEquals(new BigDecimal("88.00"), detail.getActualAmount());
        assertEquals(new BigDecimal("88.00"), order.getActualAmount());
    }

    @Test
    void finishShouldRejectWhenActualAmountMissing() {
        stubStaff(10L);
        PickupTask task = buildTask(4L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(4L)).thenReturn(task);
        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setEstimateAmount(new BigDecimal("100.00"));
        order.setActualAmount(null);
        when(iRecycleOrderService.getById(100L)).thenReturn(order);

        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(4L);
        BizException ex = assertThrows(BizException.class, () -> pickupTaskService.finish(10L, finishBo));
        assertTrue(ex.getMessage().contains("实际成交金额"));
    }

    @Test
    void cancelShouldCancelOrderAndTask() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(100L)).thenReturn(order);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        PickupTaskCancelBo cancelBo = new PickupTaskCancelBo();
        cancelBo.setId(1L);
        cancelBo.setReason("用户临时有事");

        PickupTaskDetailVo detail = pickupTaskService.cancel(10L, cancelBo);
        assertEquals("cancelled", detail.getStatusKey());
        assertEquals(RecycleOrderStatus.CANCELLED.getCode(), order.getStatus());
        assertEquals(PickupTaskStatus.CANCELLED.name(), task.getStatus());
        assertFalse(detail.getCanCancel());
        verify(iRecycleOrderService).updateById(order);
        verify(iPickupTaskService).updateById(task);
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
        verify(iPickupTaskLogService).save(any(PickupTaskLog.class));
    }

    @Test
    void updateActualShouldRejectWhenOrderNotEditable() {
        stubStaff(10L);
        PickupTask task = buildTask(1L, 10L, PickupTaskStatus.DONE.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iRecycleOrderService.getById(100L)).thenReturn(order);

        PickupTaskUpdateActualBo updateBo = new PickupTaskUpdateActualBo();
        updateBo.setId(1L);
        updateBo.setActualAmount(new BigDecimal("50"));
        assertThrows(BizException.class, () -> pickupTaskService.updateActual(10L, updateBo));
    }

    @Test
    void infoShouldHandleMissingOwnershipAndEmptyLogs() {
        stubStaff(10L);
        when(iPickupTaskService.getById(99L)).thenReturn(null);
        PickupTaskInfoBo bo = new PickupTaskInfoBo();
        bo.setId(99L);
        assertThrows(BizException.class, () -> pickupTaskService.info(10L, bo));

        PickupTask other = buildTask(2L, 99L, PickupTaskStatus.PENDING.name());
        when(iPickupTaskService.getById(2L)).thenReturn(other);
        bo.setId(2L);
        assertThrows(BizException.class, () -> pickupTaskService.info(10L, bo));

        PickupTask task = buildTask(3L, 10L, PickupTaskStatus.DONE.name());
        task.setOrderId(null);
        when(iPickupTaskService.getById(3L)).thenReturn(task);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        bo.setId(3L);
        PickupTaskDetailVo detail = pickupTaskService.info(10L, bo);
        assertTrue(detail.getLogs().isEmpty());
        assertFalse(detail.getCanStart());
        assertFalse(detail.getCanFinish());
    }

    @Test
    void startShouldFailWhenNotPendingAndSucceedOtherwise() {
        stubStaff(10L);
        PickupTask running = buildTask(1L, 10L, PickupTaskStatus.RUNNING.name());
        when(iPickupTaskService.getById(1L)).thenReturn(running);
        PickupTaskStartBo startBo = new PickupTaskStartBo();
        startBo.setId(1L);
        assertThrows(BizException.class, () -> pickupTaskService.start(10L, startBo));

        PickupTask pending = buildTask(2L, 10L, PickupTaskStatus.PENDING.name());
        pending.setOrderId(100L);
        when(iPickupTaskService.getById(2L)).thenReturn(pending);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        when(iRecycleOrderService.getById(100L)).thenReturn(order);
        startBo.setId(2L);
        PickupTaskDetailVo vo = pickupTaskService.start(10L, startBo);
        assertEquals(PickupTaskStatus.RUNNING.getKey(), vo.getStatusKey());
        verify(iRecycleOrderService).updateById(any(RecycleOrder.class));
        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
    }

    @Test
    void startShouldSkipOrderAdvanceWhenNotWaitAssign() {
        stubStaff(10L);
        PickupTask pending = buildTask(2L, 10L, PickupTaskStatus.PENDING.name());
        pending.setOrderId(100L);
        when(iPickupTaskService.getById(2L)).thenReturn(pending);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iRecycleOrderService.getById(100L)).thenReturn(buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode()));

        PickupTaskStartBo startBo = new PickupTaskStartBo();
        startBo.setId(2L);
        pickupTaskService.start(10L, startBo);
        verify(iRecycleOrderService, never()).updateById(any(RecycleOrder.class));
    }

    @Test
    void startShouldSkipOrderAdvanceWhenOrderMissing() {
        stubStaff(10L);
        PickupTask pending = buildTask(2L, 10L, PickupTaskStatus.PENDING.name());
        pending.setOrderId(100L);
        when(iPickupTaskService.getById(2L)).thenReturn(pending);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(iRecycleOrderService.getById(100L)).thenReturn(null);

        PickupTaskStartBo startBo = new PickupTaskStartBo();
        startBo.setId(2L);
        pickupTaskService.start(10L, startBo);
        verify(iRecycleOrderLogService, never()).save(any());
    }

    @Test
    void startShouldSkipOrderAdvanceWhenOrderIdNull() {
        stubStaff(10L);
        PickupTask pending = buildTask(22L, 10L, PickupTaskStatus.PENDING.name());
        pending.setOrderId(null);
        when(iPickupTaskService.getById(22L)).thenReturn(pending);
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        PickupTaskStartBo startBo = new PickupTaskStartBo();
        startBo.setId(22L);
        pickupTaskService.start(10L, startBo);
        verify(iRecycleOrderService, never()).getById(any());
    }

    @Test
    void finishShouldRejectDoneAndCancelled() {
        stubStaff(10L);
        when(iPickupTaskService.getById(1L)).thenReturn(buildTask(1L, 10L, PickupTaskStatus.DONE.name()));
        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(1L);
        assertThrows(BizException.class, () -> pickupTaskService.finish(10L, finishBo));

        when(iPickupTaskService.getById(2L)).thenReturn(buildTask(2L, 10L, PickupTaskStatus.CANCELLED.name()));
        finishBo.setId(2L);
        assertThrows(BizException.class, () -> pickupTaskService.finish(10L, finishBo));
    }

    @Test
    void finishFromPendingShouldCreateSettlement() {
        stubStaff(10L);
        PickupTask task = buildTask(3L, 10L, PickupTaskStatus.PENDING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(3L)).thenReturn(task);
        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        order.setUserId(88L);
        order.setEstimateAmount(new BigDecimal("12.5"));
        order.setActualAmount(new BigDecimal("12.5"));
        when(iRecycleOrderService.getById(100L)).thenReturn(order);
        when(iOrderSettlementService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(3L);
        finishBo.setRemark("已取");
        PickupTaskFinishVo result = pickupTaskService.finish(10L, finishBo);

        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey(), result.getOrderStatusKey());
        assertTrue(result.getMessage().contains("待确认"));
        verify(iOrderSettlementService).save(any(OrderSettlement.class));
        verify(iPickupTaskLogService, atLeast(2)).save(any(PickupTaskLog.class));
    }

    @Test
    void finishFromRunningShouldReuseExistingSettlement() {
        stubStaff(10L);
        PickupTask task = buildTask(4L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(4L)).thenReturn(task);
        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setActualAmount(new BigDecimal("90.00"));
        when(iRecycleOrderService.getById(100L)).thenReturn(order);
        when(iOrderSettlementService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);

        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(4L);
        PickupTaskFinishVo result = pickupTaskService.finish(10L, finishBo);
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey(), result.getOrderStatusKey());
        verify(iOrderSettlementService, never()).save(any(OrderSettlement.class));
    }

    @Test
    void finishFromRunningShouldTransitionWaitAssignInsideCompleteLinkedOrder() {
        stubStaff(10L);
        PickupTask task = buildTask(41L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(41L)).thenReturn(task);
        RecycleOrder order = buildOrder(100L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        order.setUserId(88L);
        order.setEstimateAmount(new BigDecimal("3"));
        order.setActualAmount(new BigDecimal("3"));
        when(iRecycleOrderService.getById(100L)).thenReturn(order);
        when(iOrderSettlementService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(41L);
        PickupTaskFinishVo result = pickupTaskService.finish(10L, finishBo);
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey(), result.getOrderStatusKey());
        verify(iRecycleOrderService, atLeast(2)).updateById(any(RecycleOrder.class));
    }

    @Test
    void finishShouldHandleAlreadyWaitSettlementAndOtherStatuses() {
        stubStaff(10L);
        PickupTask taskA = buildTask(5L, 10L, PickupTaskStatus.RUNNING.name());
        taskA.setOrderId(100L);
        when(iPickupTaskService.getById(5L)).thenReturn(taskA);
        RecycleOrder waitConfirm = buildOrder(100L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        waitConfirm.setActualAmount(new BigDecimal("10"));
        when(iRecycleOrderService.getById(100L)).thenReturn(waitConfirm);

        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(5L);
        assertEquals(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey(),
                pickupTaskService.finish(10L, finishBo).getOrderStatusKey());

        PickupTask taskB = buildTask(51L, 10L, PickupTaskStatus.RUNNING.name());
        taskB.setOrderId(100L);
        when(iPickupTaskService.getById(51L)).thenReturn(taskB);
        RecycleOrder completed = buildOrder(100L, RecycleOrderStatus.COMPLETED.getCode());
        completed.setActualAmount(new BigDecimal("10"));
        when(iRecycleOrderService.getById(100L)).thenReturn(completed);
        finishBo.setId(51L);
        assertEquals(RecycleOrderStatus.COMPLETED.getStatusKey(),
                pickupTaskService.finish(10L, finishBo).getOrderStatusKey());
    }

    @Test
    void finishShouldHandleMissingOrderLink() {
        stubStaff(10L);
        PickupTask task = buildTask(6L, 10L, PickupTaskStatus.RUNNING.name());
        task.setOrderId(null);
        when(iPickupTaskService.getById(6L)).thenReturn(task);

        PickupTaskFinishBo finishBo = new PickupTaskFinishBo();
        finishBo.setId(6L);
        PickupTaskFinishVo vo = pickupTaskService.finish(10L, finishBo);
        assertNull(vo.getOrderStatusKey());
        assertEquals("已完成取件", vo.getMessage());

        PickupTask task2 = buildTask(61L, 10L, PickupTaskStatus.RUNNING.name());
        task2.setOrderId(100L);
        when(iPickupTaskService.getById(61L)).thenReturn(task2);
        when(iRecycleOrderService.getById(100L)).thenReturn(null);
        finishBo.setId(61L);
        assertNull(pickupTaskService.finish(10L, finishBo).getOrderStatusKey());
    }

    @Test
    void infoShouldMarkFinishLogActiveWhenDone() {
        stubStaff(10L);
        PickupTask task = buildTask(7L, 10L, PickupTaskStatus.DONE.name());
        task.setOrderId(100L);
        when(iPickupTaskService.getById(7L)).thenReturn(task);
        when(iRecycleOrderService.getById(100L)).thenReturn(buildOrder(100L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode()));
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(log(7L, PickupTaskAction.FINISH.name(), "ok")));

        PickupTaskInfoBo bo = new PickupTaskInfoBo();
        bo.setId(7L);
        PickupTaskDetailVo detail = pickupTaskService.info(10L, bo);
        assertTrue(detail.getLogs().get(0).getActive());
    }

    private void stubStaff(Long staffId) {
        UserEntity staff = new UserEntity();
        staff.setUserId(staffId);
        staff.setUserType(3);
        when(iUserService.getById(staffId)).thenReturn(staff);
    }

    private PickupTask buildTask(Long id, Long staffId, String status) {
        PickupTask task = new PickupTask();
        task.setId(id);
        task.setStaffId(staffId);
        task.setStatus(status);
        task.setAppointmentTime(LocalDateTime.now());
        task.setCreateTime(LocalDateTime.now());
        return task;
    }

    private RecycleOrder buildOrder(Long id, String status) {
        RecycleOrder order = new RecycleOrder();
        order.setId(id);
        order.setOrderNo("RE" + id);
        order.setStatus(status);
        return order;
    }

    private PickupTaskLog log(Long taskId, String action, String content) {
        PickupTaskLog log = new PickupTaskLog();
        log.setTaskId(taskId);
        log.setActionType(action);
        log.setContent(content);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }
}
