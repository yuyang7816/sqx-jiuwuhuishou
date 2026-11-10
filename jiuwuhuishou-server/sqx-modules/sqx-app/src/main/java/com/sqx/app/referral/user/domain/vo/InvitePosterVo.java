package com.sqx.app.referral.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邀请海报素材
 */
@Data
public class InvitePosterVo {

    @Schema(description = "海报背景图（管理端首页装修-邀请背景图）")
    private String backgroundImageUrl;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "邀请码")
    private String invitationCode;

    @Schema(description = "邀请落地页路径")
    private String invitePath;

    @Schema(description = "小程序码图片（data:image/png;base64,...），生成失败时为空")
    private String qrCodeImage;

    @Schema(description = "海报标题")
    private String title;

    @Schema(description = "海报文案")
    private String description;
}
