package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 家电型号选项
 */
@Data
public class ApplianceSpecModelVo {

    @Schema(description = "型号ID")
    private Long id;

    @Schema(description = "型号名称")
    private String name;

    @Schema(description = "基础估价（兼容，一般为最低/最高中值）")
    private BigDecimal basePrice;

    @Schema(description = "最低估值")
    private BigDecimal minPrice;

    @Schema(description = "最高估值")
    private BigDecimal maxPrice;

    @Schema(description = "规格参数默认值")
    private Map<String, String> specs;

    @Schema(description = "当前型号绑定的规格选项（仅 product_spec，不含品类公用模板）")
    private List<ApplianceSpecFieldVo> fields;

    @Schema(description = "当前型号绑定的损伤选项")
    private List<String> damageOptions;

    @Schema(description = "当前型号绑定的新旧程度选项")
    private List<String> conditionOptions;

    @Schema(description = "当前型号绑定的使用年限选项")
    private List<String> ageOptions;
}
