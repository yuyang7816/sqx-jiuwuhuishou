package com.sqx.app.admin.servicechat.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminServiceQuickQuestionSaveBo {

    @Schema(description = "问题")
    @NotBlank(message = "问题不能为空")
    private String question;

    @Schema(description = "自动回复内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "1展示在欢迎区 0仅自动回复")
    private Integer showWelcome;

    @Schema(description = "排序")
    private Integer sort;
}
