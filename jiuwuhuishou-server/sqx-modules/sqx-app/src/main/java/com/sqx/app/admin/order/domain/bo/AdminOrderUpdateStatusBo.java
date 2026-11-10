package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminOrderUpdateStatusBo {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @NotBlank(message = "目标状态不能为空")
    @Schema(description = "目标状态码，如 WAIT_ASSIGN / IN_PROGRESS")
    private String targetStatus;

    @Schema(description = "备注")
    private String remark;
}
