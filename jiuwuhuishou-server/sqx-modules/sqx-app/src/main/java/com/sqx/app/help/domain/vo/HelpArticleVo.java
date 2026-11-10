package com.sqx.app.help.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HelpArticleVo {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "分类标签")
    private String tag;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "列表摘要")
    private String summary;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
