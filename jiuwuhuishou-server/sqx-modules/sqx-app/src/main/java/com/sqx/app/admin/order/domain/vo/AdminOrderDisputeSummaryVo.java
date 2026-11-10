package com.sqx.app.admin.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOrderDisputeSummaryVo {

    @Schema(description = "争议记录ID")
    private Long id;

    @Schema(description = "争议原因")
    private String reason;

    @Schema(description = "争议描述")
    private String description;

    @Schema(description = "争议状态")
    private String status;

    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
