package com.sqx.app.pickup.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PickupTaskVo {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务编号")
    private String taskNo;

    @Schema(description = "关联订单ID")
    private Long orderId;

    @Schema(description = "任务状态: PENDING/RUNNING/DONE/CANCELLED")
    private String status;

    @Schema(description = "前端状态键: pending/running/done")
    private String statusKey;

    @Schema(description = "状态文案")
    private String statusText;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "预约时间")
    private LocalDateTime appointmentTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "预约结束时间（时段）")
    private LocalDateTime appointmentEndTime;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactMobile;

    @Schema(description = "取件地址")
    private String addressText;

    @Schema(description = "回收品类")
    private String categoryName;

    @Schema(description = "物品摘要")
    private String itemSummary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "完成时间")
    private LocalDateTime finishedAt;
}
