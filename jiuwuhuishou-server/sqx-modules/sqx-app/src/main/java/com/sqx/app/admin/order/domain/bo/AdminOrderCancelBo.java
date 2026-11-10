package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminOrderCancelBo {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "取消原因")
    private String reason;
}
