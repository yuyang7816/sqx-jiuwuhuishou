package com.sqx.app.admin.product.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminProductBrandPageBo extends PageParam {

    @NotNull(message = "商品分类ID不能为空")
    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "品牌名称")
    private String name;
}
