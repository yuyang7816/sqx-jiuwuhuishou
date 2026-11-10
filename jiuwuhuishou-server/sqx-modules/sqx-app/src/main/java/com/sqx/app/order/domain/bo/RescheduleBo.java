package com.sqx.app.order.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 订单改约
 */
@Data
public class RescheduleBo {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "改约原因")
    @NotBlank(message = "改约原因不能为空")
    private String reason;

    @Schema(description = "新预约时间")
    @NotNull(message = "新预约时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime newAppointmentTime;

    @Schema(description = "补充说明")
    private String remark;
}
