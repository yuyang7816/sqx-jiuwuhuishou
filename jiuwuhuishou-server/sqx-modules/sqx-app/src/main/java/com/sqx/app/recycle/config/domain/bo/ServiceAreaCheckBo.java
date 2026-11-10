package com.sqx.app.recycle.config.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 服务区域校验
 */
@Data
public class ServiceAreaCheckBo {

    @Schema(description = "省")
    @NotBlank(message = "省不能为空")
    private String province;

    @Schema(description = "市")
    @NotBlank(message = "市不能为空")
    private String city;

    @Schema(description = "区")
    private String district;
}
