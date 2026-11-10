package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "分销佣金 / 邀请")
public class AdminDatacenterReferralVo {

    @Schema(description = "期内佣金发放额（type=订单佣金 且 direction=收入）")
    private BigDecimal commissionAmount;

    @Schema(description = "期内佣金笔数")
    private Long commissionCount;

    @Schema(description = "期内邀请新增用户数（superior_invitation_code 非空）")
    private Long inviteNewUserCount;
}
