package com.sqx.app.admin.recycle.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminRecycleIdBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;
}
