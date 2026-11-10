package com.sqx.app.recycle.digital.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 数码/奢侈品搜索结果
 */
@Data
public class DigitalSearchResultVo {

    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "基础估价")
    private BigDecimal basePrice;

    @Schema(description = "规格参数JSON")
    private String specJson;

    @Schema(description = "场景：digital/luxury")
    private String scene;
}
