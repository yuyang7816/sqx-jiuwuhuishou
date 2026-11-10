package com.sqx.app.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 回收订单详情
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecycleOrderDetailVo extends RecycleOrderVo {

    @Schema(description = "订单日志列表")
    private List<RecycleOrderLogVo> logs;
}
