package com.sqx.app.referral.commission.domain.vo;

import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-04 14:44:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReferralCommissionVo extends ReferralCommission implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "用户信息")
    private com.sqx.app.base.user.domain.entity.UserEntity userEntity;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "订单号")
    private String orderNo;
}
