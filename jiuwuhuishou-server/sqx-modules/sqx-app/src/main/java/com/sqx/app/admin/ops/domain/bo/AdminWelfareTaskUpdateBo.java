package com.sqx.app.admin.ops.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminWelfareTaskUpdateBo {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标图片地址")
    private String icon;

    @Schema(description = "跳转链接")
    private String url;

    @Schema(description = "按钮文案")
    private String btnText;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1启用 2禁用")
    private Integer state;
}
