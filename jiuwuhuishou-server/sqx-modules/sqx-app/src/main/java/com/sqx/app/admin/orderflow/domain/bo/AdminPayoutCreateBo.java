package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminPayoutCreateBo {

    @Schema(description = "订单ID（与 orderNo 二选一）")
    private Long orderId;

    @Schema(description = "订单号（与 orderId 二选一，运营录入优先用订单号）")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;
}
