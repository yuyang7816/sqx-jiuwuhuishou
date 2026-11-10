package com.sqx.app.admin.product.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminProductSpecTemplateBo {

    @NotNull(message = "型号ID不能为空")
    @Schema(description = "型号ID")
    private Long modelId;
}
