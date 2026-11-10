package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 家电估价页配置
 */
@Data
public class ApplianceSpecPageVo {

    @Schema(description = "品类列表")
    private List<ApplianceCategoryVo> categories;

    @Schema(description = "当前品类配置")
    private ApplianceSpecConfigVo config;
}
