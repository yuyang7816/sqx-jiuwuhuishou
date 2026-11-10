package com.sqx.app.welfare.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(name = "WelfareWithdrawBo", description = "福利中心提现")
public class WelfareWithdrawBo {

    @NotNull(message = "提现金额不能为空")
    @Schema(description = "提现金币数量")
    private BigDecimal amount;
}
