package com.sqx.app.settlement.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SettlementDisputeBo {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long orderId;

    @NotBlank(message = "争议原因不能为空")
    @Schema(description = "争议原因")
    private String reason;

    @Schema(description = "争议说明")
    private String description;
}
