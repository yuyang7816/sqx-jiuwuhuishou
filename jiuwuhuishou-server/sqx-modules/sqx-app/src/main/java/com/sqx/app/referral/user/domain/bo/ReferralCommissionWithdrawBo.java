package com.sqx.app.referral.user.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(name = "ReferralCommissionWithdrawBo", description = "佣金提现到钱包")
public class ReferralCommissionWithdrawBo {

    @NotNull(message = "提现金额不能为空")
    @Schema(description = "提现金额（元）")
    private BigDecimal amount;
}
