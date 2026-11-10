package com.sqx.app.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 回收订单
 */
@Data
public class RecycleOrderVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单类型")
    private String orderType;

    @Schema(description = "履约方式 visit/mail")
    private String fulfillType;

    @Schema(description = "履约方式文案")
    private String fulfillTypeLabel;

    @Schema(description = "品类描述")
    private String categoryName;

    @Schema(description = "预约时间（时段开始）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    @Schema(description = "预约时段结束时间（由上门时段配置解析）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentEndTime;

    @Schema(description = "地址ID")
    private Long addressId;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话（脱敏）")
    private String contactMobile;

    @Schema(description = "完整地址快照")
    private String addressText;

    @Schema(description = "上门地址")
    private String pickupAddressText;

    @Schema(description = "邮寄地址（仓库收件地址，邮寄订单返回）")
    private String mailAddressText;

    @Schema(description = "快递单号")
    private String expressNo;

    @Schema(description = "快递公司")
    private String expressCompany;

    @Schema(description = "物流轨迹")
    private List<ExpressTrackVo> expressTracks;

    @Schema(description = "预估金额")
    private BigDecimal estimateAmount;

    @Schema(description = "预估金额下限（家电）")
    private BigDecimal estimateMinAmount;

    @Schema(description = "预估金额上限（家电）")
    private BigDecimal estimateMaxAmount;

    @Schema(description = "实际金额（对应用户端实际成交金额）")
    private BigDecimal actualAmount;

    @Schema(description = "预估/预付金额是否已确认")
    private Boolean estimateAmountConfirmed;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "状态分组键")
    private String statusKey;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
