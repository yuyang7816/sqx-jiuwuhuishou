package com.sqx.app.welfare.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(name = "WelfareContinuousSignBo", description = "连续签到开关设置")
public class WelfareContinuousSignBo {

    @NotNull(message = "开关状态不能为空")
    @Schema(description = "是否开启连续签到福利")
    private Boolean enabled;
}
