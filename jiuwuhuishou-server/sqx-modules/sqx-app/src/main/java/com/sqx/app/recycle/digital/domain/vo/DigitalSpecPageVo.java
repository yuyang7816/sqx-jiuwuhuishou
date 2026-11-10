package com.sqx.app.recycle.digital.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数码商品规格页数据
 */
@Data
public class DigitalSpecPageVo {

    @Schema(description = "场景：digital / luxury")
    private String scene;

    @Schema(description = "场景名称")
    private String sceneLabel;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "基础回收价")
    private BigDecimal basePrice;

    @Schema(description = "型号绑定的规格分组（仅 product_spec，不含场景公用模板）")
    private List<DigitalSpecSectionVo> sections;
}
