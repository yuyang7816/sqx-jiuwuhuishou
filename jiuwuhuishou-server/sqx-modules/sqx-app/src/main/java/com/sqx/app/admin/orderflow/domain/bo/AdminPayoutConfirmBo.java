package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminPayoutConfirmBo {

    @NotNull(message = "打款单ID不能为空")
    @Schema(description = "打款单ID")
    private Long id;

    @Schema(description = "备注")
    private String remark;
}
