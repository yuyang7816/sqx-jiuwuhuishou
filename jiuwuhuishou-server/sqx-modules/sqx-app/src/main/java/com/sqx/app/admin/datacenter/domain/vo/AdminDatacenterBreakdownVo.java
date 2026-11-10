package com.sqx.app.admin.datacenter.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "数据中心拆解（品类/履约/质量）")
public class AdminDatacenterBreakdownVo {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "品类结构")
    private List<AdminDatacenterCategoryVo> categories;

    @Schema(description = "取件履约")
    private AdminDatacenterPickupVo pickup;

    @Schema(description = "异常/争议/评价")
    private AdminDatacenterQualityVo quality;
}
