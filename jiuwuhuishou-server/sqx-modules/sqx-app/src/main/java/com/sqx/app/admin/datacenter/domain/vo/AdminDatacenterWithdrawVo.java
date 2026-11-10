package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "提现（待审 + 漏斗）")
public class AdminDatacenterWithdrawVo {

    @Schema(description = "待审提现笔数（status=1 当前快照）")
    private Long pendingCount;

    @Schema(description = "待审提现金额（status=1 当前快照）")
    private BigDecimal pendingAmount;

    @Schema(description = "期内发起笔数（create_time 落在区间）")
    private Long periodCreatedCount;

    @Schema(description = "期内成功笔数（create_time 落在区间且 status=2）")
    private Long periodSuccessCount;

    @Schema(description = "期内拒绝笔数（create_time 落在区间且 status=3）")
    private Long periodRejectCount;

    @Schema(description = "期内成功金额")
    private BigDecimal periodSuccessAmount;

    @Schema(description = "提现漏斗（期内创建按当前状态：1待审/2成功/3拒绝）")
    private List<AdminDatacenterStatusCountVo> funnel;
}
