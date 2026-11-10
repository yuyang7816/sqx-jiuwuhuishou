package com.sqx.app.admin.product.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminProductCategoryPageBo extends PageParam {

    @Schema(description = "回收品类ID")
    private Long recycleCategoryId;

    @Schema(description = "分类名称")
    private String name;
}
