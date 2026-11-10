package com.sqx.app.welfare.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "WelfareSignResultVo", description = "签到结果")
public class WelfareSignResultVo {

    @Schema(description = "本次签到奖励金币")
    private Integer coinReward;

    @Schema(description = "连续签到天数")
    private Integer continuousSignDays;

    @Schema(description = "金币余额")
    private Integer coinBalance;
}
