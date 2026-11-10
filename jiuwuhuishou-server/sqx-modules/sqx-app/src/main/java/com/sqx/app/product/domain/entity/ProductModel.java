package com.sqx.app.product.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("product_model")
@Schema(name = "ProductModel", description = "商品型号")
public class ProductModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("brand_id")
    private Long brandId;

    @TableField("name")
    private String name;

    @TableField("image")
    private String image;

    @TableField("base_price")
    private BigDecimal basePrice;

    @TableField("min_price")
    private BigDecimal minPrice;

    @TableField("max_price")
    private BigDecimal maxPrice;

    @TableField("spec_json")
    private String specJson;

    /** 最低估值：优先 min_price，缺省回退 base_price */
    public BigDecimal resolveMinPrice() {
        if (minPrice != null) {
            return minPrice;
        }
        return basePrice == null ? BigDecimal.ZERO : basePrice;
    }

    /** 最高估值：优先 max_price，缺省回退 base_price / min */
    public BigDecimal resolveMaxPrice() {
        if (maxPrice != null) {
            return maxPrice;
        }
        return resolveMinPrice();
    }

    @TableField("sort")
    private Integer sort;

    @TableField("state")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
