package com.sqx.app.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单回收员信息
 */
@Data
public class OrderCourierVo {

    @Schema(description = "回收员姓名")
    private String name;

    @Schema(description = "联系电话")
    private String mobile;

    @Schema(description = "脱敏电话")
    private String mobileMask;
}
