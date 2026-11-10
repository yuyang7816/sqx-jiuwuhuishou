package com.sqx.app.recycle.appliance.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 家电估价规则
 */
@Getter
@Setter
@TableName("appliance_pricing_rule")
@Schema(name = "AppliancePricingRule", description = "家电估价规则")
public class AppliancePricingRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "型号ID")
    @TableField("model_id")
    private Long modelId;

    @Schema(description = "规则键")
    @TableField("rule_key")
    private String ruleKey;

    @Schema(description = "规则值")
    @TableField("rule_value")
    private String ruleValue;

    @Schema(description = "调整比例")
    @TableField("adjust_rate")
    private BigDecimal adjustRate;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
