package com.sqx.app.admin.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOrderReviewSummaryVo {

    @Schema(description = "评价记录ID")
    private Long id;

    @Schema(description = "评分")
    private Integer score;

    @Schema(description = "投诉类型")
    private String complaintType;

    @Schema(description = "评价/投诉内容")
    private String content;

    @Schema(description = "客服回复")
    private String adminReply;

    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
