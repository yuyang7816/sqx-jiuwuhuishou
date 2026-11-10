package com.sqx.app.admin.product.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductSpecAttributeVo {

    @Schema(description = "属性值ID")
    private String id;

    @Schema(description = "属性值名称")
    private String attName;

    @Schema(description = "属性估值")
    private String attPrice;

    @Schema(description = "属性图片")
    private String img;

    @Schema(description = "规格ID")
    private String specId;
}
