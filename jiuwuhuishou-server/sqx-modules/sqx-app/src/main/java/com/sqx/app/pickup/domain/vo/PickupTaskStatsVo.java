package com.sqx.app.pickup.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupTaskStatsVo {

    @Schema(description = "待上门数量")
    private Long pendingCount;

    @Schema(description = "进行中数量")
    private Long runningCount;

    @Schema(description = "已完成数量")
    private Long doneCount;
}
