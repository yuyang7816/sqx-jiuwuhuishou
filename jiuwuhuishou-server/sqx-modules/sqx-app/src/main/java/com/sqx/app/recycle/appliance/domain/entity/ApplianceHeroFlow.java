package com.sqx.app.recycle.appliance.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 家电回收-首页流程步骤
 */
@Getter
@Setter
@TableName("appliance_hero_flow")
@Schema(name = "ApplianceHeroFlow", description = "家电回收-首页流程步骤")
public class ApplianceHeroFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "步骤名称，如 线上预约")
    @TableField("name")
    private String name;

    @Schema(description = "图标图片地址")
    @TableField("icon")
    private String icon;

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
