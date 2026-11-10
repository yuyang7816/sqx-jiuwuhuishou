package com.sqx.app.admin.recycle.digital.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminDigitalBrandUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "场景 digital/luxury")
    private String scene;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌Logo")
    private String logo;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
