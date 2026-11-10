package com.sqx.app.order.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 回收订单详情查询
 */
@Data
public class RecycleOrderInfoBo {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long id;
}
