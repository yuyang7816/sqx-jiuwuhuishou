package com.sqx.app.base.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UserMessageNotifyVo", description = "用户消息通知开关")
public class UserMessageNotifyVo {

    @Schema(description = "是否接受消息通知")
    private Boolean messageNotifyEnabled;
}
