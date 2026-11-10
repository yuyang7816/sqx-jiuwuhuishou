package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminOrderHandleBo {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @NotBlank(message = "处理类型不能为空")
    @Schema(description = "处理类型：RESOLVED已处理 / UNRESOLVED未处理")
    private String handleType;

    @Schema(description = "处理说明")
    private String remark;
}
