package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputePageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeResolveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminDisputeVo;
import com.sqx.app.admin.orderflow.service.AdminDisputeManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.support.RecycleOrderStateMachine;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import com.sqx.common.core.exception.BizException;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AdminDisputeManageServiceImpl implements AdminDisputeManageService {

    private static final String DISPUTE_STATUS_PENDING = "PENDING";
    private static final String DISPUTE_STATUS_RESOLVED = "RESOLVED";
    private static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    private static final String ACTION_DISPUTE_RESOLVE = "DISPUTE_RESOLVE";
    private static final String OPERATOR_TYPE_ADMIN = "ADMIN";

    @Resource
    private IOrderDisputeService iOrderDisputeService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Resource
    private IOrderSettlementService iOrderSettlementService;

    @Override
    public IPage<AdminDisputeVo> pageInfo(AdminDisputePageBo pageBo) {
        Page<OrderDispute> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<Long> orderIds = adminLookupSupport.resolveOrderIds(pageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<OrderDispute> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, OrderDispute::getUserId, userIds)
                .in(orderIds != null, OrderDispute::getOrderId, orderIds)
                .eq(StringUtils.isNotBlank(pageBo.getStatus()), OrderDispute::getStatus, pageBo.getStatus())
                .orderByDesc(OrderDispute::getCreateTime);

        IPage<OrderDispute> iPage = iOrderDisputeService.page(initPage, wrapper);
        List<AdminDisputeVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (OrderDispute record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminDisputeVo info(AdminDisputeInfoBo infoBo) {
        OrderDispute entity = iOrderDisputeService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("争议记录不存在");
        }
        AdminDisputeVo vo = toVo(entity);
        RecycleOrder order = iRecycleOrderService.getById(entity.getOrderId());
        if (Objects.nonNull(order)) {
            vo.setOrderStatus(order.getStatus());
            vo.setOrderStatusDesc(order.getStatusDesc());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolve(AdminDisputeResolveBo resolveBo) {
        OrderDispute dispute = iOrderDisputeService.getById(resolveBo.getId());
        if (Objects.isNull(dispute)) {
            throw new BizException("争议记录不存在");
        }
        if (!DISPUTE_STATUS_PENDING.equals(dispute.getStatus())) {
            throw new BizException("争议已处理");
        }
        RecycleOrder order = iRecycleOrderService.getById(dispute.getOrderId());
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        if (!RecycleOrderStatus.DISPUTE_PENDING.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单不在争议处理中");
        }

        String action = StringUtils.upperCase(resolveBo.getAction());
        String fromStatus = order.getStatus();
        String content = "管理员处理争议";

        if ("CANCEL".equals(action)) {
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.CANCELLED);
            order.setStatus(RecycleOrderStatus.CANCELLED.getCode());
            order.setStatusDesc(RecycleOrderStatus.CANCELLED.getLabel());
            content = "管理员裁决：关闭订单";
        } else if ("ADJUST".equals(action)) {
            if (Objects.isNull(resolveBo.getActualAmount())) {
                throw new BizException("调整金额不能为空");
            }
            OrderSettlement settlement = requireSettlement(order.getId());
            settlement.setActualAmount(resolveBo.getActualAmount());
            settlement.setStatus(SETTLEMENT_STATUS_PENDING);
            settlement.setUpdateTime(LocalDateTime.now());
            iOrderSettlementService.updateById(settlement);
            order.setActualAmount(resolveBo.getActualAmount());
            order.setEstimateAmountConfirmed(0);
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM);
            order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
            order.setStatusDesc("管理员调整成交价，等待用户确认");
            content = "管理员调整成交价为 " + resolveBo.getActualAmount();
        } else {
            // PROCEED：维持原价，回到待确认，由用户再次确认后再打款
            OrderSettlement settlement = iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                    .eq(OrderSettlement::getOrderId, order.getId()).last("LIMIT 1"));
            if (Objects.nonNull(settlement)) {
                settlement.setStatus(SETTLEMENT_STATUS_PENDING);
                settlement.setUpdateTime(LocalDateTime.now());
                iOrderSettlementService.updateById(settlement);
            }
            order.setEstimateAmountConfirmed(0);
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM);
            order.setStatus(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode());
            order.setStatusDesc("管理员维持原价，等待用户确认");
            content = "管理员裁决：维持原价，等待用户确认";
        }

        if (StringUtils.isNotBlank(resolveBo.getRemark())) {
            content = content + "：" + resolveBo.getRemark();
        }
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        finalizeDispute(dispute, resolveBo, content, fromStatus, order);
    }

    private void finalizeDispute(OrderDispute dispute, AdminDisputeResolveBo resolveBo,
                                 String content, String fromStatus, RecycleOrder order) {
        dispute.setStatus(DISPUTE_STATUS_RESOLVED);
        dispute.setResolveRemark(resolveBo.getRemark());
        dispute.setUpdateTime(LocalDateTime.now());
        iOrderDisputeService.updateById(dispute);
        appendOrderLog(order.getId(), ACTION_DISPUTE_RESOLVE, fromStatus, order.getStatus(), content);
    }

    private OrderSettlement requireSettlement(Long orderId) {
        OrderSettlement settlement = iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, orderId).last("LIMIT 1"));
        if (Objects.isNull(settlement)) {
            throw new BizException("结算单不存在");
        }
        return settlement;
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

    private AdminDisputeVo toVo(OrderDispute entity) {
        AdminDisputeVo vo = new AdminDisputeVo();
        BeanUtils.copyProperties(entity, vo);
        vo.setOrderNo(adminLookupSupport.resolveOrderNo(entity.getOrderId()));
        adminLookupSupport.fillUserFields(entity.getUserId(), vo);
        return vo;
    }
}
