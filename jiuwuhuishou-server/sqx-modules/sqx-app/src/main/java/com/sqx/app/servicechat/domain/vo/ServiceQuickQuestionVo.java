package com.sqx.app.servicechat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ServiceQuickQuestionVo", description = "客服快捷问题")
public class ServiceQuickQuestionVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "自动回复内容")
    private String content;
}
