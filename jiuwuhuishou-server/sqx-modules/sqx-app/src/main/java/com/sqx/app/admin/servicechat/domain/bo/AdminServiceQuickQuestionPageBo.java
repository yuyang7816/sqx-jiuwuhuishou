package com.sqx.app.admin.servicechat.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminServiceQuickQuestionPageBo extends PageParam {

    @Schema(description = "问题关键词")
    private String question;
}
