package com.sqx.app.admin.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOrderExceptionSummaryVo {

    @Schema(description = "异常记录ID")
    private Long id;

    @Schema(description = "异常类型")
    private String exceptionType;

    @Schema(description = "期望处理方式")
    private String expectAction;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
