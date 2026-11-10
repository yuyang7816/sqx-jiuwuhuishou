package com.sqx.app.admin.product.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminProductSpecPageBo extends PageParam {

    @NotNull(message = "型号ID不能为空")
    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "规格名称")
    private String attributeName;
}
