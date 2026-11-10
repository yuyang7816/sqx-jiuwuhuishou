package com.sqx.app.recycle.book.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍预约-预估重量选项
 */
@Getter
@Setter
@TableName("book_weight_option")
@Schema(name = "BookWeightOption", description = "书籍预约-预估重量选项")
public class BookWeightOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "重量区间标题，如 5-15kg")
    @TableField("title")
    private String title;

    @Schema(description = "说明文案")
    @JsonProperty("desc")
    @TableField("option_desc")
    private String optionDesc;

    @Schema(description = "预估金额")
    @TableField("estimate_amount")
    private BigDecimal estimateAmount;

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
