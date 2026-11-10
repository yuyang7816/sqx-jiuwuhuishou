package com.sqx.app.admin.pickup.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminPickupTaskAssignBo {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "取件任务ID")
    private Long id;

    @NotNull(message = "取件员工ID不能为空")
    @Schema(description = "取件员工用户ID（userType=3）")
    private Long staffId;
}
