package com.sqx.app.review.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReviewInfoVo {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "评分")
    private Integer score;

    @Schema(description = "投诉类型")
    private String complaintType;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "是否已评价")
    private Boolean reviewed;
}
