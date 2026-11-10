package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单状态计数")
public class AdminDatacenterStatusCountVo {

    @Schema(description = "状态码")
    private String status;

    @Schema(description = "状态中文")
    private String statusLabel;

    @Schema(description = "数量")
    private Long count;
}
