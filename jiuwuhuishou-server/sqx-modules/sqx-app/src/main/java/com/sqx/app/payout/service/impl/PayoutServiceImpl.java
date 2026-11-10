package com.sqx.app.payout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.support.RecycleOrderStateMachine;
import com.sqx.app.payout.db.service.IOrderPayoutLogService;
import com.sqx.app.payout.db.service.IOrderPayoutService;
import com.sqx.app.payout.domain.bo.PayoutInfoBo;
import com.sqx.app.payout.domain.entity.OrderPayout;
import com.sqx.app.payout.domain.entity.OrderPayoutLog;
import com.sqx.app.payout.domain.vo.PayoutInfoVo;
import com.sqx.app.payout.domain.vo.PayoutLogVo;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.referral.user.service.ReferralUserCommissionService;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.GenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PayoutServiceImpl implements PayoutService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String ACTION_PAYOUT = "PAYOUT";
    private static final String OPERATOR_TYPE_SYSTEM = "SYSTEM";
    private static final String PAY_CHANNEL_WALLET = "WALLET";
    private static final String PAY_CHANNEL_TEXT = "用户钱包";

    /** 交易类型: 5=系统调整 */
    private static final int TX_TYPE_SYSTEM = 5;
    /** 业务类型: 3=活动奖励（回收打款入账） */
    private static final int BIZ_TYPE_REWARD = 3;
    private static final int DIRECTION_IN = 1;
    private static final int TX_STATUS_DONE = 2;
    private static final String WALLET_TX_TITLE = "回收订单打款到账";

    @Resource
    private IOrderPayoutService iOrderPayoutService;
    @Resource
    private IOrderPayoutLogService iOrderPayoutLogService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Resource
    private ReferralUserCommissionService referralUserCommissionService;
    @Resource
    private IUserWalletService iUserWalletService;
    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;

    @Override
    public PayoutInfoVo info(Long userId, PayoutInfoBo infoBo) {
        RecycleOrder order = getOwnedOrder(userId, infoBo.getOrderId());
        OrderPayout payout = iOrderPayoutService.getOne(new LambdaQueryWrapper<OrderPayout>()
                .eq(OrderPayout::getOrderId, order.getId())
                .last("LIMIT 1"));
        if (Objects.isNull(payout)) {
            throw new BizException("打款单不存在，请先确认成交");
        }

        PayoutInfoVo vo = new PayoutInfoVo();
        vo.setOrderId(order.getId());
        vo.setPayoutNo(payout.getPayoutNo());
        vo.setAmount(payout.getAmount());
        vo.setStatus(payout.getStatus());
        vo.setStatusText(resolveStatusText(payout.getStatus()));
        vo.setPayChannelText(PAY_CHANNEL_TEXT);
        vo.setAccountText(PAY_CHANNEL_TEXT);
        vo.setLogs(buildLogs(payout));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAndComplete(RecycleOrder order, BigDecimal amount, Long operatorId) {
        OrderPayout existing = iOrderPayoutService.getOne(new LambdaQueryWrapper<OrderPayout>()
                .eq(OrderPayout::getOrderId, order.getId())
                .last("LIMIT 1"));
        if (Objects.nonNull(existing)) {
            if (STATUS_SUCCESS.equals(existing.getStatus())) {
                return;
            }
            completePayout(order, existing, operatorId);
            return;
        }

        OrderPayout payout = new OrderPayout();
        payout.setPayoutNo("PO" + GenUtil.genOrderNo());
        payout.setOrderId(order.getId());
        payout.setUserId(order.getUserId());
        payout.setAmount(amount);
        payout.setPayChannel(PAY_CHANNEL_WALLET);
        payout.setStatus(STATUS_PROCESSING);
        payout.setRemark("用户确认成交后自动发起打款");
        iOrderPayoutService.save(payout);
        appendPayoutLog(payout.getId(), STATUS_PROCESSING, "平台已发起打款");

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.PAYOUT_PROCESSING);
        order.setStatus(RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        order.setStatusDesc(RecycleOrderStatus.PAYOUT_PROCESSING.getLabel());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_PAYOUT, fromStatus, order.getStatus(), "平台打款处理中", operatorId);

        completePayout(order, payout, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startPayout(RecycleOrder order, BigDecimal amount, Long operatorId) {
        OrderPayout existing = iOrderPayoutService.getOne(new LambdaQueryWrapper<OrderPayout>()
                .eq(OrderPayout::getOrderId, order.getId())
                .last("LIMIT 1"));
        if (Objects.nonNull(existing)) {
            if (STATUS_SUCCESS.equals(existing.getStatus())) {
                throw new BizException("打款已完成");
            }
            if (STATUS_PROCESSING.equals(existing.getStatus())) {
                return;
            }
            existing.setAmount(amount);
            existing.setPayChannel(PAY_CHANNEL_WALLET);
            existing.setStatus(STATUS_PROCESSING);
            existing.setUpdateTime(LocalDateTime.now());
            iOrderPayoutService.updateById(existing);
            appendPayoutLog(existing.getId(), STATUS_PROCESSING, "平台已发起打款");
        } else {
            OrderPayout payout = new OrderPayout();
            payout.setPayoutNo("PO" + GenUtil.genOrderNo());
            payout.setOrderId(order.getId());
            payout.setUserId(order.getUserId());
            payout.setAmount(amount);
            payout.setPayChannel(PAY_CHANNEL_WALLET);
            payout.setStatus(STATUS_PROCESSING);
            payout.setRemark("确认成交后发起打款");
            iOrderPayoutService.save(payout);
            appendPayoutLog(payout.getId(), STATUS_PROCESSING, "平台已发起打款");
        }

        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.PAYOUT_PROCESSING);
        order.setStatus(RecycleOrderStatus.PAYOUT_PROCESSING.getCode());
        order.setStatusDesc(RecycleOrderStatus.PAYOUT_PROCESSING.getLabel());
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);
        appendOrderLog(order.getId(), ACTION_PAYOUT, fromStatus, order.getStatus(), "平台打款处理中", operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishPayout(RecycleOrder order, Long operatorId) {
        OrderPayout payout = iOrderPayoutService.getOne(new LambdaQueryWrapper<OrderPayout>()
                .eq(OrderPayout::getOrderId, order.getId())
                .last("LIMIT 1"));
        if (Objects.isNull(payout)) {
            throw new BizException("打款单不存在");
        }
        if (STATUS_SUCCESS.equals(payout.getStatus())) {
            return;
        }
        completePayout(order, payout, operatorId);
    }

    private void completePayout(RecycleOrder order, OrderPayout payout, Long operatorId) {
        payout.setPayChannel(PAY_CHANNEL_WALLET);
        payout.setStatus(STATUS_SUCCESS);
        payout.setSuccessTime(LocalDateTime.now());
        payout.setUpdateTime(LocalDateTime.now());
        iOrderPayoutService.updateById(payout);
        appendPayoutLog(payout.getId(), STATUS_SUCCESS, "打款成功，已转入用户钱包");

        creditUserWallet(order, payout);

        String fromStatus = order.getStatus();
        if (RecycleOrderStatus.PAYOUT_PROCESSING.getCode().equals(fromStatus)) {
            RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.COMPLETED);
            order.setStatus(RecycleOrderStatus.COMPLETED.getCode());
            order.setStatusDesc(RecycleOrderStatus.COMPLETED.getLabel());
            order.setUpdateTime(LocalDateTime.now());
            iRecycleOrderService.updateById(order);
            appendOrderLog(order.getId(), ACTION_PAYOUT, fromStatus, order.getStatus(), "打款已完成，订单已闭环", operatorId);
            referralUserCommissionService.saveUserCommission(order.getId());
        }
    }

    /**
     * 打款完成时将成交金额入账到订单用户钱包，并写入钱包明细。
     */
    private void creditUserWallet(RecycleOrder order, OrderPayout payout) {
        BigDecimal amount = payout.getAmount();
        if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("打款金额无效");
        }
        Long userId = order.getUserId();
        if (Objects.isNull(userId)) {
            throw new BizException("订单用户不存在");
        }

        // 同一订单仅入账一次，避免重复确认打款导致重复加款
        long exists = iUserWalletTransactionService.count(new LambdaQueryWrapper<UserWalletTransaction>()
                .eq(UserWalletTransaction::getUserId, userId)
                .eq(UserWalletTransaction::getBizId, order.getId())
                .eq(UserWalletTransaction::getBizType, BIZ_TYPE_REWARD)
                .eq(UserWalletTransaction::getDirection, DIRECTION_IN)
                .eq(UserWalletTransaction::getTitle, WALLET_TX_TITLE));
        if (exists > 0) {
            log.warn("订单打款钱包入账已存在，跳过重复入账 orderId={}, userId={}", order.getId(), userId);
            return;
        }

        getOrCreateUserWallet(userId);
        iUserWalletService.incr(userId, amount);

        UserWalletTransaction transaction = new UserWalletTransaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TX_TYPE_SYSTEM);
        transaction.setBizType(BIZ_TYPE_REWARD);
        transaction.setBizId(order.getId());
        transaction.setDirection(DIRECTION_IN);
        transaction.setStatus(TX_STATUS_DONE);
        transaction.setTitle(WALLET_TX_TITLE);
        String orderNo = Objects.nonNull(order.getOrderNo()) ? order.getOrderNo() : String.valueOf(order.getId());
        transaction.setDescription(String.format("回收订单 %s 打款到钱包: ￥%s", orderNo, amount.toPlainString()));
        try {
            iUserWalletTransactionService.save(transaction);
        } catch (Exception e) {
            log.warn("保存回收打款钱包流水失败，orderId={}, userId={}, amount={}", order.getId(), userId, amount, e);
            throw new BizException("钱包明细写入失败，请重试");
        }
    }

    private UserWallet getOrCreateUserWallet(Long userId) {
        UserWallet userWallet = iUserWalletService.getOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("LIMIT 1"));
        if (Objects.nonNull(userWallet)) {
            return userWallet;
        }
        userWallet = new UserWallet();
        userWallet.setUserId(userId);
        userWallet.setAmount(BigDecimal.ZERO);
        iUserWalletService.save(userWallet);
        return userWallet;
    }

    private List<PayoutLogVo> buildLogs(OrderPayout payout) {
        List<OrderPayoutLog> logs = iOrderPayoutLogService.list(new LambdaQueryWrapper<OrderPayoutLog>()
                .eq(OrderPayoutLog::getPayoutId, payout.getId())
                .orderByAsc(OrderPayoutLog::getCreateTime));
        List<PayoutLogVo> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(logs)) {
            return voList;
        }
        for (int i = 0; i < logs.size(); i++) {
            OrderPayoutLog logItem = logs.get(i);
            PayoutLogVo vo = new PayoutLogVo();
            vo.setTitle(logItem.getContent());
            vo.setCreateTime(logItem.getCreateTime());
            vo.setActive(i == logs.size() - 1);
            voList.add(vo);
        }
        return voList;
    }

    private String resolveStatusText(String status) {
        if (STATUS_SUCCESS.equals(status)) {
            return "已到账";
        }
        if (STATUS_PROCESSING.equals(status)) {
            return "打款中";
        }
        if (STATUS_PENDING.equals(status)) {
            return "待打款";
        }
        return "处理中";
    }

    private RecycleOrder getOwnedOrder(Long userId, Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order) || !Objects.equals(order.getUserId(), userId)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private void appendPayoutLog(Long payoutId, String status, String content) {
        OrderPayoutLog logItem = new OrderPayoutLog();
        logItem.setPayoutId(payoutId);
        logItem.setStatus(status);
        logItem.setContent(content);
        iOrderPayoutLogService.save(logItem);
    }

    private void appendOrderLog(Long orderId, String actionType, String fromStatus, String toStatus,
                                 String content, Long operatorId) {
        RecycleOrderLog logItem = new RecycleOrderLog();
        logItem.setOrderId(orderId);
        logItem.setActionType(actionType);
        logItem.setFromStatus(fromStatus);
        logItem.setToStatus(toStatus);
        logItem.setContent(content);
        logItem.setOperatorId(operatorId);
        logItem.setOperatorType(OPERATOR_TYPE_SYSTEM);
        iRecycleOrderLogService.save(logItem);
    }
}
