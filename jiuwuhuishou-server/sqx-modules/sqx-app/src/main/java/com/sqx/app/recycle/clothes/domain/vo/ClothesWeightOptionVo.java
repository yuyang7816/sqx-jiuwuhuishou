package com.sqx.app.recycle.clothes.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 旧衣预估重量选项
 */
@Data
public class ClothesWeightOptionVo {

    @Schema(description = "重量区间标题，如 5-20kg")
    private String title;

    @Schema(description = "说明文案")
    private String desc;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "排序")
    private Integer sort;
}
