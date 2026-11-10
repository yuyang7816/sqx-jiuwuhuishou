package com.sqx.app.admin.product.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminProductModelPageBo extends PageParam {

    @NotNull(message = "品牌ID不能为空")
    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "型号名称")
    private String name;
}
