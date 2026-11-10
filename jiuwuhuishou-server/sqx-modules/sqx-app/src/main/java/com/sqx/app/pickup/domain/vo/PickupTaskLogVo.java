package com.sqx.app.pickup.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PickupTaskLogVo {

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "节点标题")
    private String title;

    @Schema(description = "日志内容")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发生时间")
    private LocalDateTime createTime;

    @Schema(description = "是否当前节点")
    private Boolean active;
}
