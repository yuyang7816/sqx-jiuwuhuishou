package com.sqx.app.admin.orderflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminSettlementVo implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "结算单号")
    private String settlementNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "实际金额")
    private BigDecimal actualAmount;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "关联订单状态码")
    private String orderStatus;

    @Schema(description = "关联订单状态描述")
    private String orderStatusDesc;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
