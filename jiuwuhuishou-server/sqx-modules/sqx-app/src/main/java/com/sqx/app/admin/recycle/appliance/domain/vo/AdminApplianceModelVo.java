package com.sqx.app.admin.recycle.appliance.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminApplianceModelVo {

    private Long id;
    private Long categoryId;

    @Schema(description = "品类名称")
    private String categoryName;

    private String name;
    private String specJson;
    private BigDecimal basePrice;
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
