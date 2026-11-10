package com.sqx.app.admin.product.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminProductModelSaveBo {

    @NotNull(message = "品牌ID不能为空")
    @Schema(description = "品牌ID")
    private Long brandId;

    @NotBlank(message = "型号名称不能为空")
    @Schema(description = "型号名称")
    private String name;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "基础估价（数码/奢侈品使用；家电由最低/最高估值中值回写）")
    private BigDecimal basePrice;

    @Schema(description = "最低估值（仅家电）")
    private BigDecimal minPrice;

    @Schema(description = "最高估值（仅家电）")
    private BigDecimal maxPrice;

    @Schema(description = "规格参数JSON")
    private String specJson;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1启用 2禁用")
    private Integer state;
}
