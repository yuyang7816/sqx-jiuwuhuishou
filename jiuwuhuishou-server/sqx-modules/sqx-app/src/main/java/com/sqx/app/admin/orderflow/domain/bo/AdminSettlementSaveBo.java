package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminSettlementSaveBo {

    @NotNull(message = "结算单ID不能为空")
    @Schema(description = "结算单ID")
    private Long id;

    @NotNull(message = "实际金额不能为空")
    @Schema(description = "实际成交金额")
    private BigDecimal actualAmount;

    @Schema(description = "备注")
    private String remark;
}
