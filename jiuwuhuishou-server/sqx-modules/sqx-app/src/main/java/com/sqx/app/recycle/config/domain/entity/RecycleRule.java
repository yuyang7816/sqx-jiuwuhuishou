package com.sqx.app.recycle.config.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回收规则
 */
@Getter
@Setter
@TableName("recycle_rule")
@Schema(name = "RecycleRule", description = "回收规则")
public class RecycleRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "标题")
    @TableField("title")
    private String title;

    @Schema(description = "规则内容")
    @TableField("content")
    private String content;

    @Schema(description = "规则类型")
    @TableField("rule_type")
    private String ruleType;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "1启用 2禁用")
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
