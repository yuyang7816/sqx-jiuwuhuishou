package com.sqx.app.admin.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminServiceSessionInfoBo {

    @Schema(description = "会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long id;
}
