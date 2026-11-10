package com.sqx.app.wallet.wallet.domain.vo;

import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.admin.support.AdminLookupSupport;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-13 15:09:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserWalletVo extends UserWallet implements AdminLookupSupport.UserDisplayFields {

    private String userName;

    private String phone;
}
