package com.sqx.app.admin.recycle.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminRecycleRuleUpdateBo {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "规则内容")
    private String content;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1启用 2禁用")
    private Integer state;
}
