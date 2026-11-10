package com.sqx.app.pickup.enums;

import lombok.Getter;

/**
 * 取件任务动作
 */
@Getter
public enum PickupTaskAction {

    ASSIGN("任务已分配", "系统已将任务分配至当前员工"),
    START("开始上门", "员工开始前往上门地址"),
    FINISH("完成取件", "员工已完成上门取件交接"),
    UPDATE_ACTUAL("修改实际金额", "员工已修改实际打款金额"),
    CANCEL("取消订单", "员工已取消关联订单");

    private final String title;
    private final String defaultContent;

    PickupTaskAction(String title, String defaultContent) {
        this.title = title;
        this.defaultContent = defaultContent;
    }
}
