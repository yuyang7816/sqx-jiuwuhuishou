package com.sqx.app.admin.recycle.appointment.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminAppointmentSlotUpdateBo {

    @NotNull(message = "ID不能为空")
    @Schema(description = "主键")
    private Long id;

    @NotBlank(message = "开始时间不能为空")
    @Schema(description = "开始时间，如 10:00")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    @Schema(description = "结束时间，如 12:00")
    private String endTime;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "1上架 2下架")
    private Integer state;
}
