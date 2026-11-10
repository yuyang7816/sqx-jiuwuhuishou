package com.sqx.app.recycle.digital.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数码规格分组
 */
@Data
public class DigitalSpecSectionVo {

    @Schema(description = "字段key")
    private String key;

    @Schema(description = "分组标题")
    private String label;

    @Schema(description = "可选项")
    private List<String> options;

    @Schema(description = "布局：grid / column")
    private String layout;
}
