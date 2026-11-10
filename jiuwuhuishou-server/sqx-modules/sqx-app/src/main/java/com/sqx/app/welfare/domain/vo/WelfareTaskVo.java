package com.sqx.app.welfare.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "WelfareTaskVo", description = "福利任务")
public class WelfareTaskVo {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "图标图片地址")
    private String icon;

    @Schema(description = "跳转地址")
    private String url;

    @Schema(description = "按钮文案")
    private String btnText;

    @Schema(description = "排序")
    private Integer sort;
}
