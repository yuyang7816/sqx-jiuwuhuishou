package com.sqx.app.admin.recycle.digital.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminDigitalModelVo {

    private Long id;
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    private String name;
    private String specJson;
    private BigDecimal basePrice;
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
