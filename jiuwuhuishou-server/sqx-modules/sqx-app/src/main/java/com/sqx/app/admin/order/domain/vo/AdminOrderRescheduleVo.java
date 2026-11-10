package com.sqx.app.admin.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOrderRescheduleVo implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "改约记录ID")
    private Long id;

    @Schema(description = "订单ID（内部跳转用，列表不展示）")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "改约原因")
    private String reason;

    @Schema(description = "新预约时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime newAppointmentTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
