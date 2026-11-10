package com.sqx.app.admin.ops.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminHelpCategoryUpdateBo {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "客户端类型")
    private Integer clientType;

    @Schema(description = "排序")
    private Integer sort;
}
