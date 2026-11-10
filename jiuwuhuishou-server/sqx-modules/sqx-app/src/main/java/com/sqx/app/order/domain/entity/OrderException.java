package com.sqx.app.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单异常反馈
 */
@Getter
@Setter
@TableName("order_exception")
@Schema(name = "OrderException", description = "订单异常反馈")
public class OrderException implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "异常类型")
    @TableField("exception_type")
    private String exceptionType;

    @Schema(description = "期望处理方式")
    @TableField("expect_action")
    private String expectAction;

    @Schema(description = "问题描述")
    @TableField("description")
    private String description;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
