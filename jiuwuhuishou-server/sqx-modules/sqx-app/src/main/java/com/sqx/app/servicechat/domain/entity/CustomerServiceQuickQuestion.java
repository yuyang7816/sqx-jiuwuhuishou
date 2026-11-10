package com.sqx.app.servicechat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("customer_service_quick_question")
@Schema(name = "CustomerServiceQuickQuestion", description = "客服-快捷问题与自动回复")
public class CustomerServiceQuickQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "问题")
    @TableField("question")
    private String question;

    @Schema(description = "自动回复内容")
    @TableField("content")
    private String content;

    @Schema(description = "1展示在欢迎区 0仅自动回复")
    @TableField("show_welcome")
    private Integer showWelcome;

    @Schema(description = "排序，越小越靠前")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
