package com.sqx.app.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderAssignPickupBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderCancelBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderCompleteBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderConfirmMailReceiveBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderConfirmPayoutBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderHandleBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderPageBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateEstimateBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateExpressBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderDetailVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderDisputeSummaryVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderExceptionSummaryVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderExceptionVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderReviewSummaryVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderVo;
import com.sqx.app.admin.order.service.AdminOrderManageService;
import com.sqx.app.admin.order.service.AdminOrderRescheduleManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.express.aliyun.AliyunLogisticsService;
import com.sqx.app.express.sf.SfExpressOrderService;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderException;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.app.order.domain.vo.RecycleOrderLogVo;
import com.sqx.app.order.enums.RecycleFulfillType;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.support.RecycleOrderStateMachine;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.entity.OrderReview;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.GenUtil;
import com.sqx.common.mybatis.utils.PageUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminOrderManageServiceImpl implements AdminOrderManageService {

    private static final Integer STAFF_USER_TYPE = 3;
    private static final String ACTION_CANCEL = "CANCEL";
    private static final String ACTION_STATUS = "STATUS_CHANGE";
    private static final String ACTION_ASSIGN = "ASSIGN_PICKUP";
    private static final String ACTION_PICKUP_START = "PICKUP_START";
    private static final String ACTION_ACTUAL_UPDATE = "ACTUAL_UPDATE";
    private static final String ACTION_CONFIRM_PAYOUT = "CONFIRM_PAYOUT";
    private static final String ACTION_COMPLETE = "COMPLETE";
    private static final String ACTION_HANDLE = "HANDLE";
    private static final String ACTION_EXPRESS_UPDATE = "EXPRESS_UPDATE";
    private static final String ACTION_MAIL_RECEIVE = "MAIL_RECEIVE";
    private static final String MAIL_STATUS_WAIT_RECEIVE = "待收货";
    private static final String DISPUTE_STATUS_PENDING = "PENDING";
    private static final String DISPUTE_STATUS_RESOLVED = "RESOLVED";
    private static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    private static final String OPERATOR_TYPE_ADMIN = "ADMIN";

    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private IPickupTaskService iPickupTaskService;
    @Resource
    private IPickupTaskLogService iPickupTaskLogService;
    @Resource
    private IUserService iUserService;
    @Resource
    private IOrderSettlementService iOrderSettlementService;
    @Resource
    private IOrderDisputeService iOrderDisputeService;
    @Resource
    private PayoutService payoutService;
    @Resource
    private AdminOrderRescheduleManageService adminOrderRescheduleManageService;
    @Resource
    private IOrderExceptionService iOrderExceptionService;
    @Resource
    private IOrderReviewService iOrderReviewService;
    @Resource
    private SfExpressOrderService sfExpressOrderService;
    @Resource
    private AliyunLogisticsService aliyunLogisticsService;

    @Override
    public IPage<AdminOrderVo> pageInfo(AdminOrderPageBo pageBo) {
        Page<RecycleOrder> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<Long> orderIds = adminLookupSupport.resolveOrderIds(pageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<String> statusCodes = resolveStatusCodes(pageBo.getStatusKey());

        QueryWrapper<RecycleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Objects.nonNull(pageBo.getUserId()), RecycleOrder::getUserId, pageBo.getUserId())
                .in(userIds != null, RecycleOrder::getUserId, userIds)
                .in(orderIds != null, RecycleOrder::getId, orderIds)
                .eq(StringUtils.isNotBlank(pageBo.getOrderType()), RecycleOrder::getOrderType, pageBo.getOrderType())
                .eq(StringUtils.isNotBlank(pageBo.getFulfillType()), RecycleOrder::getFulfillType, pageBo.getFulfillType())
                .eq(StringUtils.isNotBlank(pageBo.getStatus()), RecycleOrder::getStatus, pageBo.getStatus())
                .in(CollectionUtils.isNotEmpty(statusCodes), RecycleOrder::getStatus, statusCodes)
                .orderByDesc(RecycleOrder::getCreateTime);

        IPage<RecycleOrder> iPage = iRecycleOrderService.page(initPage, queryWrapper);
        List<AdminOrderVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (RecycleOrder record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
            fillLatestUserFeedback(voList);
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminOrderDetailVo info(AdminOrderInfoBo infoBo) {
        RecycleOrder order = iRecycleOrderService.getById(infoBo.getId());
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }

        AdminOrderDetailVo detailVo = new AdminOrderDetailVo();
        BeanUtils.copyProperties(toVo(order), detailVo);

        List<RecycleOrderLog> logs = iRecycleOrderLogService.list(new LambdaQueryWrapper<RecycleOrderLog>()
                .eq(RecycleOrderLog::getOrderId, order.getId())
                .orderByDesc(RecycleOrderLog::getCreateTime));
        List<RecycleOrderLogVo> logVoList = CollectionUtils.isNotEmpty(logs)
                ? logs.stream().map(this::toLogVo).collect(Collectors.toList())
                : new ArrayList<>();
        detailVo.setLogs(logVoList);
        detailVo.setReschedules(adminOrderRescheduleManageService.listByOrderId(order.getId()));
        List<AdminOrderExceptionVo> exceptions = listExceptionsByOrderId(order);
        List<AdminOrderDisputeSummaryVo> disputes = listDisputesByOrderId(order.getId());
        List<AdminOrderReviewSummaryVo> reviews = listReviewsByOrderId(order.getId());
        detailVo.setExceptions(exceptions);
        detailVo.setDisputes(disputes);
        detailVo.setReviews(reviews);
        if (CollectionUtils.isNotEmpty(disputes)) {
            detailVo.setLatestDispute(disputes.get(0));
        }
        if (CollectionUtils.isNotEmpty(exceptions)) {
            detailVo.setLatestException(toExceptionSummaryFromVo(exceptions.get(0)));
        }
        if (CollectionUtils.isNotEmpty(reviews)) {
            detailVo.setLatestReview(reviews.get(0));
        }
        detailVo.setUserFeedbackSummary(buildUserFeedbackSummary(detailVo));
        if (RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            try {
                detailVo.setExpressTracks(aliyunLogisticsService.listExpressTracks(
                        order.getExpressNo(), resolveExpressPhones(order)));
            } catch (Exception ex) {
                log.warn("订单详情加载物流轨迹失败: orderId={}, err={}", order.getId(), ex.getMessage());
                detailVo.setExpressTracks(new ArrayList<>());
            }
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(AdminOrderCancelBo cancelBo) {
        RecycleOrder order = requireOrder(cancelBo.getId());
        if (RecycleOrderStatus.CANCELLED.getCode().equals(order.getStatus())) {
            throw new BizException("订单已取消");
        }
        if (RecycleOrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            throw new BizException("已完成订单不可取消");
        }
        String fromStatus = order.getStatus();
        if (RecycleFulfillType.of(order.getFulfillType()).isMail() && StringUtils.isNotBlank(order.getExpressNo())) {
            sfExpressOrderService.cancelMailOrder(order);
        }
        order.setStatus(RecycleOrderStatus.CANCELLED.getCode());
        order.setStatusDesc(RecycleOrderStatus.CANCELLED.getLabel());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        String content = "管理员取消订单";
        if (StringUtils.isNotBlank(cancelBo.getReason())) {
            content = content + "：" + cancelBo.getReason();
        }
        appendOrderLog(order.getId(), ACTION_CANCEL, fromStatus, order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(AdminOrderUpdateStatusBo updateBo) {
        RecycleOrder order = requireOrder(updateBo.getId());
        RecycleOrderStatus target = RecycleOrderStatus.of(updateBo.getTargetStatus());
        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, target);
        order.setStatus(target.getCode());
        order.setStatusDesc(StringUtils.defaultIfBlank(updateBo.getRemark(), target.getLabel()));
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        String content = "管理员调整订单状态为「" + target.getLabel() + "」";
        if (StringUtils.isNotBlank(updateBo.getRemark())) {
            content = content + "：" + updateBo.getRemark();
        }
        appendOrderLog(order.getId(), ACTION_STATUS, fromStatus, order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPickup(AdminOrderAssignPickupBo assignBo) {
        RecycleOrder order = requireOrder(assignBo.getOrderId());
        if (RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            throw new BizException("邮寄订单不支持分配上门员工");
        }
        UserEntity staff = requireStaff(assignBo.getStaffId());

        PickupTask task = iPickupTaskService.getOne(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getOrderId, order.getId())
                .in(PickupTask::getStatus, PickupTaskStatus.PENDING.name(), PickupTaskStatus.RUNNING.name())
                .orderByDesc(PickupTask::getCreateTime)
                .last("LIMIT 1"));

        if (Objects.isNull(task)) {
            task = new PickupTask();
            task.setTaskNo("PK" + GenUtil.genOrderNo());
            task.setOrderId(order.getId());
            task.setStaffId(staff.getUserId());
            task.setStatus(PickupTaskStatus.PENDING.name());
            task.setAppointmentTime(order.getAppointmentTime());
            task.setContactName(order.getContactName());
            task.setContactMobile(order.getContactMobile());
            task.setAddressText(order.getAddressText());
            task.setCategoryName(order.getCategoryName());
            task.setItemSummary(StringUtils.defaultIfBlank(order.getCategoryName(), "待确认"));
            iPickupTaskService.save(task);
            appendPickupLog(task.getId(), PickupTaskAction.ASSIGN, "管理员分配取件员：" + staff.getUserName(), staff.getUserId());
        } else {
            task.setStaffId(staff.getUserId());
            iPickupTaskService.updateById(task);
            appendPickupLog(task.getId(), PickupTaskAction.ASSIGN, "管理员改派取件员：" + staff.getUserName(), staff.getUserId());
        }

        if (RecycleOrderStatus.WAIT_ASSIGN.getCode().equals(order.getStatus())) {
            String fromStatus = order.getStatus();
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.IN_PROGRESS);
            order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
            order.setStatusDesc("员工正在上门取件");
            order.setUpdateTime(LocalDateTime.now());
            iRecycleOrderService.updateById(order);
            appendOrderLog(order.getId(), ACTION_PICKUP_START, fromStatus, order.getStatus(),
                    "管理员分配取件员：" + staff.getUserName());
        } else if (RecycleOrderStatus.IN_PROGRESS.getCode().equals(order.getStatus())) {
            finishPickupTask(task);
            String fromStatus = order.getStatus();
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM);
            order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
            order.setStatusDesc(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getLabel());
            order.setUpdateTime(LocalDateTime.now());
            iRecycleOrderService.updateById(order);
            appendOrderLog(order.getId(), ACTION_ASSIGN, fromStatus, order.getStatus(),
                    "管理员分配取件员并进入待确认：" + staff.getUserName());
        } else {
            appendOrderLog(order.getId(), ACTION_ASSIGN, order.getStatus(), order.getStatus(),
                    "管理员分配/改派取件员：" + staff.getUserName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEstimate(AdminOrderUpdateEstimateBo updateBo) {
        RecycleOrder order = requireOrder(updateBo.getId());
        if (!RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许修改实际金额");
        }
        // 仅更新实际金额（对应用户端「实际成交金额」），预估金额保持不变
        order.setActualAmount(updateBo.getActualAmount());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        OrderSettlement settlement = findSettlement(order.getId());
        if (Objects.nonNull(settlement)) {
            settlement.setActualAmount(updateBo.getActualAmount());
            settlement.setUpdateTime(LocalDateTime.now());
            iOrderSettlementService.updateById(settlement);
        }

        String content = "管理员修改实际金额为 " + updateBo.getActualAmount();
        if (StringUtils.isNotBlank(updateBo.getRemark())) {
            content = content + "，修改说明：" + updateBo.getRemark();
        }
        appendOrderLog(order.getId(), ACTION_ACTUAL_UPDATE, order.getStatus(), order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPayout(AdminOrderConfirmPayoutBo confirmBo) {
        RecycleOrder order = requireOrder(confirmBo.getId());
        if (!RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许确认打款");
        }
        BigDecimal amount = resolvePayoutAmount(order);
        order.setActualAmount(amount);
        order.setEstimateAmountConfirmed(1);
        order.setUpdateTime(LocalDateTime.now());

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_PAYOUT);
        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setStatusDesc("管理员已确认成交价，等待打款");
        iRecycleOrderService.updateById(order);

        OrderSettlement settlement = findSettlement(order.getId());
        if (Objects.nonNull(settlement)) {
            settlement.setActualAmount(amount);
            settlement.setStatus("CONFIRMED");
            settlement.setUpdateTime(LocalDateTime.now());
            iOrderSettlementService.updateById(settlement);
        }

        payoutService.startPayout(order, amount, null);

        String content = "管理员确认打款，金额 " + amount;
        if (StringUtils.isNotBlank(confirmBo.getRemark())) {
            content = content + "：" + confirmBo.getRemark();
        }
        appendOrderLog(order.getId(), ACTION_CONFIRM_PAYOUT, fromStatus, order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(AdminOrderCompleteBo completeBo) {
        RecycleOrder order = requireOrder(completeBo.getId());
        if (!RecycleOrderStatus.PAYOUT_PROCESSING.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许完成");
        }
        String fromStatus = order.getStatus();
        payoutService.finishPayout(order, null);
        order = requireOrder(order.getId());
        String content = "管理员确认订单完成";
        if (StringUtils.isNotBlank(completeBo.getRemark())) {
            content = content + "：" + completeBo.getRemark();
        }
        appendOrderLog(order.getId(), ACTION_COMPLETE, fromStatus, order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(AdminOrderHandleBo handleBo) {
        RecycleOrder order = requireOrder(handleBo.getId());
        String status = order.getStatus();
        if (!RecycleOrderStatus.EXCEPTION_PENDING.getCode().equals(status)
                && !RecycleOrderStatus.DISPUTE_PENDING.getCode().equals(status)) {
            throw new BizException("当前订单状态不允许处理");
        }

        boolean resolved = isResolvedHandleType(handleBo.getHandleType());
        String fromStatus = order.getStatus();
        String content = resolved ? "管理员处理完成" : "管理员标记未处理";

        if (resolved) {
            if (RecycleOrderStatus.EXCEPTION_PENDING.getCode().equals(status)) {
                RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.IN_PROGRESS);
                order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
                order.setStatusDesc(RecycleOrderStatus.IN_PROGRESS.getLabel());
            } else {
                RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM);
                order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
                order.setStatusDesc("争议已处理，等待用户确认成交价");
                order.setEstimateAmountConfirmed(0);
                resetSettlementPending(order.getId());
                resolveDispute(order.getId(), handleBo.getRemark());
            }
        } else {
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.CANCELLED);
            order.setStatus(RecycleOrderStatus.CANCELLED.getCode());
            order.setStatusDesc(RecycleOrderStatus.CANCELLED.getLabel());
            if (RecycleOrderStatus.DISPUTE_PENDING.getCode().equals(status)) {
                resolveDispute(order.getId(), handleBo.getRemark());
            }
        }

        if (StringUtils.isNotBlank(handleBo.getRemark())) {
            content = content + "：" + handleBo.getRemark();
        }
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_HANDLE, fromStatus, order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExpressNo(AdminOrderUpdateExpressBo updateBo) {
        RecycleOrder order = requireOrder(updateBo.getId());
        if (!RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            throw new BizException("仅邮寄订单支持修改快递单号");
        }
        String expressNo = StringUtils.trimToEmpty(updateBo.getExpressNo());
        if (StringUtils.isBlank(expressNo)) {
            throw new BizException("快递单号不能为空");
        }
        String fromExpressNo = StringUtils.trimToEmpty(order.getExpressNo());
        if (expressNo.equals(fromExpressNo)) {
            return;
        }
        order.setExpressNo(expressNo);
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        String content = StringUtils.isBlank(fromExpressNo)
                ? "管理员设置快递单号为 " + expressNo
                : "管理员将快递单号由 " + fromExpressNo + " 修改为 " + expressNo;
        appendOrderLog(order.getId(), ACTION_EXPRESS_UPDATE, order.getStatus(), order.getStatus(), content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmMailReceive(AdminOrderConfirmMailReceiveBo confirmBo) {
        RecycleOrder order = requireOrder(confirmBo.getId());
        if (!RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            throw new BizException("仅邮寄订单支持确认收货");
        }
        if (!RecycleOrderStatus.IN_PROGRESS.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许确认收货");
        }
        if (StringUtils.isBlank(order.getExpressNo())) {
            throw new BizException("订单尚未填写快递单号，无法确认收货");
        }
        if (!MAIL_STATUS_WAIT_RECEIVE.equals(StringUtils.trimToEmpty(order.getStatusDesc()))) {
            throw new BizException("请等待用户发货后再确认收货");
        }

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM);
        order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
        order.setStatusDesc(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getLabel());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        createMailSettlementIfAbsent(order);
        appendOrderLog(order.getId(), ACTION_MAIL_RECEIVE, fromStatus, order.getStatus(),
                "平台确认收货，订单进入待确认");
    }

    private void createMailSettlementIfAbsent(RecycleOrder order) {
        long count = iOrderSettlementService.count(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, order.getId()));
        if (count > 0) {
            return;
        }
        // 邮寄确认收货仅生成结算单；实际成交金额须管理员手动填写，禁止用预估金额回填
        OrderSettlement settlement = new OrderSettlement();
        settlement.setSettlementNo("ST" + GenUtil.genOrderNo());
        settlement.setOrderId(order.getId());
        settlement.setUserId(order.getUserId());
        settlement.setEstimateAmount(order.getEstimateAmount());
        settlement.setActualAmount(order.getActualAmount());
        settlement.setStatus(SETTLEMENT_STATUS_PENDING);
        settlement.setRemark("平台确认邮寄收货后自动生成结算单");
        iOrderSettlementService.save(settlement);
    }

    @Override
    public List<ExpressTrackVo> listExpressTracks(AdminOrderInfoBo infoBo) {
        RecycleOrder order = requireOrder(infoBo.getId());
        if (!RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            throw new BizException("仅邮寄订单支持查询物流轨迹");
        }
        if (StringUtils.isBlank(order.getExpressNo())) {
            throw new BizException("订单尚未填写快递单号");
        }
        return aliyunLogisticsService.listExpressTracks(order.getExpressNo(), resolveExpressPhones(order));
    }

    private String[] resolveExpressPhones(RecycleOrder order) {
        return new String[]{
                order.getContactMobile(),
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_PHONE)
        };
    }

    private void finishPickupTask(PickupTask task) {
        if (Objects.isNull(task)) {
            return;
        }
        if (PickupTaskStatus.DONE.name().equals(task.getStatus())) {
            return;
        }
        task.setStatus(PickupTaskStatus.DONE.name());
        iPickupTaskService.updateById(task);
        appendPickupLog(task.getId(), PickupTaskAction.FINISH, "管理员分配员工后自动完成取件", null);
    }

    private OrderSettlement findSettlement(Long orderId) {
        return iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, orderId)
                .last("LIMIT 1"));
    }

    private BigDecimal resolvePayoutAmount(RecycleOrder order) {
        if (Objects.nonNull(order.getActualAmount())) {
            return order.getActualAmount();
        }
        OrderSettlement settlement = findSettlement(order.getId());
        if (Objects.nonNull(settlement) && Objects.nonNull(settlement.getActualAmount())) {
            return settlement.getActualAmount();
        }
        if (Objects.nonNull(settlement) && Objects.nonNull(settlement.getEstimateAmount())) {
            return settlement.getEstimateAmount();
        }
        return order.getEstimateAmount();
    }

    private void resolveDispute(Long orderId, String remark) {
        OrderDispute dispute = iOrderDisputeService.getOne(new LambdaQueryWrapper<OrderDispute>()
                .eq(OrderDispute::getOrderId, orderId)
                .eq(OrderDispute::getStatus, DISPUTE_STATUS_PENDING)
                .orderByDesc(OrderDispute::getCreateTime)
                .last("LIMIT 1"));
        if (Objects.isNull(dispute)) {
            return;
        }
        dispute.setStatus(DISPUTE_STATUS_RESOLVED);
        dispute.setResolveRemark(remark);
        dispute.setUpdateTime(LocalDateTime.now());
        iOrderDisputeService.updateById(dispute);
    }

    private void resetSettlementPending(Long orderId) {
        OrderSettlement settlement = iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, orderId)
                .orderByDesc(OrderSettlement::getId)
                .last("LIMIT 1"));
        if (Objects.isNull(settlement)) {
            return;
        }
        settlement.setStatus(SETTLEMENT_STATUS_PENDING);
        settlement.setUpdateTime(LocalDateTime.now());
        iOrderSettlementService.updateById(settlement);
    }

    private boolean isResolvedHandleType(String handleType) {
        if (StringUtils.isBlank(handleType)) {
            return false;
        }
        String normalized = handleType.trim();
        return "RESOLVED".equalsIgnoreCase(normalized) || "已处理".equals(normalized);
    }

    private RecycleOrder requireOrder(Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private UserEntity requireStaff(Long staffId) {
        UserEntity staff = iUserService.getById(staffId);
        if (Objects.isNull(staff) || !Objects.equals(staff.getUserType(), STAFF_USER_TYPE)) {
            throw new BizException("取件员工不存在或类型不正确");
        }
        return staff;
    }

    private void appendOrderLog(Long orderId, String actionType, String fromStatus, String toStatus, String content) {
        RecycleOrderLog log = new RecycleOrderLog();
        log.setOrderId(orderId);
        log.setActionType(actionType);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setContent(content);
        log.setOperatorType(OPERATOR_TYPE_ADMIN);
        iRecycleOrderLogService.save(log);
    }

    private void appendPickupLog(Long taskId, PickupTaskAction action, String content, Long operatorId) {
        PickupTaskLog log = new PickupTaskLog();
        log.setTaskId(taskId);
        log.setActionType(action.name());
        log.setContent(content);
        log.setOperatorId(operatorId);
        iPickupTaskLogService.save(log);
    }

    private AdminOrderVo toVo(RecycleOrder order) {
        AdminOrderVo vo = new AdminOrderVo();
        BeanUtils.copyProperties(order, vo);
        RecycleOrderStatus status = RecycleOrderStatus.of(order.getStatus());
        vo.setStatusKey(status.getStatusKey());
        RecycleFulfillType fulfillType = RecycleFulfillType.of(order.getFulfillType());
        vo.setFulfillType(fulfillType.getCode());
        vo.setFulfillTypeLabel(fulfillType.getLabel());
        vo.setPickupAddressText(order.getAddressText());
        vo.setMailAddressText(order.getMailAddressText());
        vo.setExpressNo(order.getExpressNo());
        if (fulfillType.isMail()) {
            vo.setExpressCompany(StringUtils.defaultIfBlank(vo.getExpressCompany(), "顺丰速运"));
        }
        vo.setEstimateAmountConfirmed(resolveEstimateAmountConfirmed(order));
        adminLookupSupport.fillUserFields(order.getUserId(), vo);
        return vo;
    }

    private boolean resolveEstimateAmountConfirmed(RecycleOrder order) {
        if (Objects.equals(order.getEstimateAmountConfirmed(), 1)) {
            return true;
        }
        String status = order.getStatus();
        return RecycleOrderStatus.WAIT_PAYOUT.getCode().equals(status)
                || RecycleOrderStatus.PAYOUT_PROCESSING.getCode().equals(status)
                || RecycleOrderStatus.COMPLETED.getCode().equals(status);
    }

    private List<String> resolveStatusCodes(String statusKey) {
        if (StringUtils.isBlank(statusKey)) {
            return null;
        }
        List<String> statusCodes = Arrays.stream(RecycleOrderStatus.values())
                .filter(status -> statusKey.equals(status.getStatusKey()))
                .map(RecycleOrderStatus::getCode)
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(statusCodes) ? null : statusCodes;
    }

    private RecycleOrderLogVo toLogVo(RecycleOrderLog log) {
        RecycleOrderLogVo vo = new RecycleOrderLogVo();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    private List<AdminOrderExceptionVo> listExceptionsByOrderId(RecycleOrder order) {
        if (Objects.isNull(order) || Objects.isNull(order.getId())) {
            return Collections.emptyList();
        }
        List<OrderException> records = iOrderExceptionService.list(new LambdaQueryWrapper<OrderException>()
                .eq(OrderException::getOrderId, order.getId())
                .orderByDesc(OrderException::getCreateTime));
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<AdminOrderExceptionVo> voList = new ArrayList<>();
        for (OrderException record : records) {
            AdminOrderExceptionVo vo = new AdminOrderExceptionVo();
            BeanUtils.copyProperties(record, vo);
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getStatus());
            vo.setOrderStatusDesc(order.getStatusDesc());
            adminLookupSupport.fillUserFields(order.getUserId(), vo);
            voList.add(vo);
        }
        return voList;
    }

    private List<AdminOrderDisputeSummaryVo> listDisputesByOrderId(Long orderId) {
        if (Objects.isNull(orderId)) {
            return Collections.emptyList();
        }
        List<OrderDispute> records = iOrderDisputeService.list(new LambdaQueryWrapper<OrderDispute>()
                .eq(OrderDispute::getOrderId, orderId)
                .orderByDesc(OrderDispute::getCreateTime));
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(this::toDisputeSummary).collect(Collectors.toList());
    }

    private List<AdminOrderReviewSummaryVo> listReviewsByOrderId(Long orderId) {
        if (Objects.isNull(orderId)) {
            return Collections.emptyList();
        }
        List<OrderReview> records = iOrderReviewService.list(new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getOrderId, orderId)
                .orderByDesc(OrderReview::getCreateTime));
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(this::toReviewSummary).collect(Collectors.toList());
    }

    private void fillLatestUserFeedback(List<AdminOrderVo> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        List<Long> orderIds = voList.stream()
                .map(AdminOrderVo::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orderIds)) {
            return;
        }

        Map<Long, OrderDispute> latestDisputeMap = loadLatestByOrderId(
                iOrderDisputeService.list(new LambdaQueryWrapper<OrderDispute>()
                        .in(OrderDispute::getOrderId, orderIds)
                        .orderByDesc(OrderDispute::getCreateTime)),
                OrderDispute::getOrderId);
        Map<Long, OrderException> latestExceptionMap = loadLatestByOrderId(
                iOrderExceptionService.list(new LambdaQueryWrapper<OrderException>()
                        .in(OrderException::getOrderId, orderIds)
                        .orderByDesc(OrderException::getCreateTime)),
                OrderException::getOrderId);
        Map<Long, OrderReview> latestReviewMap = loadLatestByOrderId(
                iOrderReviewService.list(new LambdaQueryWrapper<OrderReview>()
                        .in(OrderReview::getOrderId, orderIds)
                        .orderByDesc(OrderReview::getCreateTime)),
                OrderReview::getOrderId);

        for (AdminOrderVo vo : voList) {
            OrderDispute dispute = latestDisputeMap.get(vo.getId());
            if (Objects.nonNull(dispute)) {
                vo.setLatestDispute(toDisputeSummary(dispute));
            }
            OrderException exception = latestExceptionMap.get(vo.getId());
            if (Objects.nonNull(exception)) {
                vo.setLatestException(toExceptionSummary(exception));
            }
            OrderReview review = latestReviewMap.get(vo.getId());
            if (Objects.nonNull(review)) {
                vo.setLatestReview(toReviewSummary(review));
            }
            vo.setUserFeedbackSummary(buildUserFeedbackSummary(vo));
        }
    }

    private <T> Map<Long, T> loadLatestByOrderId(List<T> records, Function<T, Long> orderIdGetter) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyMap();
        }
        Map<Long, T> latestMap = new LinkedHashMap<>();
        for (T record : records) {
            Long orderId = orderIdGetter.apply(record);
            if (Objects.isNull(orderId) || latestMap.containsKey(orderId)) {
                continue;
            }
            latestMap.put(orderId, record);
        }
        return latestMap;
    }

    private AdminOrderDisputeSummaryVo toDisputeSummary(OrderDispute dispute) {
        AdminOrderDisputeSummaryVo vo = new AdminOrderDisputeSummaryVo();
        BeanUtils.copyProperties(dispute, vo);
        return vo;
    }

    private AdminOrderExceptionSummaryVo toExceptionSummary(OrderException exception) {
        AdminOrderExceptionSummaryVo vo = new AdminOrderExceptionSummaryVo();
        BeanUtils.copyProperties(exception, vo);
        return vo;
    }

    private AdminOrderExceptionSummaryVo toExceptionSummaryFromVo(AdminOrderExceptionVo exception) {
        AdminOrderExceptionSummaryVo vo = new AdminOrderExceptionSummaryVo();
        vo.setId(exception.getId());
        vo.setExceptionType(exception.getExceptionType());
        vo.setExpectAction(exception.getExpectAction());
        vo.setDescription(exception.getDescription());
        vo.setCreateTime(exception.getCreateTime());
        return vo;
    }

    private AdminOrderReviewSummaryVo toReviewSummary(OrderReview review) {
        AdminOrderReviewSummaryVo vo = new AdminOrderReviewSummaryVo();
        BeanUtils.copyProperties(review, vo);
        return vo;
    }

    private String buildUserFeedbackSummary(AdminOrderVo vo) {
        List<String> parts = new ArrayList<>();
        if (Objects.nonNull(vo.getLatestDispute())) {
            AdminOrderDisputeSummaryVo dispute = vo.getLatestDispute();
            String text = joinFeedbackParts("争议", dispute.getReason(), dispute.getDescription());
            if (StringUtils.isNotBlank(text)) {
                parts.add(text);
            }
        }
        if (Objects.nonNull(vo.getLatestException())) {
            AdminOrderExceptionSummaryVo exception = vo.getLatestException();
            String text = joinFeedbackParts("异常", exception.getExceptionType(), exception.getDescription());
            if (StringUtils.isNotBlank(text)) {
                parts.add(text);
            }
        }
        if (Objects.nonNull(vo.getLatestReview())) {
            AdminOrderReviewSummaryVo review = vo.getLatestReview();
            String scoreText = Objects.nonNull(review.getScore()) ? review.getScore() + "分" : null;
            String text = joinFeedbackParts("评价", scoreText, review.getComplaintType(), review.getContent());
            if (StringUtils.isNotBlank(text)) {
                parts.add(text);
            }
        }
        return parts.isEmpty() ? null : String.join("；", parts);
    }

    private String joinFeedbackParts(String prefix, String... values) {
        List<String> filtered = Arrays.stream(values)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (filtered.isEmpty()) {
            return null;
        }
        return prefix + "：" + String.join(" - ", filtered);
    }
}
