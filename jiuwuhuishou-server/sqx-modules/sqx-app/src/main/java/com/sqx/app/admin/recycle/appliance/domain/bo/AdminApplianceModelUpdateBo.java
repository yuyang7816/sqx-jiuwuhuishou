package com.sqx.app.admin.recycle.appliance.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminApplianceModelUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @NotNull(message = "品类ID不能为空")
    @Schema(description = "品类ID")
    private Long categoryId;

    @Schema(description = "型号名称")
    private String name;

    @Schema(description = "规格参数JSON")
    private String specJson;

    @Schema(description = "基础估价")
    private BigDecimal basePrice;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
