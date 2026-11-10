package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 家电估价参数配置
 */
@Data
public class ApplianceSpecConfigVo {

    @Schema(description = "品类ID")
    private Long categoryId;

    @Schema(description = "品类名称")
    private String categoryName;

    @Schema(description = "页面标题")
    private String title;

    @Schema(description = "页面副标题")
    private String subtitle;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "卡片标签")
    private String cardLabel;

    @Schema(description = "环保奖励")
    private Integer reward;

    @Schema(description = "品牌及型号")
    private List<ApplianceSpecBrandVo> brands;

    @Schema(description = "品类级规格字段（已废弃，固定为空；规格仅挂在型号上）")
    private List<ApplianceSpecFieldVo> fields;

    @Schema(description = "品类级损伤选项（已废弃，固定为空；选项仅挂在型号上）")
    private List<String> damageOptions;

    @Schema(description = "品类级新旧程度选项（已废弃，固定为空；选项仅挂在型号上）")
    private List<String> conditionOptions;

    @Schema(description = "品类级使用年限选项（已废弃，固定为空；选项仅挂在型号上）")
    private List<String> ageOptions;
}
