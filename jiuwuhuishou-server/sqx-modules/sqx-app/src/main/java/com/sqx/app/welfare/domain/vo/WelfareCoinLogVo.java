package com.sqx.app.welfare.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(name = "WelfareCoinLogVo", description = "积分明细")
public class WelfareCoinLogVo {

    @Schema(description = "记录id")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "积分变动数量")
    private Integer coinAmount;

    @Schema(description = "类型：1收入 2支出")
    private Integer type;

    @Schema(description = "签到日期")
    private LocalDate signDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
