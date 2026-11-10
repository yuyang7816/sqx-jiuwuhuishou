package com.sqx.app.recycle.digital.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数码品牌
 */
@Getter
@Setter
@TableName("digital_brand")
@Schema(name = "DigitalBrand", description = "数码品牌")
public class DigitalBrand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "品牌名称")
    @TableField("name")
    private String name;

    @Schema(description = "品牌Logo")
    @TableField("logo")
    private String logo;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    @TableField("state")
    private Integer state;

    @Schema(description = "场景：digital/luxury")
    @TableField("scene")
    private String scene;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
