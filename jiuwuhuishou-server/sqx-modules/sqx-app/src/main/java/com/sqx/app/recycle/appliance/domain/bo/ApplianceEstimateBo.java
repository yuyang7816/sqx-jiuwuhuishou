package com.sqx.app.recycle.appliance.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 家电估价请求
 */
@Data
public class ApplianceEstimateBo {

    @Schema(description = "型号ID")
    @NotNull(message = "型号ID不能为空")
    private Long modelId;

    @Schema(description = "成色/条件")
    private String condition;

    @Schema(description = "扩展参数")
    private Map<String, String> params;

    @Schema(description = "损伤情况")
    private String damage;

    @Schema(description = "使用年限")
    private String age;
}
