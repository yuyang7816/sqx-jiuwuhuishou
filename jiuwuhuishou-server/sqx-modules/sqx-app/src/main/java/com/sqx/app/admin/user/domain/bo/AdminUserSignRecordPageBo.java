package com.sqx.app.admin.user.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminUserSignRecordPageBo extends PageParam {

    @Schema(description = "用户ID")
    @NotNull(message = "用户id不能为空")
    private Long userId;
}
