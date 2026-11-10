package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "环比（上一同等时长）")
public class AdminDatacenterCompareVo {

    @Schema(description = "上期订单量")
    private Long orderCount;

    @Schema(description = "订单量环比变化率，无上期则为 null")
    private BigDecimal orderCountChangeRate;

    @Schema(description = "上期成交额")
    private BigDecimal gmv;

    @Schema(description = "成交额环比变化率，无上期则为 null")
    private BigDecimal gmvChangeRate;

    @Schema(description = "上期完成率")
    private BigDecimal completionRate;

    @Schema(description = "上期取消率")
    private BigDecimal cancelRate;

    @Schema(description = "上期新增用户")
    private Long newUserCount;

    @Schema(description = "新增用户环比变化率，无上期则为 null")
    private BigDecimal newUserCountChangeRate;

    @Schema(description = "上期打款成功率")
    private BigDecimal payoutSuccessRate;
}
