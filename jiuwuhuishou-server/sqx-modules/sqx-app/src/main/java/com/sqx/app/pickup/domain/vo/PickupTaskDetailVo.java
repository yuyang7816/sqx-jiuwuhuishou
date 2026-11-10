package com.sqx.app.pickup.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PickupTaskDetailVo extends PickupTaskVo {

    @Schema(description = "关联订单号")
    private String recycleOrderNo;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "预估金额下限（家电）")
    private BigDecimal estimateMinAmount;

    @Schema(description = "预估金额上限（家电）")
    private BigDecimal estimateMaxAmount;

    @Schema(description = "实际成交金额（实际打款金额）")
    private BigDecimal actualAmount;

    @Schema(description = "实际金额修改说明")
    private String actualAmountRemark;

    @Schema(description = "是否可开始上门")
    private Boolean canStart;

    @Schema(description = "是否可完成取件")
    private Boolean canFinish;

    @Schema(description = "是否可修改实际打款金额")
    private Boolean canEditActual;

    @Schema(description = "是否可取消订单")
    private Boolean canCancel;

    @Schema(description = "流程节点")
    private List<PickupTaskLogVo> logs;
}
