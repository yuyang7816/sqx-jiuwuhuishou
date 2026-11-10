package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminOrderExceptionInfoBo {

    @NotNull(message = "异常记录ID不能为空")
    @Schema(description = "异常记录ID")
    private Long id;
}
