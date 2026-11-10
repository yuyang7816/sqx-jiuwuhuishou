package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "福利签到 / 金币池")
public class AdminDatacenterWelfareVo {

    @Schema(description = "期内签到 UV（distinct user_id，sign_date 落在区间）")
    private Long signUv;

    @Schema(description = "期内签到次数")
    private Long signCount;

    @Schema(description = "期内签到发放金币合计")
    private Long periodCoinRewardSum;

    @Schema(description = "金币池余额合计（user_welfare.coin_balance 快照）")
    private Long coinPoolBalance;
}
