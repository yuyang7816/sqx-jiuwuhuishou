package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminSettlementConfirmBo {

    @NotNull(message = "结算单ID不能为空")
    @Schema(description = "结算单ID")
    private Long id;

    @Schema(description = "备注")
    private String remark;
}
