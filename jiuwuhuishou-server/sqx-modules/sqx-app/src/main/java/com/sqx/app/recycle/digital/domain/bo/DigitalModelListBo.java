package com.sqx.app.recycle.digital.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 数码型号列表查询
 */
@Data
public class DigitalModelListBo {

    @Schema(description = "品牌ID")
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    @Schema(description = "关键词")
    private String keyword;
}
