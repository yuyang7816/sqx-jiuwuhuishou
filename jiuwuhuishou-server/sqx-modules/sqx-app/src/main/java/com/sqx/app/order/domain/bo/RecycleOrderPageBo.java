package com.sqx.app.order.domain.bo;

import com.sqx.common.mybatis.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回收订单分页查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecycleOrderPageBo extends PageParam {

    @Schema(description = "用户ID", hidden = true)
    private Long userId;

    @Schema(description = "状态分组键 waitAssign/running/pendingConfirm/paying/done/cancelled/exception/dispute")
    private String statusKey;

    @Schema(description = "订单类型 clothes/appliance/digital")
    private String orderType;
}
