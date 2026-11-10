package com.sqx.app.admin.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminOrderUpdateEstimateBo {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @NotNull(message = "实际金额不能为空")
    @Schema(description = "实际金额（对应用户端实际成交金额）")
    private BigDecimal actualAmount;

    @Schema(description = "修改说明")
    private String remark;
}
