package com.sqx.app.admin.servicechat.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminServiceSessionPageBo extends PageParam {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "会话状态 1进行中 2已关闭")
    private Integer status;

    @Schema(description = "仅看待回复（末条为用户消息）")
    private Boolean pendingReply;
}
