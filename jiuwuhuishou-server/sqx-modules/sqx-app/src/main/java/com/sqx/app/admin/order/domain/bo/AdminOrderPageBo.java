package com.sqx.app.admin.order.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminOrderPageBo extends PageParam {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态分组键 waitAssign/running/pendingConfirm/paying/done/cancelled/exception/dispute")
    private String statusKey;

    @Schema(description = "订单类型 clothes/appliance/digital")
    private String orderType;

    @Schema(description = "履约方式 visit上门/mail邮寄")
    private String fulfillType;

    @Schema(description = "订单状态码")
    private String status;
}
