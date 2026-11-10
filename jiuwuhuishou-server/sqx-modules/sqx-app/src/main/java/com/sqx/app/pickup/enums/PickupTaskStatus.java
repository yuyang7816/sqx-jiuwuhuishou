package com.sqx.app.pickup.enums;

import lombok.Getter;

/**
 * 取件任务状态
 */
@Getter
public enum PickupTaskStatus {

    PENDING("pending", "待上门"),
    RUNNING("running", "取件中"),
    DONE("done", "已完成"),
    CANCELLED("cancelled", "已取消");

    private final String key;
    private final String label;

    PickupTaskStatus(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public static PickupTaskStatus fromKey(String key) {
        if (key == null || key.isEmpty() || "all".equalsIgnoreCase(key)) {
            return null;
        }
        for (PickupTaskStatus status : values()) {
            if (status.key.equalsIgnoreCase(key) || status.name().equalsIgnoreCase(key)) {
                return status;
            }
        }
        return null;
    }

    public static PickupTaskStatus fromName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (PickupTaskStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
