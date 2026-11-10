package com.sqx.app.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 预约日期选项
 */
@Data
public class AppointmentDateOptionVo {

    @Schema(description = "日期标签：今日/明日/后日/周一~周日")
    private String label;

    @Schema(description = "日期展示：M月d日")
    private String day;

    @Schema(description = "日期键 yyyy-MM-dd")
    private String dateKey;

    @Schema(description = "时段列表")
    private List<AppointmentSlotOptionVo> slots;
}
