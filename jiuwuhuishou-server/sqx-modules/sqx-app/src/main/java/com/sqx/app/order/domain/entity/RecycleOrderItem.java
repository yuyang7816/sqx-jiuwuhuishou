package com.sqx.app.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 回收订单明细
 */
@Getter
@Setter
@TableName("recycle_order_item")
@Schema(name = "RecycleOrderItem", description = "回收订单明细")
public class RecycleOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "明细类型")
    @TableField("item_type")
    private String itemType;

    @Schema(description = "明细名称")
    @TableField("item_name")
    private String itemName;

    @Schema(description = "明细值/规格")
    @TableField("item_value")
    private String itemValue;

    @Schema(description = "数量/重量")
    @TableField("quantity")
    private BigDecimal quantity;

    @Schema(description = "单位")
    @TableField("unit")
    private String unit;

    @Schema(description = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
