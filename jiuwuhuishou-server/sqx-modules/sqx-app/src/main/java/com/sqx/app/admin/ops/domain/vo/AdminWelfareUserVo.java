package com.sqx.app.admin.ops.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdminWelfareUserVo implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "金币余额")
    private Integer coinBalance;

    @Schema(description = "连续签到天数")
    private Integer continuousSignDays;

    @Schema(description = "是否开启连续签到 0否1是")
    private Integer continuousSignEnabled;

    @Schema(description = "最近签到日期")
    private LocalDate lastSignDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
