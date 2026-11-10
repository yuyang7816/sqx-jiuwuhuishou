package com.sqx.app.admin.recycle.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminRecycleCategoryPageBo extends PageParam {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "状态 1上架 2下架")
    private Integer state;
}
