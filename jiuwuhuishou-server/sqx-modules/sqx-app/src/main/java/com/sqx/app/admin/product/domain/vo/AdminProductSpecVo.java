package com.sqx.app.admin.product.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminProductSpecVo {

    private Long id;
    private Long modelId;
    private String modelName;
    private Long brandId;
    private String brandName;
    private String attributeName;
    private List<ProductSpecAttributeVo> attributes;
    private Integer multiSelect;
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
