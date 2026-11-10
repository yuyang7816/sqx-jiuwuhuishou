package com.sqx.app.referral.withdraw.domain.vo;

import com.sqx.app.referral.withdraw.domain.entity.WithdrawalAccount;
import com.sqx.app.admin.support.AdminLookupSupport;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-06-20 14:02:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WithdrawalAccountVo extends WithdrawalAccount implements AdminLookupSupport.UserDisplayFields {

    private String userName;

    private String phone;
}
