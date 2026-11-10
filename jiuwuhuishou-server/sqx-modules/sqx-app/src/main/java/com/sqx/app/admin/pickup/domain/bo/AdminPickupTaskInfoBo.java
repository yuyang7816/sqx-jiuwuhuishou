package com.sqx.app.admin.pickup.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminPickupTaskInfoBo {

    @Schema(description = "任务ID")
    @NotNull(message = "id不能为空")
    private Long id;
}
