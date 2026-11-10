package com.sqx.app.admin.ops.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminWelfareUserPageBo extends PageParam {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;
}
