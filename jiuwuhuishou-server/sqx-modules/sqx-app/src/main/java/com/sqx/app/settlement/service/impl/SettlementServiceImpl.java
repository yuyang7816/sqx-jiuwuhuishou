package com.sqx.app.settlement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.support.RecycleOrderStateMachine;
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
import com.sqx.app.settlement.service.SettlementService;
import com.sqx.common.core.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class SettlementServiceImpl implements SettlementService {

    private static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    private static final String SETTLEMENT_STATUS_CONFIRMED = "CONFIRMED";
    private static final String DISPUTE_STATUS_PENDING = "PENDING";
    private static final String ACTION_SETTLEMENT_CONFIRM = "SETTLEMENT_CONFIRM";
    private static final String ACTION_SETTLEMENT_DISPUTE = "SETTLEMENT_DISPUTE";
    private static final String OPERATOR_TYPE_USER = "USER";

    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Resource
    private IOrderSettlementService iOrderSettlementService;
    @Resource
    private IOrderDisputeService iOrderDisputeService;
    @Resource
    private PayoutService payoutService;

    @Override
    public SettlementInfoVo info(Long userId, SettlementInfoBo infoBo) {
        RecycleOrder order = getOwnedOrder(userId, infoBo.getOrderId());
        OrderSettlement settlement = getSettlement(order.getId());
        SettlementInfoVo vo = new SettlementInfoVo();
        vo.setOrderId(order.getId());
        vo.setSettlementNo(settlement.getSettlementNo());
        vo.setEstimateAmount(settlement.getEstimateAmount());
        vo.setActualAmount(resolveActualAmount(settlement, order));
        vo.setConfirmed(SETTLEMENT_STATUS_CONFIRMED.equals(settlement.getStatus()));
        vo.setStatus(settlement.getStatus());
        vo.setOrderStatusKey(RecycleOrderStatus.of(order.getStatus()).getStatusKey());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SettlementConfirmVo confirm(Long userId, SettlementConfirmBo confirmBo) {
        RecycleOrder order = getOwnedOrder(userId, confirmBo.getOrderId());
        if (!RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许确认成交");
        }

        OrderSettlement settlement = getSettlement(order.getId());
        if (SETTLEMENT_STATUS_CONFIRMED.equals(settlement.getStatus())) {
            throw new BizException("结算已确认，请勿重复操作");
        }

        BigDecimal actualAmount = resolveActualAmount(settlement, order);
        if (Objects.isNull(actualAmount)) {
            throw new BizException("实际成交金额尚未填写，请等待平台确认");
        }
        settlement.setActualAmount(actualAmount);
        settlement.setStatus(SETTLEMENT_STATUS_CONFIRMED);
        settlement.setUpdateTime(LocalDateTime.now());
        iOrderSettlementService.updateById(settlement);

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.WAIT_PAYOUT);
        order.setStatus(RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setStatusDesc("用户已确认成交价，等待打款");
        order.setActualAmount(actualAmount);
        order.setEstimateAmountConfirmed(1);
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_SETTLEMENT_CONFIRM, fromStatus, order.getStatus(),
                "用户确认实际成交价格", userId);

        // 进入打款中，由管理端完成打款；不再自动完结订单
        payoutService.startPayout(order, actualAmount, userId);

        SettlementConfirmVo vo = new SettlementConfirmVo();
        vo.setOrderId(order.getId());
        vo.setOrderStatusKey(RecycleOrderStatus.PAYOUT_PROCESSING.getStatusKey());
        vo.setMessage("已确认成交，等待平台打款");
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispute(Long userId, SettlementDisputeBo disputeBo) {
        RecycleOrder order = getOwnedOrder(userId, disputeBo.getOrderId());
        if (!RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许发起争议");
        }

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.DISPUTE_PENDING);

        OrderDispute dispute = new OrderDispute();
        dispute.setOrderId(order.getId());
        dispute.setUserId(userId);
        dispute.setReason(disputeBo.getReason());
        dispute.setDescription(disputeBo.getDescription());
        dispute.setStatus(DISPUTE_STATUS_PENDING);
        iOrderDisputeService.save(dispute);

        order.setStatus(RecycleOrderStatus.DISPUTE_PENDING.getCode());
        order.setStatusDesc("用户发起价格争议，等待平台处理");
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_SETTLEMENT_DISPUTE, fromStatus, order.getStatus(),
                "用户发起价格争议：" + disputeBo.getReason(), userId);
    }

    private RecycleOrder getOwnedOrder(Long userId, Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order) || !Objects.equals(order.getUserId(), userId)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private OrderSettlement getSettlement(Long orderId) {
        OrderSettlement settlement = iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, orderId)
                .last("LIMIT 1"));
        if (Objects.isNull(settlement)) {
            throw new BizException("结算单不存在，请等待员工完成取件");
        }
        return settlement;
    }

    private BigDecimal resolveActualAmount(OrderSettlement settlement, RecycleOrder order) {
        if (Objects.nonNull(settlement.getActualAmount())) {
            return settlement.getActualAmount();
        }
        if (Objects.nonNull(order.getActualAmount())) {
            return order.getActualAmount();
        }
        // 禁止用预估金额冒充实际成交金额，须由平台/员工手动填写
        return null;
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
        log.setOperatorType(OPERATOR_TYPE_USER);
        iRecycleOrderLogService.save(log);
    }
}
