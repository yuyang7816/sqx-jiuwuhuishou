package com.sqx.app.payout.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayoutLogVo {

    @Schema(description = "节点标题")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发生时间")
    private LocalDateTime createTime;

    @Schema(description = "是否当前节点")
    private Boolean active;
}
