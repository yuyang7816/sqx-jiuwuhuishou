package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 家电回收流程步骤（用户端）
 */
@Data
public class ApplianceHeroFlowVo {

    @Schema(description = "步骤名称")
    private String name;

    @Schema(description = "图标图片地址（兼容旧版图标名）")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;
}
