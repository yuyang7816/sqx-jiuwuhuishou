package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 家电品牌及型号
 */
@Data
public class ApplianceSpecBrandVo {

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "型号列表")
    private List<ApplianceSpecModelVo> models;
}
