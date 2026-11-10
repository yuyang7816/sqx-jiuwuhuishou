package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminDisputeResolveBo {

    @NotNull(message = "争议ID不能为空")
    @Schema(description = "争议ID")
    private Long id;

    /**
     * PROCEED=维持原价回待确认；ADJUST=调整金额回待确认；CANCEL=关闭订单
     */
    @NotBlank(message = "处理方式不能为空")
    @Schema(description = "处理方式：PROCEED/ADJUST/CANCEL")
    private String action;

    @Schema(description = "调整后实际金额（ADJUST 时必填）")
    private BigDecimal actualAmount;

    @Schema(description = "处理备注")
    private String remark;
}
