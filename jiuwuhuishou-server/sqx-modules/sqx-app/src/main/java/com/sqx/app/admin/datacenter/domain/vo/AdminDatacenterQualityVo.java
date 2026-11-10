package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "质量指标")
public class AdminDatacenterQualityVo {

    @Schema(description = "期内异常反馈创建数")
    private Long exceptionCreatedCount;

    @Schema(description = "当前异常处理中订单数（EXCEPTION_PENDING）")
    private Long exceptionOpenCount;

    @Schema(description = "期内争议创建数")
    private Long disputeCreatedCount;

    @Schema(description = "当前未结争议数（PENDING 快照）")
    private Long disputePendingCount;

    @Schema(description = "期内创建争议中已解决数")
    private Long disputeResolvedCount;

    @Schema(description = "争议解决率 = 已解决 / 期内创建")
    private BigDecimal disputeResolveRate;

    @Schema(description = "期内评价数")
    private Long reviewCount;

    @Schema(description = "期内评价均分")
    private BigDecimal reviewAvgScore;

    @Schema(description = "评价回复率（有 admin_reply）")
    private BigDecimal reviewReplyRate;
}
