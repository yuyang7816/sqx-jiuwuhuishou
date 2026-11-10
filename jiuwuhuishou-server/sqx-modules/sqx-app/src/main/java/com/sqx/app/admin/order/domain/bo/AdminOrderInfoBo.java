package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminOrderInfoBo {

    @Schema(description = "订单ID")
    @NotNull(message = "id不能为空")
    private Long id;
}
