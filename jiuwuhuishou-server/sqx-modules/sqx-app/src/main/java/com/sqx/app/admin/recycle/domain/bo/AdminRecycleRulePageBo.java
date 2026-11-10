package com.sqx.app.admin.recycle.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminRecycleRulePageBo extends PageParam {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "1启用 2禁用")
    private Integer state;
}
