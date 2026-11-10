package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminPayoutInfoBo {

    @Schema(description = "打款单ID")
    @NotNull(message = "id不能为空")
    private Long id;
}
