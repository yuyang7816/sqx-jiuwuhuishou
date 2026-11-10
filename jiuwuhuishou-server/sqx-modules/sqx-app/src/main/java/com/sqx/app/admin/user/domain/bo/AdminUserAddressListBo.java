package com.sqx.app.admin.user.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminUserAddressListBo {

    @Schema(description = "用户ID")
    @NotNull(message = "用户id不能为空")
    private Long userId;
}
