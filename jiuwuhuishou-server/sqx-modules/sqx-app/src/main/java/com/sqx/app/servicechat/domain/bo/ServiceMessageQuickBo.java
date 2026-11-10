package com.sqx.app.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServiceMessageQuickBo {

    @NotBlank(message = "问题不能为空")
    @Schema(description = "快捷问题")
    private String question;
}
