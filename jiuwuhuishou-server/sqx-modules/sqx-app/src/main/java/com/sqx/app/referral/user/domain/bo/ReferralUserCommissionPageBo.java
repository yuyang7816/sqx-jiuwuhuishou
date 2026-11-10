package com.sqx.app.referral.user.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReferralUserCommissionPageBo extends PageParam {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;
}
