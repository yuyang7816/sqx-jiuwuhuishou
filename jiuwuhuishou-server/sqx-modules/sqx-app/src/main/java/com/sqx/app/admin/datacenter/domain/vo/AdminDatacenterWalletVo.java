package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "钱包汇总")
public class AdminDatacenterWalletVo {

    @Schema(description = "平台钱包余额合计（user_wallet.amount 快照）")
    private BigDecimal balanceSum;

    @Schema(description = "期内流入合计（direction=1）")
    private BigDecimal inflowAmount;

    @Schema(description = "期内流出合计（direction=2）")
    private BigDecimal outflowAmount;

    @Schema(description = "期内净流入 = 流入 - 流出")
    private BigDecimal netInflowAmount;
}
