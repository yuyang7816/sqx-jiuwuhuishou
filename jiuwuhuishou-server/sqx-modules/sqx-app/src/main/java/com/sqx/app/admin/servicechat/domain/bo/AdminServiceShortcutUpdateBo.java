package com.sqx.app.admin.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminServiceShortcutUpdateBo {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "快捷提示词")
    @NotBlank(message = "提示词不能为空")
    private String content;

    @Schema(description = "排序")
    private Integer sort;
}
