package com.sqx.app.admin.datacenter.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "数据中心概览查询")
public class AdminDatacenterOverviewBo {

    @Schema(description = "开始时间（含），缺省近7日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间（含），缺省当前时刻")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "可选品类：clothes/book/appliance/digital/luxury")
    private String orderType;
}
