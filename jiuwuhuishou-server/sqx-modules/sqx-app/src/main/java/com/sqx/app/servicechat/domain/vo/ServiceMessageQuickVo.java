package com.sqx.app.servicechat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ServiceMessageQuickVo {

    @Schema(description = "用户消息")
    private ServiceMessageVo userMessage;

    @Schema(description = "客服自动回复")
    private ServiceMessageVo replyMessage;

    @Schema(description = "是否转人工")
    private Boolean transferring;

    @Schema(description = "转人工提示文案")
    private String transferHint;
}
