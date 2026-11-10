package com.sqx.app.admin.user.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminUserWelfareUpdateCoinBo {

    @Schema(description = "用户ID")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @Schema(description = "调整积分数量")
    @NotNull(message = "积分数量不能为空")
    private Integer amount;

    @Schema(description = "方向 incr增加 decr减少")
    @NotNull(message = "调整方向不能为空")
    private String operator;
}
