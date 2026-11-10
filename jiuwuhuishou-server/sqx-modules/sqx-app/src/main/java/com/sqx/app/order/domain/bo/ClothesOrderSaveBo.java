package com.sqx.app.order.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 旧衣回收下单
 */
@Data
public class ClothesOrderSaveBo {

    @Schema(description = "地址ID")
    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    @Schema(description = "预约时间")
    @NotNull(message = "预约时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    @Schema(description = "预估重量，如 5-20kg")
    @NotBlank(message = "预估重量不能为空")
    private String weight;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "回收品类名称，如 旧衣回收 / 书籍回收")
    private String categoryName;

    @Schema(description = "备注")
    private String remark;
}
