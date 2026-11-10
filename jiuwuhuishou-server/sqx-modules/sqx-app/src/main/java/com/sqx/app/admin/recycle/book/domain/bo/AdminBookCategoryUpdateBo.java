package com.sqx.app.admin.recycle.book.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminBookCategoryUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
