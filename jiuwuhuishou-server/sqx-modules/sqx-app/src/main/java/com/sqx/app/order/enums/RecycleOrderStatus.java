package com.sqx.app.order.enums;

import lombok.Getter;

/**
 * 回收订单状态
 */
@Getter
public enum RecycleOrderStatus {

    CREATED("CREATED", "已创建", "running"),
    WAIT_AUDIT("WAIT_AUDIT", "待审核", "running"),
    WAIT_ASSIGN("WAIT_ASSIGN", "待分配", "waitAssign"),
    IN_PROGRESS("IN_PROGRESS", "进行中", "running"),
    WAIT_SETTLEMENT_CONFIRM("WAIT_SETTLEMENT_CONFIRM", "待确认", "pendingConfirm"),
    WAIT_PAYOUT("WAIT_PAYOUT", "待打款", "paying"),
    PAYOUT_PROCESSING("PAYOUT_PROCESSING", "打款中", "paying"),
    COMPLETED("COMPLETED", "已完成", "done"),
    CANCELLED("CANCELLED", "已取消", "cancelled"),
    EXCEPTION_PENDING("EXCEPTION_PENDING", "异常处理中", "exception"),
    DISPUTE_PENDING("DISPUTE_PENDING", "争议处理中", "dispute");

    private final String code;
    private final String label;
    private final String statusKey;

    RecycleOrderStatus(String code, String label, String statusKey) {
        this.code = code;
        this.label = label;
        this.statusKey = statusKey;
    }

    public static RecycleOrderStatus of(String code) {
        for (RecycleOrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不支持的订单状态: " + code);
    }
}
