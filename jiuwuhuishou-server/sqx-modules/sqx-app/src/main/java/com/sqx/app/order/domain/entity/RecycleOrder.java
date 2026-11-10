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
 * 回收订单
 */
@Getter
@Setter
@TableName("recycle_order")
@Schema(name = "RecycleOrder", description = "回收订单")
public class RecycleOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单号")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "订单类型 clothes/appliance/digital")
    @TableField("order_type")
    private String orderType;

    @Schema(description = "履约方式 visit上门/mail邮寄")
    @TableField("fulfill_type")
    private String fulfillType;

    @Schema(description = "品类描述")
    @TableField("category_name")
    private String categoryName;

    @Schema(description = "预约时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;

    @Schema(description = "地址ID")
    @TableField("address_id")
    private Long addressId;

    @Schema(description = "联系人")
    @TableField("contact_name")
    private String contactName;

    @Schema(description = "联系电话")
    @TableField("contact_mobile")
    private String contactMobile;

    @Schema(description = "完整地址快照（上门地址）")
    @TableField("address_text")
    private String addressText;

    @Schema(description = "邮寄地址快照（仓库收件地址）")
    @TableField("mail_address_text")
    private String mailAddressText;

    @Schema(description = "快递单号（邮寄订单）")
    @TableField("express_no")
    private String expressNo;

    @Schema(description = "预估金额")
    @TableField("estimate_amount")
    private BigDecimal estimateAmount;

    @Schema(description = "预估金额下限（家电）")
    @TableField("estimate_min_amount")
    private BigDecimal estimateMinAmount;

    @Schema(description = "预估金额上限（家电）")
    @TableField("estimate_max_amount")
    private BigDecimal estimateMaxAmount;

    @Schema(description = "实际金额")
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    @Schema(description = "实际金额修改说明")
    @TableField("actual_amount_remark")
    private String actualAmountRemark;

    @Schema(description = "预估/预付金额是否已确认")
    @TableField("estimate_amount_confirmed")
    private Integer estimateAmountConfirmed;

    @Schema(description = "订单状态")
    @TableField("status")
    private String status;

    @Schema(description = "状态描述")
    @TableField("status_desc")
    private String statusDesc;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
