package com.sqx.app.base.banner.domain.bo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

/**
 * @author sqx
 * @since 2025-05-16 13:49:04
 */
@Data
public class BannerUpdateBo {

    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "图片地址")
    private String imageUrl;

    @Schema(description = "分类1首页轮播图 2首页底部配置 3其他配置分类 4邀请背景图 5首页分类 6首页公告消息")
    private Integer classify;

    @Schema(description = "跳转地址 ")
    private String url;

    @Schema(description = "顺序")
    private Integer sort;

    @Schema(description = "描述")
    private String description;
}
