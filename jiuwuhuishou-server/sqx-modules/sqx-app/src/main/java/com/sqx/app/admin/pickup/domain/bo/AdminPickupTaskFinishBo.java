package com.sqx.app.admin.pickup.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminPickupTaskFinishBo {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "取件任务ID")
    private Long id;

    @Schema(description = "完成备注")
    private String remark;
}
