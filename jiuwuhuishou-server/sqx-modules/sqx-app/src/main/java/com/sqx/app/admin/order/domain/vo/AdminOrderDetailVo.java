package com.sqx.app.admin.order.domain.vo;

import com.sqx.app.order.domain.vo.RecycleOrderLogVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminOrderDetailVo extends AdminOrderVo {

    @Schema(description = "订单日志列表")
    private List<RecycleOrderLogVo> logs;

    @Schema(description = "改约记录")
    private List<AdminOrderRescheduleVo> reschedules;

    @Schema(description = "异常反馈")
    private List<AdminOrderExceptionVo> exceptions;

    @Schema(description = "争议记录")
    private List<AdminOrderDisputeSummaryVo> disputes;

    @Schema(description = "评价/投诉记录")
    private List<AdminOrderReviewSummaryVo> reviews;
}
