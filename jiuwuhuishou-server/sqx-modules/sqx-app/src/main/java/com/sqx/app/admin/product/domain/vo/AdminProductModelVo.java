package com.sqx.app.admin.product.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminProductModelVo {

    private Long id;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String image;
    private BigDecimal basePrice;
    /** 最低估值（家电区间；数码/奢侈品与 basePrice 对齐） */
    private BigDecimal minPrice;
    /** 最高估值（家电区间；数码/奢侈品与 basePrice 对齐） */
    private BigDecimal maxPrice;
    private String specJson;
    private Integer sort;
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
