package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 家电估价结果
 */
@Data
public class ApplianceEstimateVo {

    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "成色/条件")
    private String condition;

    @Schema(description = "预估价格")
    private BigDecimal estimatePrice;

    @Schema(description = "价格区间下限")
    private BigDecimal lowPrice;

    @Schema(description = "价格区间上限")
    private BigDecimal highPrice;

    @Schema(description = "环保激励奖励（已停用，固定为 0）")
    private Integer reward;
}
