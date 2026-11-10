package com.sqx.app.servicechat.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceMessageVo {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型 text/image")
    private String messageType;

    @Schema(description = "发送方 1用户 2客服")
    private Integer senderType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发送时间")
    private LocalDateTime createTime;
}
