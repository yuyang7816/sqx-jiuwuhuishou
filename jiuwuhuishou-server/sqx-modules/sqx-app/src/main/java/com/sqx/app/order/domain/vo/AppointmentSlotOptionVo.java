package com.sqx.app.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预约时段选项
 */
@Data
public class AppointmentSlotOptionVo {

    @Schema(description = "时段文案")
    private String time;

    @Schema(description = "是否已满")
    private Boolean full;

    @Schema(description = "是否不可选")
    private Boolean disabled;

    @Schema(description = "不可选原因：expired/full")
    private String disabledReason;

    @Schema(description = "提示文案")
    private String tip;
}
