package com.sqx.app.pickup.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PickupTaskPageBo extends PageParam {

    @Schema(description = "员工用户ID，由服务端注入")
    private Long staffId;

    @Schema(description = "状态筛选: all/pending/running/done")
    private String statusKey;
}
