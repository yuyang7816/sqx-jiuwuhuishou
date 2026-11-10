package com.sqx.app.admin.recycle.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminRecycleCategorySaveBo {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "编码 clothes/appliance/digital")
    private String code;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;

    @Schema(description = "描述")
    private String description;
}
