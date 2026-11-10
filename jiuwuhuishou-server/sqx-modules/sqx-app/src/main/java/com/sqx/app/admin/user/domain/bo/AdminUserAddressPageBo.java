package com.sqx.app.admin.user.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminUserAddressPageBo extends PageParam {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactMobile;

    @Schema(description = "是否默认 0否 1是")
    private Integer isDefault;
}
