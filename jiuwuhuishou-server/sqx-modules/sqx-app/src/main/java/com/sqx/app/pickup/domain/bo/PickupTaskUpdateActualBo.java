package com.sqx.app.pickup.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PickupTaskUpdateActualBo {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID")
    private Long id;

    @NotNull(message = "实际打款金额不能为空")
    @DecimalMin(value = "0", message = "实际打款金额不能小于0")
    @Schema(description = "实际打款金额（对应用户端实际成交金额）")
    private BigDecimal actualAmount;

    @Schema(description = "修改说明")
    private String remark;
}
