package com.sqx.app.admin.recycle.digital.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminDigitalPricingPageBo extends PageParam {

    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "规则键")
    private String ruleKey;
}
