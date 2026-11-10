package com.sqx.app.referral.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "ReferralCommissionWithdrawResultVo", description = "佣金提现结果")
public class ReferralCommissionWithdrawResultVo {

    @Schema(description = "本次提现金额")
    private BigDecimal withdrawAmount;

    @Schema(description = "剩余佣金")
    private BigDecimal commission;

    @Schema(description = "钱包余额（元）")
    private BigDecimal walletAmount;
}
