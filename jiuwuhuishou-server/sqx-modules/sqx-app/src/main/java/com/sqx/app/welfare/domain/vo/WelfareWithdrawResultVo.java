package com.sqx.app.welfare.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "WelfareWithdrawResultVo", description = "福利中心提现结果")
public class WelfareWithdrawResultVo {

    @Schema(description = "本次提现金币")
    private Integer withdrawAmount;

    @Schema(description = "剩余金币余额")
    private Integer coinBalance;

    @Schema(description = "钱包余额（元）")
    private BigDecimal walletAmount;
}
