package com.sqx.app.admin.ops.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminBrandCooperationPageBo extends PageParam {

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态")
    private Integer status;
}
