package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 家电品类概览（含价格卡片展示信息）
 */
@Data
public class ApplianceCategoryOverviewVo {

    @Schema(description = "品类ID")
    private Long id;

    @Schema(description = "品类名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "价格卡片标题")
    private String priceTitle;

    @Schema(description = "最低回收价")
    private BigDecimal priceMin;

    @Schema(description = "最高回收价")
    private BigDecimal priceMax;

    @Schema(description = "价格区间展示文案")
    private String price;

    @Schema(description = "型号列表")
    private List<ApplianceModelVo> models;
}
