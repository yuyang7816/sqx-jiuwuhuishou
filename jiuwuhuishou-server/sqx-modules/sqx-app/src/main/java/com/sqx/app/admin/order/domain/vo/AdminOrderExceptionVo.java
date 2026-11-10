package com.sqx.app.admin.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOrderExceptionVo implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "异常记录ID")
    private Long id;

    @Schema(description = "订单ID（内部跳转用，列表不展示）")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "异常类型")
    private String exceptionType;

    @Schema(description = "期望处理方式")
    private String expectAction;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "订单当前状态")
    private String orderStatus;

    @Schema(description = "订单状态描述")
    private String orderStatusDesc;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
