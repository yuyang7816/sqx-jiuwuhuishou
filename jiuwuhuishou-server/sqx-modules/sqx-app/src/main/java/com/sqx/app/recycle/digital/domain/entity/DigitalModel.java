package com.sqx.app.recycle.digital.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数码型号
 */
@Getter
@Setter
@TableName("digital_model")
@Schema(name = "DigitalModel", description = "数码型号")
public class DigitalModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "品牌ID")
    @TableField("brand_id")
    private Long brandId;

    @Schema(description = "型号名称")
    @TableField("name")
    private String name;

    @Schema(description = "规格参数JSON")
    @TableField("spec_json")
    private String specJson;

    @Schema(description = "基础估价")
    @TableField("base_price")
    private BigDecimal basePrice;

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
