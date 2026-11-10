package com.sqx.app.admin.product.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminProductModelUpdateBo extends AdminProductModelSaveBo {

    @NotNull(message = "型号ID不能为空")
    @Schema(description = "型号ID")
    private Long id;
}
