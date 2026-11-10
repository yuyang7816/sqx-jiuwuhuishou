package com.sqx.app.admin.product.domain.bo;

import com.sqx.app.admin.product.domain.vo.ProductSpecAttributeVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AdminProductSpecSaveBo {

    @NotNull(message = "型号ID不能为空")
    @Schema(description = "型号ID")
    private Long modelId;

    @NotBlank(message = "规格名称不能为空")
    @Schema(description = "规格名称")
    private String attributeName;

    @Schema(description = "规格属性值")
    private List<ProductSpecAttributeVo> attributes;

    @Schema(description = "是否多选 0否1是")
    private Integer multiSelect;

    @Schema(description = "排序")
    private Integer sort;
}
