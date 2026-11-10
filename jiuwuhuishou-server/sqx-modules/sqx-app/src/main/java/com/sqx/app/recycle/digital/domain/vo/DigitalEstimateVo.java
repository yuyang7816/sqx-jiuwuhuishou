package com.sqx.app.recycle.digital.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 数码估价结果
 */
@Data
public class DigitalEstimateVo {

    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "成色/条件")
    private String condition;

    @Schema(description = "预估价格")
    private BigDecimal estimatePrice;
}
