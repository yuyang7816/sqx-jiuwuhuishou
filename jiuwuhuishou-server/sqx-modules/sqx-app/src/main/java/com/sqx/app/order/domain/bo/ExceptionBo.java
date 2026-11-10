package com.sqx.app.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单异常反馈
 */
@Data
public class ExceptionBo {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "异常类型")
    @NotBlank(message = "异常类型不能为空")
    private String exceptionType;

    @Schema(description = "期望处理方式")
    @NotBlank(message = "期望处理方式不能为空")
    private String expectAction;

    @Schema(description = "问题描述")
    private String description;
}
