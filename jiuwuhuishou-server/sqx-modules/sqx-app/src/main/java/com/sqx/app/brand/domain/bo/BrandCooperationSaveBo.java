package com.sqx.app.brand.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class BrandCooperationSaveBo {

    @Schema(description = "提交用户ID", hidden = true)
    private Long userId;

    @NotBlank(message = "品牌名称不能为空")
    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "品牌图片")
    private String brandImage;

    @NotBlank(message = "联系人不能为空")
    @Schema(description = "联系人")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "联系电话格式不正确")
    @Schema(description = "联系电话")
    private String mobile;

    @NotBlank(message = "省份不能为空")
    @Schema(description = "省")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Schema(description = "市")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Schema(description = "区")
    private String district;

    @NotBlank(message = "联系地址不能为空")
    @Schema(description = "详细地址")
    private String address;
}
