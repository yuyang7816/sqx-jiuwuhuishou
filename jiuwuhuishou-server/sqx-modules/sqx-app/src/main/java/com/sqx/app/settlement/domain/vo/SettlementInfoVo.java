package com.sqx.app.settlement.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementInfoVo {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "结算单号")
    private String settlementNo;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "实际成交金额")
    private BigDecimal actualAmount;

    @Schema(description = "是否已确认")
    private Boolean confirmed;

    @Schema(description = "结算状态")
    private String status;

    @Schema(description = "订单状态键")
    private String orderStatusKey;
}
