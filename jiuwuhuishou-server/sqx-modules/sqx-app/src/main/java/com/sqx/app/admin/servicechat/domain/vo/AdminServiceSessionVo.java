package com.sqx.app.admin.servicechat.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminServiceSessionVo implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "会话ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "状态 1进行中 2已关闭")
    private Integer status;

    @Schema(description = "最近消息预览")
    private String lastMessagePreview;

    @Schema(description = "最近消息类型 text/image")
    private String lastMessageType;

    @Schema(description = "最近发送方 1用户 2客服")
    private Integer lastSenderType;

    @Schema(description = "是否待回复（末条为用户消息）")
    private Boolean pendingReply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最近消息时间")
    private LocalDateTime lastMessageTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
