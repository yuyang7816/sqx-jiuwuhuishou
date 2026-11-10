package com.sqx.app.wallet.transaction.domain.vo;

import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.admin.support.AdminLookupSupport;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-13 15:12:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserWalletTransactionVo extends UserWalletTransaction implements AdminLookupSupport.UserDisplayFields {

    private String userName;

    private String phone;
}
