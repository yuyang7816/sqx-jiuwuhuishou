package com.sqx.app.pickup.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("pickup_task")
@Schema(name = "PickupTask", description = "取件任务")
public class PickupTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_no")
    private String taskNo;

    @TableField("order_id")
    private Long orderId;

    @TableField("staff_id")
    private Long staffId;

    @TableField("status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_mobile")
    private String contactMobile;

    @TableField("address_text")
    private String addressText;

    @TableField("category_name")
    private String categoryName;

    @TableField("item_summary")
    private String itemSummary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("finished_at")
    private LocalDateTime finishedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
