package com.sqx.app.admin.datacenter.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "数据中心增长与运营（P2）")
public class AdminDatacenterGrowthVo {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "提现")
    private AdminDatacenterWithdrawVo withdraw;

    @Schema(description = "分销")
    private AdminDatacenterReferralVo referral;

    @Schema(description = "福利")
    private AdminDatacenterWelfareVo welfare;

    @Schema(description = "客服")
    private AdminDatacenterServiceChatVo serviceChat;

    @Schema(description = "品牌合作")
    private AdminDatacenterBrandVo brand;

    @Schema(description = "钱包")
    private AdminDatacenterWalletVo wallet;
}
