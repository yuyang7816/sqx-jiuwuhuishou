package com.sqx.app.pickup.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PickupTaskStartBo {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID")
    private Long id;
}
