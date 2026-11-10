package com.sqx.app.admin.ops.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminHelpCategoryPageBo extends PageParam {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "客户端类型")
    private Integer clientType;
}
