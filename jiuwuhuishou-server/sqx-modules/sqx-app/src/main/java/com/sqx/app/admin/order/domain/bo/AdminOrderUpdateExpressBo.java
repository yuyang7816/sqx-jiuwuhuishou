package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AdminOrderUpdateExpressBo {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @NotBlank(message = "快递单号不能为空")
    @Size(max = 64, message = "快递单号长度不能超过64")
    @Schema(description = "快递单号")
    private String expressNo;
}
