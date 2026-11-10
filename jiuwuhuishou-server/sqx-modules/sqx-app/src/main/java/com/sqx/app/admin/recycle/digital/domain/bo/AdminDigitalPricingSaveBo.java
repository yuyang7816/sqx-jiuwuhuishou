package com.sqx.app.admin.recycle.digital.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminDigitalPricingSaveBo {

    @NotNull(message = "型号ID不能为空")
    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "规则键")
    private String ruleKey;

    @Schema(description = "规则值")
    private String ruleValue;

    @Schema(description = "调整比例")
    private BigDecimal adjustRate;
}
