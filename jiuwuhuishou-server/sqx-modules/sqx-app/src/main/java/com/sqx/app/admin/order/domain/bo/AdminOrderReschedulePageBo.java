package com.sqx.app.admin.order.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminOrderReschedulePageBo extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "订单ID（内部筛选用）")
    private Long orderId;
}
