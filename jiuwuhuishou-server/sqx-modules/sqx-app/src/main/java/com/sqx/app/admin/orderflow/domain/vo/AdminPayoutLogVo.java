package com.sqx.app.admin.orderflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPayoutLogVo {

    @Schema(description = "状态")
    private String status;

    @Schema(description = "内容")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "时间")
    private LocalDateTime createTime;
}
