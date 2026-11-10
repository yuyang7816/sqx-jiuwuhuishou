package com.sqx.app.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 快递物流轨迹节点
 */
@Data
public class ExpressTrackVo {

    @Schema(description = "节点标题")
    private String title;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "描述")
    private String desc;

    @Schema(description = "是否当前节点")
    private Boolean active;
}
