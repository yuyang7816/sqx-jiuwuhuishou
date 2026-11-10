package com.sqx.app.admin.recycle.digital.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminDigitalPricingVo {

    private Long id;
    private Long modelId;

    @Schema(description = "型号名称")
    private String modelName;

    private String ruleKey;
    private String ruleValue;
    private BigDecimal adjustRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
