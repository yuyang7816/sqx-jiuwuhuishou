package com.sqx.app.admin.recycle.appliance.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminApplianceHeroFlowUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @NotBlank(message = "步骤名称不能为空")
    @Schema(description = "步骤名称，如 线上预约")
    private String name;

    @NotBlank(message = "图标不能为空")
    @Schema(description = "图标图片地址")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
