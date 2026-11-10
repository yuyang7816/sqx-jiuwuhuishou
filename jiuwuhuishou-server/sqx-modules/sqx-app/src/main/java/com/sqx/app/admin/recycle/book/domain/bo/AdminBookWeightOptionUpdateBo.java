package com.sqx.app.admin.recycle.book.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminBookWeightOptionUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @NotBlank(message = "重量区间不能为空")
    @Schema(description = "重量区间标题，如 5-15kg")
    private String title;

    @Schema(description = "说明文案")
    private String desc;

    @NotNull(message = "预估金额不能为空")
    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
