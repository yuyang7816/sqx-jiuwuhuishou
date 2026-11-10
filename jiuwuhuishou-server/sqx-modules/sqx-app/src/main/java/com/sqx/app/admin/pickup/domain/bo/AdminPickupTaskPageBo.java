package com.sqx.app.admin.pickup.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminPickupTaskPageBo extends PageParam {

    @Schema(description = "取件员工用户ID")
    private Long staffId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "状态键 pending/running/done/cancelled")
    private String statusKey;

    @Schema(description = "员工姓名")
    private String staffName;
}
