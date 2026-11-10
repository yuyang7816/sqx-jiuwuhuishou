package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客服会话")
public class AdminDatacenterServiceChatVo {

    @Schema(description = "进行中会话积压（status=1 当前快照）")
    private Long openSessionCount;

    @Schema(description = "已关闭会话数（status=2 当前快照）")
    private Long closedSessionCount;

    @Schema(description = "期内新建会话数")
    private Long periodCreatedCount;
}
