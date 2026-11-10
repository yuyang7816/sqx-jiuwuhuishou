package com.sqx.app.servicechat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ServiceShortcutVo", description = "客服快捷提示词")
public class ServiceShortcutVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "快捷提示词")
    private String content;
}
