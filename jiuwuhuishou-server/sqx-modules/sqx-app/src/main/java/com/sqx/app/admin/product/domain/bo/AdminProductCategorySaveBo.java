package com.sqx.app.admin.product.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminProductCategorySaveBo {

    @NotNull(message = "回收类型不能为空")
    @Schema(description = "回收品类ID")
    private Long recycleCategoryId;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1启用 2禁用")
    private Integer state;
}
