package com.sqx.app.referral.commission.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 推荐排行榜条目
 */
@Data
public class ReferralLeaderboardVo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "邀请人数")
    private Long inviteCount;

    @Schema(description = "累计佣金")
    private BigDecimal amount;
}
