package com.sqx.app.admin.recycle.digital.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminDigitalBrandSaveBo {

    @Schema(description = "场景 digital/luxury，默认 digital")
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
