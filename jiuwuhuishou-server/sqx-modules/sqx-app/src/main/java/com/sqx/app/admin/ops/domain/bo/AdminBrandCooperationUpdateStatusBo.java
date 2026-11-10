package com.sqx.app.admin.ops.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminBrandCooperationUpdateStatusBo {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "状态")
    @NotNull(message = "status不能为空")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
