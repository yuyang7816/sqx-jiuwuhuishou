package com.sqx.app.base.user.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chengming
 * @since 2025/4/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListUserBO extends PageParam {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "状态 1正常 2禁用")
    private Integer status;

    @Schema(description = "用户类型 0未认证 1普通用户 3员工")
    private Integer userType;

    @Schema(description = "上级邀请码")
    private String superiorInvitationCode;

    @Schema(description = "邀请码")
    private String invitationCode;

    @Schema(description = "注册渠道：H5 / mp / app")
    private String platform;
}
