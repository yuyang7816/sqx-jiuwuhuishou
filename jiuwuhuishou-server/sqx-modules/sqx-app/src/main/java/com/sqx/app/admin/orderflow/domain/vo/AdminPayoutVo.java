package com.sqx.app.admin.orderflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.admin.support.AdminLookupSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminPayoutVo implements AdminLookupSupport.UserDisplayFields {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "打款单号")
    private String payoutNo;

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

    @Schema(description = "打款金额")
    private BigDecimal amount;

    @Schema(description = "支付渠道")
    private String payChannel;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "关联订单状态码")
    private String orderStatus;

    @Schema(description = "关联订单状态描述")
    private String orderStatusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "打款日志（详情）")
    private List<AdminPayoutLogVo> logs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "成功时间")
    private LocalDateTime successTime;
}
