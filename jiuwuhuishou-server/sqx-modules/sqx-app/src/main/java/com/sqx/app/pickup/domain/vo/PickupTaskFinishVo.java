package com.sqx.app.pickup.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PickupTaskFinishVo {

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "关联订单ID")
    private Long orderId;

    @Schema(description = "订单前端状态键")
    private String orderStatusKey;

    @Schema(description = "提示文案")
    private String message;
}
