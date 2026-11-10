package com.sqx.app.base.user.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(name = "UserMessageNotifyBo", description = "用户消息通知开关设置")
public class UserMessageNotifyBo {

    @NotNull(message = "开关状态不能为空")
    @Schema(description = "是否接受消息通知")
    private Boolean enabled;
}
