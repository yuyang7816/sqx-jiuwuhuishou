package com.sqx.app.order.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 家电回收下单
 */
@Data
public class ApplianceOrderSaveBo {

    @Schema(description = "地址ID")
    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    @Schema(description = "预约时间")
    @NotNull(message = "预约时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    @Schema(description = "家电型号ID")
    @NotNull(message = "型号ID不能为空")
    private Long modelId;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "预估金额下限（家电）")
    private BigDecimal estimateMinAmount;

    @Schema(description = "预估金额上限（家电）")
    private BigDecimal estimateMaxAmount;

    @Schema(description = "履约方式 visit上门/mail邮寄，默认 visit")
    private String fulfillType;

    @Schema(description = "备注")
    private String remark;
}
