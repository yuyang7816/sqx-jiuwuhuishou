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
import com.sqx.app.order.enums.RecycleFulfillType;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.service.AppointmentOptionService;
import com.sqx.app.order.support.RecycleOrderStateMachine;
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
import com.sqx.app.pickup.domain.vo.PickupTaskLogVo;
import com.sqx.app.pickup.domain.vo.PickupTaskStatsVo;
import com.sqx.app.pickup.domain.vo.PickupTaskVo;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.pickup.service.PickupTaskService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.GenUtil;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class PickupTaskServiceImpl implements PickupTaskService {

    private static final Integer STAFF_USER_TYPE = 3;
    private static final String OPERATOR_TYPE_STAFF = "STAFF";
    private static final String ACTION_PICKUP_START = "PICKUP_START";
    private static final String ACTION_PICKUP_FINISH = "PICKUP_FINISH";
    private static final String ACTION_ACTUAL_UPDATE = "ACTUAL_UPDATE";
    private static final String ACTION_CANCEL = "CANCEL";
    private static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    private static final Set<String> EDITABLE_ACTUAL_ORDER_STATUS = new HashSet<>(Arrays.asList(
            RecycleOrderStatus.WAIT_ASSIGN.getCode(),
            RecycleOrderStatus.IN_PROGRESS.getCode(),
            RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode()
    ));

    @Resource
    private IPickupTaskService iPickupTaskService;

    @Resource
    private IPickupTaskLogService iPickupTaskLogService;

    @Resource
    private IRecycleOrderService iRecycleOrderService;

    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;

    @Resource
    private IOrderSettlementService iOrderSettlementService;

    @Resource
    private IUserService iUserService;

    @Resource
    private SfExpressOrderService sfExpressOrderService;

    @Resource
    private AppointmentOptionService appointmentOptionService;

    @Override
    public PickupTaskStatsVo stats(Long staffId) {
        assertStaff(staffId);
        return new PickupTaskStatsVo(
                countByStatus(staffId, PickupTaskStatus.PENDING),
                countByStatus(staffId, PickupTaskStatus.RUNNING),
                countByStatus(staffId, PickupTaskStatus.DONE)
        );
    }

    @Override
    public IPage<PickupTaskVo> page(PickupTaskPageBo pageBo) {
        assertStaff(pageBo.getStaffId());
        Page<PickupTask> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        LambdaQueryWrapper<PickupTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PickupTask::getStaffId, pageBo.getStaffId());

        PickupTaskStatus status = PickupTaskStatus.fromKey(pageBo.getStatusKey());
        if (Objects.nonNull(status)) {
            queryWrapper.eq(PickupTask::getStatus, status.name());
        }

        queryWrapper.orderByDesc(PickupTask::getAppointmentTime)
                .orderByDesc(PickupTask::getCreateTime);

        IPage<PickupTask> iPage = iPickupTaskService.page(initPage, queryWrapper);
        List<PickupTaskVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (PickupTask record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public PickupTaskDetailVo info(Long staffId, PickupTaskInfoBo infoBo) {
        assertStaff(staffId);
        PickupTask task = getOwnedTask(staffId, infoBo.getId());
        return buildDetailVo(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PickupTaskDetailVo start(Long staffId, PickupTaskStartBo startBo) {
        assertStaff(staffId);
        PickupTask task = getOwnedTask(staffId, startBo.getId());
        PickupTaskStatus status = PickupTaskStatus.fromName(task.getStatus());
        if (!PickupTaskStatus.PENDING.equals(status)) {
            throw new BizException("当前任务状态不允许开始上门");
        }

        task.setStatus(PickupTaskStatus.RUNNING.name());
        task.setUpdateTime(LocalDateTime.now());
        iPickupTaskService.updateById(task);

        appendTaskLog(task.getId(), PickupTaskAction.START, PickupTaskAction.START.getDefaultContent(), staffId);
        advanceOrderToInProgress(task.getOrderId(), staffId);

        return buildDetailVo(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PickupTaskFinishVo finish(Long staffId, PickupTaskFinishBo finishBo) {
        assertStaff(staffId);
        PickupTask task = getOwnedTask(staffId, finishBo.getId());
        PickupTaskStatus status = PickupTaskStatus.fromName(task.getStatus());
        if (PickupTaskStatus.DONE.equals(status)) {
            throw new BizException("任务已完成，请勿重复操作");
        }
        if (PickupTaskStatus.CANCELLED.equals(status)) {
            throw new BizException("任务已取消，无法完成取件");
        }
        assertActualAmountFilled(task);

        if (PickupTaskStatus.PENDING.equals(status)) {
            task.setStatus(PickupTaskStatus.RUNNING.name());
            appendTaskLog(task.getId(), PickupTaskAction.START, "员工直接完成上门取件", staffId);
            advanceOrderToInProgress(task.getOrderId(), staffId);
        }

        task.setStatus(PickupTaskStatus.DONE.name());
        task.setFinishedAt(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        iPickupTaskService.updateById(task);

        String finishContent = StringUtils.defaultIfBlank(finishBo.getRemark(), PickupTaskAction.FINISH.getDefaultContent());
        appendTaskLog(task.getId(), PickupTaskAction.FINISH, finishContent, staffId);

        String orderStatusKey = completeLinkedOrder(task, staffId);

        PickupTaskFinishVo result = new PickupTaskFinishVo();
        result.setTaskId(task.getId());
        result.setOrderId(task.getOrderId());
        result.setOrderStatusKey(orderStatusKey);
        result.setMessage(StringUtils.isNotBlank(orderStatusKey)
                ? "已完成取件，用户订单已进入待确认"
                : "已完成取件");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PickupTaskDetailVo updateActual(Long staffId, PickupTaskUpdateActualBo updateBo) {
        assertStaff(staffId);
        PickupTask task = getOwnedTask(staffId, updateBo.getId());
        if (PickupTaskStatus.CANCELLED.equals(PickupTaskStatus.fromName(task.getStatus()))) {
            throw new BizException("任务已取消，无法修改实际打款金额");
        }
        if (Objects.isNull(task.getOrderId())) {
            throw new BizException("任务未关联订单，无法修改实际打款金额");
        }

        RecycleOrder order = iRecycleOrderService.getById(task.getOrderId());
        if (Objects.isNull(order)) {
            throw new BizException("关联订单不存在");
        }
        if (!canEditActualAmount(order)) {
            throw new BizException("当前订单状态不允许修改实际打款金额");
        }

        BigDecimal previousAmount = order.getActualAmount();
        boolean amountChanged = previousAmount == null
                || previousAmount.compareTo(updateBo.getActualAmount()) != 0;
        String remark = StringUtils.trimToEmpty(updateBo.getRemark());
        // 首次填写不强制说明；仅在已有实际金额后再次修改时要求填写说明
        if (previousAmount != null && amountChanged && StringUtils.isBlank(remark)) {
            throw new BizException("修改金额时请填写修改说明");
        }

        order.setActualAmount(updateBo.getActualAmount());
        if (amountChanged) {
            order.setActualAmountRemark(remark);
        } else if (StringUtils.isNotBlank(remark)) {
            order.setActualAmountRemark(remark);
        }
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        OrderSettlement settlement = iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, order.getId())
                .orderByDesc(OrderSettlement::getId)
                .last("limit 1"));
        if (Objects.nonNull(settlement)) {
            settlement.setActualAmount(updateBo.getActualAmount());
            settlement.setUpdateTime(LocalDateTime.now());
            iOrderSettlementService.updateById(settlement);
        }

        String content = "员工修改实际打款金额为 " + updateBo.getActualAmount();
        if (StringUtils.isNotBlank(remark)) {
            content = content + "，修改说明：" + remark;
        }
        appendOrderLog(order.getId(), ACTION_ACTUAL_UPDATE, order.getStatus(), order.getStatus(), content, staffId);
        appendTaskLog(task.getId(), PickupTaskAction.UPDATE_ACTUAL, content, staffId);

        return buildDetailVo(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PickupTaskDetailVo cancel(Long staffId, PickupTaskCancelBo cancelBo) {
        assertStaff(staffId);
        PickupTask task = getOwnedTask(staffId, cancelBo.getId());
        PickupTaskStatus taskStatus = PickupTaskStatus.fromName(task.getStatus());
        if (PickupTaskStatus.CANCELLED.equals(taskStatus)) {
            throw new BizException("任务已取消，请勿重复操作");
        }
        if (Objects.isNull(task.getOrderId())) {
            throw new BizException("任务未关联订单，无法取消");
        }

        RecycleOrder order = iRecycleOrderService.getById(task.getOrderId());
        if (Objects.isNull(order)) {
            throw new BizException("关联订单不存在");
        }
        if (!canCancelOrder(order)) {
            throw new BizException("当前订单状态不允许取消");
        }

        String fromStatus = order.getStatus();
        if (RecycleFulfillType.of(order.getFulfillType()).isMail() && StringUtils.isNotBlank(order.getExpressNo())) {
            sfExpressOrderService.cancelMailOrder(order);
        }
        order.setStatus(RecycleOrderStatus.CANCELLED.getCode());
        order.setStatusDesc(RecycleOrderStatus.CANCELLED.getLabel());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        task.setStatus(PickupTaskStatus.CANCELLED.name());
        task.setUpdateTime(LocalDateTime.now());
        iPickupTaskService.updateById(task);

        String content = "员工取消订单";
        if (StringUtils.isNotBlank(cancelBo.getReason())) {
            content = content + "：" + cancelBo.getReason();
        }
        appendOrderLog(order.getId(), ACTION_CANCEL, fromStatus, order.getStatus(), content, staffId);
        appendTaskLog(task.getId(), PickupTaskAction.CANCEL, content, staffId);

        return buildDetailVo(task);
    }

    private void assertStaff(Long staffId) {
        UserEntity user = iUserService.getById(staffId);
        if (Objects.isNull(user) || !STAFF_USER_TYPE.equals(user.getUserType())) {
            throw new BizException("仅取件员工可访问取件任务");
        }
    }

    private PickupTask getOwnedTask(Long staffId, Long taskId) {
        PickupTask task = iPickupTaskService.getById(taskId);
        if (Objects.isNull(task)) {
            throw new BizException("取件任务不存在");
        }
        if (!Objects.equals(task.getStaffId(), staffId)) {
            throw new BizException("无权操作该取件任务");
        }
        return task;
    }

    private long countByStatus(Long staffId, PickupTaskStatus status) {
        return iPickupTaskService.count(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getStaffId, staffId)
                .eq(PickupTask::getStatus, status.name()));
    }

    private PickupTaskVo toVo(PickupTask record) {
        PickupTaskVo vo = new PickupTaskVo();
        BeanUtils.copyProperties(record, vo);

        PickupTaskStatus status = PickupTaskStatus.fromName(record.getStatus());
        if (Objects.nonNull(status)) {
            vo.setStatusKey(status.getKey());
            vo.setStatusText(status.getLabel());
        }
        vo.setAppointmentEndTime(appointmentOptionService.resolveSlotEndTime(record.getAppointmentTime()));
        return vo;
    }

    private PickupTaskDetailVo buildDetailVo(PickupTask task) {
        PickupTaskDetailVo detailVo = new PickupTaskDetailVo();
        BeanUtils.copyProperties(toVo(task), detailVo);

        PickupTaskStatus status = PickupTaskStatus.fromName(task.getStatus());
        detailVo.setCanStart(PickupTaskStatus.PENDING.equals(status));
        detailVo.setCanFinish(PickupTaskStatus.PENDING.equals(status) || PickupTaskStatus.RUNNING.equals(status));
        detailVo.setCanEditActual(Boolean.FALSE);
        detailVo.setCanCancel(Boolean.FALSE);

        if (Objects.nonNull(task.getOrderId())) {
            RecycleOrder order = iRecycleOrderService.getById(task.getOrderId());
            if (Objects.nonNull(order)) {
                detailVo.setRecycleOrderNo(order.getOrderNo());
                detailVo.setEstimateAmount(order.getEstimateAmount());
                detailVo.setEstimateMinAmount(order.getEstimateMinAmount());
                detailVo.setEstimateMaxAmount(order.getEstimateMaxAmount());
                // 取件中未填写时返回空，不做预估金额默认展示
                detailVo.setActualAmount(order.getActualAmount());
                detailVo.setActualAmountRemark(StringUtils.trimToNull(order.getActualAmountRemark()));
                detailVo.setCanEditActual(!PickupTaskStatus.CANCELLED.equals(status) && canEditActualAmount(order));
                detailVo.setCanCancel(!PickupTaskStatus.CANCELLED.equals(status) && canCancelOrder(order));
            }
        }

        List<PickupTaskLog> logs = iPickupTaskLogService.list(new LambdaQueryWrapper<PickupTaskLog>()
                .eq(PickupTaskLog::getTaskId, task.getId())
                .orderByAsc(PickupTaskLog::getCreateTime));
        detailVo.setLogs(buildLogVos(logs, status));
        return detailVo;
    }

    private boolean canEditActualAmount(RecycleOrder order) {
        if (Objects.isNull(order) || !EDITABLE_ACTUAL_ORDER_STATUS.contains(order.getStatus())) {
            return false;
        }
        return !Integer.valueOf(1).equals(order.getEstimateAmountConfirmed());
    }

    private boolean canCancelOrder(RecycleOrder order) {
        if (Objects.isNull(order)) {
            return false;
        }
        if (RecycleOrderStatus.CANCELLED.getCode().equals(order.getStatus())
                || RecycleOrderStatus.COMPLETED.getCode().equals(order.getStatus())
                || RecycleOrderStatus.WAIT_PAYOUT.getCode().equals(order.getStatus())
                || RecycleOrderStatus.PAYOUT_PROCESSING.getCode().equals(order.getStatus())) {
            return false;
        }
        return true;
    }

    private List<PickupTaskLogVo> buildLogVos(List<PickupTaskLog> logs, PickupTaskStatus status) {
        List<PickupTaskLogVo> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(logs)) {
            return voList;
        }
        for (int i = 0; i < logs.size(); i++) {
            PickupTaskLog log = logs.get(i);
            PickupTaskAction action = resolveAction(log.getActionType());
            PickupTaskLogVo vo = new PickupTaskLogVo();
            vo.setActionType(log.getActionType());
            vo.setTitle(Objects.nonNull(action) ? action.getTitle() : log.getActionType());
            vo.setContent(log.getContent());
            vo.setCreateTime(log.getCreateTime());
            boolean isLast = i == logs.size() - 1;
            vo.setActive(isLast && !PickupTaskStatus.DONE.equals(status));
            if (PickupTaskStatus.DONE.equals(status) && PickupTaskAction.FINISH.name().equals(log.getActionType())) {
                vo.setActive(true);
            }
            voList.add(vo);
        }
        return voList;
    }

    private PickupTaskAction resolveAction(String actionType) {
        if (StringUtils.isBlank(actionType)) {
            return null;
        }
        try {
            return PickupTaskAction.valueOf(actionType);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void appendTaskLog(Long taskId, PickupTaskAction action, String content, Long operatorId) {
        PickupTaskLog log = new PickupTaskLog();
        log.setTaskId(taskId);
        log.setActionType(action.name());
        log.setContent(content);
        log.setOperatorId(operatorId);
        iPickupTaskLogService.save(log);
    }

    private void advanceOrderToInProgress(Long orderId, Long staffId) {
        if (Objects.isNull(orderId)) {
            return;
        }
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order)) {
            return;
        }
        if (!RecycleOrderStatus.WAIT_ASSIGN.getCode().equals(order.getStatus())) {
            return;
        }
        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.IN_PROGRESS);
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setStatusDesc("员工正在上门取件");
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_PICKUP_START, fromStatus, order.getStatus(),
                "员工开始上门取件", staffId);
    }

    private String completeLinkedOrder(PickupTask task, Long staffId) {
        if (Objects.isNull(task.getOrderId())) {
            return null;
        }
        RecycleOrder order = iRecycleOrderService.getById(task.getOrderId());
        if (Objects.isNull(order)) {
            return null;
        }

        String currentStatus = order.getStatus();
        if (RecycleOrderStatus.WAIT_ASSIGN.getCode().equals(currentStatus)) {
            transitionOrder(order, RecycleOrderStatus.IN_PROGRESS, "员工正在上门取件", staffId, ACTION_PICKUP_START, "员工开始上门取件");
            currentStatus = order.getStatus();
        }
        if (RecycleOrderStatus.IN_PROGRESS.getCode().equals(currentStatus)) {
            transitionOrder(order, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM, "已完成上门回收，请确认最终成交价",
                    staffId, ACTION_PICKUP_FINISH, "员工已完成上门取件");
            createSettlementIfAbsent(order);
            return RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey();
        }
        if (RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode().equals(currentStatus)) {
            return RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey();
        }
        return RecycleOrderStatus.of(currentStatus).getStatusKey();
    }

    private void transitionOrder(RecycleOrder order, RecycleOrderStatus targetStatus, String statusDesc,
                                 Long staffId, String actionType, String logContent) {
        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, targetStatus);
        order.setStatus(targetStatus.getCode());
        order.setStatusDesc(statusDesc);
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), actionType, fromStatus, order.getStatus(), logContent, staffId);
    }

    private void createSettlementIfAbsent(RecycleOrder order) {
        long count = iOrderSettlementService.count(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, order.getId()));
        if (count > 0) {
            return;
        }
        BigDecimal estimateAmount = order.getEstimateAmount();
        BigDecimal actualAmount = order.getActualAmount();
        if (actualAmount == null) {
            throw new BizException("请先填写实际成交金额");
        }
        OrderSettlement settlement = new OrderSettlement();
        settlement.setSettlementNo("ST" + GenUtil.genOrderNo());
        settlement.setOrderId(order.getId());
        settlement.setUserId(order.getUserId());
        settlement.setEstimateAmount(estimateAmount);
        settlement.setActualAmount(actualAmount);
        settlement.setStatus(SETTLEMENT_STATUS_PENDING);
        settlement.setRemark("员工完成取件后自动生成结算单");
        iOrderSettlementService.save(settlement);
    }

    private void assertActualAmountFilled(PickupTask task) {
        if (Objects.isNull(task.getOrderId())) {
            return;
        }
        RecycleOrder order = iRecycleOrderService.getById(task.getOrderId());
        if (Objects.isNull(order)) {
            return;
        }
        if (order.getActualAmount() == null) {
            throw new BizException("请先填写实际成交金额");
        }
    }

    private void appendOrderLog(Long orderId, String actionType, String fromStatus, String toStatus,
                                 String content, Long operatorId) {
        RecycleOrderLog log = new RecycleOrderLog();
        log.setOrderId(orderId);
        log.setActionType(actionType);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setContent(content);
        log.setOperatorId(operatorId);
        log.setOperatorType(OPERATOR_TYPE_STAFF);
        iRecycleOrderLogService.save(log);
    }
}
