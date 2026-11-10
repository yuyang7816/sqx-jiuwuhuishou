package com.sqx.app.admin.orderflow.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminReviewReplyBo {

    @NotNull(message = "评价ID不能为空")
    @Schema(description = "评价ID")
    private Long id;

    @NotBlank(message = "回复内容不能为空")
    @Schema(description = "客服回复")
    private String adminReply;
}
