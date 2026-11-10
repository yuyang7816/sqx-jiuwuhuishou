package com.sqx.app.settlement.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettlementConfirmVo {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单状态键")
    private String orderStatusKey;

    @Schema(description = "提示文案")
    private String message;
}
