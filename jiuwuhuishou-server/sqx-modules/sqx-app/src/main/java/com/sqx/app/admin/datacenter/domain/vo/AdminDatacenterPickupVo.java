package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "取件履约")
public class AdminDatacenterPickupVo {

    @Schema(description = "待上门（当前快照）")
    private Long pendingCount;

    @Schema(description = "取件中（当前快照）")
    private Long runningCount;

    @Schema(description = "已完成（当前快照）")
    private Long doneCount;

    @Schema(description = "超时未完成：预约时间已过且仍 PENDING/RUNNING")
    private Long overdueCount;

    @Schema(description = "期内完成任务数（finished_at 落在区间）")
    private Long periodDoneCount;

    @Schema(description = "期内完成任务平均耗时（小时，create_time→finished_at）")
    private BigDecimal avgFinishHours;
}
