package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementSaveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminSettlementVo;
import com.sqx.app.admin.orderflow.service.AdminSettlementManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.support.RecycleOrderStateMachine;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
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
public class AdminSettlementManageServiceImpl implements AdminSettlementManageService {

    private static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    private static final String SETTLEMENT_STATUS_CONFIRMED = "CONFIRMED";
    private static final String ACTION_SETTLEMENT_SAVE = "SETTLEMENT_SAVE";
    private static final String ACTION_SETTLEMENT_CONFIRM = "SETTLEMENT_CONFIRM";
    private static final String OPERATOR_TYPE_ADMIN = "ADMIN";

    @Resource
    private IOrderSettlementService iOrderSettlementService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Resource
    private PayoutService payoutService;

    @Override
    public IPage<AdminSettlementVo> pageInfo(AdminSettlementPageBo pageBo) {
        Page<OrderSettlement> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<Long> orderIds = adminLookupSupport.resolveOrderIds(pageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<OrderSettlement> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, OrderSettlement::getUserId, userIds)
                .in(orderIds != null, OrderSettlement::getOrderId, orderIds)
                .eq(StringUtils.isNotBlank(pageBo.getStatus()), OrderSettlement::getStatus, pageBo.getStatus())
                .orderByDesc(OrderSettlement::getCreateTime);

        IPage<OrderSettlement> iPage = iOrderSettlementService.page(initPage, wrapper);
        List<AdminSettlementVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (OrderSettlement record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminSettlementVo info(AdminSettlementInfoBo infoBo) {
        OrderSettlement entity = iOrderSettlementService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("结算单不存在");
        }
        AdminSettlementVo vo = toVo(entity);
        RecycleOrder order = iRecycleOrderService.getById(entity.getOrderId());
        if (Objects.nonNull(order)) {
            vo.setOrderStatus(order.getStatus());
            vo.setOrderStatusDesc(order.getStatusDesc());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(AdminSettlementSaveBo saveBo) {
        OrderSettlement settlement = requireSettlement(saveBo.getId());
        if (SETTLEMENT_STATUS_CONFIRMED.equals(settlement.getStatus())) {
            throw new BizException("结算已确认，不可修改金额");
        }
        settlement.setActualAmount(saveBo.getActualAmount());
        if (StringUtils.isNotBlank(saveBo.getRemark())) {
            settlement.setRemark(saveBo.getRemark());
        }
        settlement.setUpdateTime(LocalDateTime.now());
        iOrderSettlementService.updateById(settlement);

        RecycleOrder order = requireOrder(settlement.getOrderId());
        order.setActualAmount(saveBo.getActualAmount());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_SETTLEMENT_SAVE, order.getStatus(), order.getStatus(),
                "管理员录入实际成交金额：" + saveBo.getActualAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(AdminSettlementConfirmBo confirmBo) {
        OrderSettlement settlement = requireSettlement(confirmBo.getId());
        if (SETTLEMENT_STATUS_CONFIRMED.equals(settlement.getStatus())) {
            throw new BizException("结算已确认，请勿重复操作");
        }
        RecycleOrder order = requireOrder(settlement.getOrderId());
        if (!RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许确认结算");
        }

        BigDecimal actualAmount = settlement.getActualAmount();
        if (Objects.isNull(actualAmount)) {
            actualAmount = order.getActualAmount();
        }
        if (Objects.isNull(actualAmount)) {
            actualAmount = settlement.getEstimateAmount();
        }

        settlement.setActualAmount(actualAmount);
        settlement.setStatus(SETTLEMENT_STATUS_CONFIRMED);
        settlement.setUpdateTime(LocalDateTime.now());
        iOrderSettlementService.updateById(settlement);

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_PAYOUT);
        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setStatusDesc("管理员已确认成交价，等待打款");
        order.setActualAmount(actualAmount);
        order.setEstimateAmountConfirmed(1);
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        String content = "管理员代确认实际成交价格";
        if (StringUtils.isNotBlank(confirmBo.getRemark())) {
            content = content + "：" + confirmBo.getRemark();
        }
        appendOrderLog(order.getId(), ACTION_SETTLEMENT_CONFIRM, fromStatus, order.getStatus(), content);
        // 进入打款中，由打款管理/订单完成操作闭环
        payoutService.startPayout(order, actualAmount, null);
    }

    private OrderSettlement requireSettlement(Long id) {
        OrderSettlement settlement = iOrderSettlementService.getById(id);
        if (Objects.isNull(settlement)) {
            throw new BizException("结算单不存在");
        }
        return settlement;
    }

    private RecycleOrder requireOrder(Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        return order;
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

    private AdminSettlementVo toVo(OrderSettlement entity) {
        AdminSettlementVo vo = new AdminSettlementVo();
        BeanUtils.copyProperties(entity, vo);
        vo.setOrderNo(adminLookupSupport.resolveOrderNo(entity.getOrderId()));
        adminLookupSupport.fillUserFields(entity.getUserId(), vo);
        return vo;
    }
}
