package com.sqx.app.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单改约记录
 */
@Getter
@Setter
@TableName("order_reschedule")
@Schema(name = "OrderReschedule", description = "订单改约记录")
public class OrderReschedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "改约原因")
    @TableField("reason")
    private String reason;

    @Schema(description = "新预约时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("new_appointment_time")
    private LocalDateTime newAppointmentTime;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
