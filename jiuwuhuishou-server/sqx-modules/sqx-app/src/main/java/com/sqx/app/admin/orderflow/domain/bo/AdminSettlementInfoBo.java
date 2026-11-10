package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminSettlementInfoBo {

    @Schema(description = "结算单ID")
    @NotNull(message = "id不能为空")
    private Long id;
}
