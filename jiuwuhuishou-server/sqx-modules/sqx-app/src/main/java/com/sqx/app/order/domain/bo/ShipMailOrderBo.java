package com.sqx.app.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户端邮寄订单发货
 */
@Data
public class ShipMailOrderBo {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "快递单号")
    @NotBlank(message = "快递单号不能为空")
    private String expressNo;

    @Schema(description = "快递名称，默认顺丰速运")
    private String expressCompany;
}
