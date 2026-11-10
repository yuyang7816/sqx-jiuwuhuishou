package com.sqx.app.admin.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminServiceShortcutSaveBo {

    @Schema(description = "快捷提示词")
    @NotBlank(message = "提示词不能为空")
    private String content;

    @Schema(description = "排序")
    private Integer sort;
}
