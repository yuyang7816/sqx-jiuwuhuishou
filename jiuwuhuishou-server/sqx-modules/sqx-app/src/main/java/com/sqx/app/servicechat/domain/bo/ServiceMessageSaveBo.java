package com.sqx.app.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServiceMessageSaveBo {

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;

    @NotBlank(message = "消息类型不能为空")
    @Schema(description = "消息类型 text/image")
    private String messageType;
}
