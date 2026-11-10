package com.sqx.app.welfare.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "WelfareSummaryVo", description = "福利中心概览")
public class WelfareSummaryVo {

    @Schema(description = "金币余额")
    private Integer coinBalance;

    @Schema(description = "连续签到天数")
    private Integer continuousSignDays;

    @Schema(description = "是否开启连续签到福利")
    private Boolean continuousSignEnabled;

    @Schema(description = "今日是否已签到")
    private Boolean signedToday;
}
