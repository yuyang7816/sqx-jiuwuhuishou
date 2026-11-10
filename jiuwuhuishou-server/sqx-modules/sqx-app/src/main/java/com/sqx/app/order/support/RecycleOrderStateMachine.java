package com.sqx.app.order.support;

import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.common.core.exception.BizException;

import java.util.*;

/**
 * 订单状态机
 */
public final class RecycleOrderStateMachine {

    private static final Map<RecycleOrderStatus, Set<RecycleOrderStatus>> TRANSITIONS = new EnumMap<>(RecycleOrderStatus.class);

    static {
        allow(RecycleOrderStatus.CREATED, RecycleOrderStatus.WAIT_AUDIT, RecycleOrderStatus.WAIT_ASSIGN, RecycleOrderStatus.CANCELLED);
        allow(RecycleOrderStatus.WAIT_AUDIT, RecycleOrderStatus.WAIT_ASSIGN, RecycleOrderStatus.CANCELLED, RecycleOrderStatus.EXCEPTION_PENDING);
        allow(RecycleOrderStatus.WAIT_ASSIGN, RecycleOrderStatus.IN_PROGRESS, RecycleOrderStatus.CANCELLED, RecycleOrderStatus.EXCEPTION_PENDING);
        allow(RecycleOrderStatus.IN_PROGRESS, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM, RecycleOrderStatus.CANCELLED, RecycleOrderStatus.EXCEPTION_PENDING);
        allow(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM, RecycleOrderStatus.WAIT_PAYOUT, RecycleOrderStatus.DISPUTE_PENDING, RecycleOrderStatus.EXCEPTION_PENDING, RecycleOrderStatus.CANCELLED);
        allow(RecycleOrderStatus.DISPUTE_PENDING, RecycleOrderStatus.WAIT_PAYOUT, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM, RecycleOrderStatus.CANCELLED);
        allow(RecycleOrderStatus.WAIT_PAYOUT, RecycleOrderStatus.PAYOUT_PROCESSING);
        allow(RecycleOrderStatus.PAYOUT_PROCESSING, RecycleOrderStatus.COMPLETED);
        allow(RecycleOrderStatus.EXCEPTION_PENDING, RecycleOrderStatus.WAIT_ASSIGN, RecycleOrderStatus.CANCELLED, RecycleOrderStatus.IN_PROGRESS);
    }

    private RecycleOrderStateMachine() {
    }

    private static void allow(RecycleOrderStatus from, RecycleOrderStatus... targets) {
        TRANSITIONS.put(from, EnumSet.copyOf(Arrays.asList(targets)));
    }

    public static void assertCanTransition(String fromCode, RecycleOrderStatus target) {
        RecycleOrderStatus from = RecycleOrderStatus.of(fromCode);
        Set<RecycleOrderStatus> allowed = TRANSITIONS.getOrDefault(from, Collections.emptySet());
        if (!allowed.contains(target)) {
            throw new BizException(String.format("订单状态不允许从[%s]变更为[%s]", from.getLabel(), target.getLabel()));
        }
    }

    public static boolean canCancel(String statusCode) {
        RecycleOrderStatus status = RecycleOrderStatus.of(statusCode);
        Set<RecycleOrderStatus> allowed = TRANSITIONS.getOrDefault(status, Collections.emptySet());
        return allowed.contains(RecycleOrderStatus.CANCELLED);
    }

    public static boolean canReschedule(String statusCode) {
        // 待确认已进入结算环节，不允许改约；取消能力见 canCancel
        if (RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.equals(RecycleOrderStatus.of(statusCode))) {
            return false;
        }
        return canCancel(statusCode);
    }
}
