package com.sqx.app.address.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除用户地址
 */
@Data
public class UserAddressDeleteBo {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;
}
