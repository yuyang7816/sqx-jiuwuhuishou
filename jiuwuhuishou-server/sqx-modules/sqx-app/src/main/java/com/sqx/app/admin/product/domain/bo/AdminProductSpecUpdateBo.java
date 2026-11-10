package com.sqx.app.admin.product.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminProductSpecUpdateBo extends AdminProductSpecSaveBo {

    @NotNull(message = "ID不能为空")
    private Long id;
}
