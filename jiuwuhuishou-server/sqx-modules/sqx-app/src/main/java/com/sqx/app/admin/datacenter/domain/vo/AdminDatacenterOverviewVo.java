package com.sqx.app.admin.datacenter.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "数据中心概览")
public class AdminDatacenterOverviewVo {

    @Schema(description = "查询开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "查询结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "环比对照开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prevStartTime;

    @Schema(description = "环比对照结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prevEndTime;

    @Schema(description = "订单量（create_time 落在区间，含取消）")
    private Long orderCount;

    @Schema(description = "成交额 GMV（COMPLETED 且 update_time 落在区间的 actual_amount 之和）")
    private BigDecimal gmv;

    @Schema(description = "完成订单数（create_time 落在区间且当前 COMPLETED）")
    private Long completedCount;

    @Schema(description = "完成率 = 完成数 / 订单量")
    private BigDecimal completionRate;

    @Schema(description = "取消订单数（create_time 落在区间且当前 CANCELLED）")
    private Long cancelledCount;

    @Schema(description = "取消率 = 取消数 / 订单量")
    private BigDecimal cancelRate;

    @Schema(description = "预估金额合计（create_time 落在区间）")
    private BigDecimal estimateAmountSum;

    @Schema(description = "实际金额合计（create_time 落在区间）")
    private BigDecimal actualAmountSum;

    @Schema(description = "价差 = 实际合计 - 预估合计")
    private BigDecimal amountDiff;

    @Schema(description = "待处理积压（当前快照：WAIT_AUDIT/WAIT_ASSIGN/EXCEPTION_PENDING/DISPUTE_PENDING）")
    private Long backlogCount;

    @Schema(description = "新增注册用户数")
    private Long newUserCount;

    @Schema(description = "下单用户数（去重）")
    private Long orderUserCount;

    @Schema(description = "待确认结算金额（结算单 PENDING，快照）")
    private BigDecimal pendingSettlementAmount;

    @Schema(description = "待打款金额（订单 WAIT_PAYOUT/PAYOUT_PROCESSING，快照）")
    private BigDecimal pendingPayoutAmount;

    @Schema(description = "当期打款成功数")
    private Long payoutSuccessCount;

    @Schema(description = "当期打款失败数")
    private Long payoutFailCount;

    @Schema(description = "打款成功率 = 成功 / (成功 + 失败)")
    private BigDecimal payoutSuccessRate;

    @Schema(description = "订单漏斗（create_time 落在区间，按当前状态）")
    private List<AdminDatacenterStatusCountVo> funnel;

    @Schema(description = "环比")
    private AdminDatacenterCompareVo compare;
}
