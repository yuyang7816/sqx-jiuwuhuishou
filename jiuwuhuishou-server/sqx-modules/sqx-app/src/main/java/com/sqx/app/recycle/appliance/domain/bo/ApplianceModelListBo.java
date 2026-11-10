package com.sqx.app.recycle.appliance.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 家电型号列表查询
 */
@Data
public class ApplianceModelListBo {

    @Schema(description = "品类ID")
    @NotNull(message = "品类ID不能为空")
    private Long categoryId;
}
