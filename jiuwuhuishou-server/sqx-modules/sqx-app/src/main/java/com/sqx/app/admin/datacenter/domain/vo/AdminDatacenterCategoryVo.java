package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "品类结构项")
public class AdminDatacenterCategoryVo {

    @Schema(description = "order_type")
    private String orderType;

    @Schema(description = "品类中文名")
    private String orderTypeLabel;

    @Schema(description = "期内订单量")
    private Long orderCount;

    @Schema(description = "期内成交额（COMPLETED · update_time）")
    private BigDecimal gmv;
}
