package com.sqx.app.recycle.appliance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 家电规格字段
 */
@Data
public class ApplianceSpecFieldVo {

    @Schema(description = "字段key")
    private String key;

    @Schema(description = "字段名称")
    private String label;

    @Schema(description = "可选项")
    private List<String> options;

    @Schema(description = "是否多选：0否 1是")
    private Integer multiSelect;
}
