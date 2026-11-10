package com.sqx.app.admin.recycle.appliance.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminApplianceCategoryUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "品类名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "价格卡片展示标题")
    private String displayTitle;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
