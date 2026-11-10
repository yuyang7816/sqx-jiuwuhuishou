package com.sqx.app.recycle.clothes.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 旧衣预估配置
 */
@Data
public class ClothesEstimateConfigVo {

    @Schema(description = "预估重量选项")
    private List<ClothesWeightOptionVo> weights;
}
