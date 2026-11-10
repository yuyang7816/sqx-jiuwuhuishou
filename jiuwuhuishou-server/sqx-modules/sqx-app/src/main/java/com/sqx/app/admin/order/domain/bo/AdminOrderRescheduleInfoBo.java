package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminOrderRescheduleInfoBo {

    @NotNull(message = "改约记录ID不能为空")
    @Schema(description = "改约记录ID")
    private Long id;
}
