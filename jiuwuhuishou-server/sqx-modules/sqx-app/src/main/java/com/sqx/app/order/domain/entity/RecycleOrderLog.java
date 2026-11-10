package com.sqx.app.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回收订单日志
 */
@Getter
@Setter
@TableName("recycle_order_log")
@Schema(name = "RecycleOrderLog", description = "回收订单日志")
public class RecycleOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "动作类型")
    @TableField("action_type")
    private String actionType;

    @Schema(description = "原状态")
    @TableField("from_status")
    private String fromStatus;

    @Schema(description = "新状态")
    @TableField("to_status")
    private String toStatus;

    @Schema(description = "日志内容")
    @TableField("content")
    private String content;

    @Schema(description = "操作人ID")
    @TableField("operator_id")
    private Long operatorId;

    @Schema(description = "操作人类型 USER/STAFF/SYSTEM")
    @TableField("operator_type")
    private String operatorType;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
