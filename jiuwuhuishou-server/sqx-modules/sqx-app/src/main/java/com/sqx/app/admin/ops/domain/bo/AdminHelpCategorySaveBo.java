package com.sqx.app.admin.ops.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminHelpCategorySaveBo {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "客户端类型")
    private Integer clientType;

    @Schema(description = "排序")
    private Integer sort;
}
