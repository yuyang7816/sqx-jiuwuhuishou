package com.sqx.app.admin.datacenter.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "品牌合作线索")
public class AdminDatacenterBrandVo {

    @Schema(description = "期内新建线索数")
    private Long periodCreatedCount;

    @Schema(description = "待跟进数（status=0 当前快照）")
    private Long pendingFollowCount;

    @Schema(description = "线索漏斗（当前快照按状态：0待跟进/1已联系/2已合作/3已拒绝）")
    private List<AdminDatacenterStatusCountVo> funnel;
}
