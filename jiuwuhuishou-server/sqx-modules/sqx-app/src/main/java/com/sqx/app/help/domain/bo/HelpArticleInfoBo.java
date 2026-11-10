package com.sqx.app.help.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HelpArticleInfoBo {

    @NotNull(message = "文章ID不能为空")
    @Schema(description = "文章ID")
    private Long id;
}
