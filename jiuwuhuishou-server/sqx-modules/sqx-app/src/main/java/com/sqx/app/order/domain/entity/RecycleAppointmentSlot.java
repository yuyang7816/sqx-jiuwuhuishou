package com.sqx.app.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回收上门预约时间段
 */
@Getter
@Setter
@TableName("recycle_appointment_slot")
@Schema(name = "RecycleAppointmentSlot", description = "回收上门预约时间段")
public class RecycleAppointmentSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "开始时间，如 10:00")
    @TableField("start_time")
    private String startTime;

    @Schema(description = "结束时间，如 12:00")
    @TableField("end_time")
    private String endTime;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    @TableField("state")
    private Integer state;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
