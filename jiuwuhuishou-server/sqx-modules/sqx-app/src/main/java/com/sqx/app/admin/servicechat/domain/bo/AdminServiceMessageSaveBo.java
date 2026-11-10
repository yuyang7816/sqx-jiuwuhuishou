package com.sqx.app.admin.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminServiceMessageSaveBo {

    @Schema(description = "会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    @Schema(description = "消息类型 text/image")
    private String messageType;

    @Schema(description = "消息内容（文本或图片相对路径）")
    @NotBlank(message = "消息内容不能为空")
    private String content;
}
