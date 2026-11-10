package com.sqx.app.payout.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PayoutInfoVo {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "打款单号")
    private String payoutNo;

    @Schema(description = "打款金额")
    private BigDecimal amount;

    @Schema(description = "打款状态")
    private String status;

    @Schema(description = "打款状态文案")
    private String statusText;

    @Schema(description = "收款方式")
    private String payChannelText;

    @Schema(description = "到账账号")
    private String accountText;

    @Schema(description = "状态记录")
    private List<PayoutLogVo> logs;
}
