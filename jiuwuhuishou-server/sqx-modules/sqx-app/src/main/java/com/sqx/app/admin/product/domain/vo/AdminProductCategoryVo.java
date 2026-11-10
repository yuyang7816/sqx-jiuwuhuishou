package com.sqx.app.admin.product.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminProductCategoryVo {

    private Long id;
    private Long recycleCategoryId;
    private String recycleCategoryName;
    private String recycleCategoryCode;
    private String name;
    private String image;
    private Integer sort;
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
