package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminOrderExceptionHandleBo {

    @NotNull(message = "异常记录ID不能为空")
    @Schema(description = "异常记录ID")
    private Long id;

    @NotBlank(message = "处理类型不能为空")
    @Schema(description = "处理类型：RESOLVED=已处理恢复履约，UNRESOLVED=未处理取消订单")
    private String handleType;

    @Schema(description = "处理说明")
    private String remark;
}
