package com.sqx.app.base.user.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(name = "ChangePasswordBo", description = "修改密码")
public class ChangePasswordBo {

    @NotBlank(message = "当前密码不能为空")
    @Schema(description = "当前密码")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String newPassword;
}
