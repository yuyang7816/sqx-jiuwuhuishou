package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "数据中心日趋势点")
public class AdminDatacenterTrendPointVo {

    @Schema(description = "日期 yyyy-MM-dd")
    private String date;

    @Schema(description = "当日订单量（create_time）")
    private Long orderCount;

    @Schema(description = "当日成交额（COMPLETED · update_time · actual_amount）")
    private BigDecimal gmv;
}
