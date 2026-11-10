package com.sqx.app.address.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户地址
 */
@Getter
@Setter
@TableName("user_address")
@Schema(name = "UserAddress", description = "用户地址")
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "联系人")
    @TableField("contact_name")
    private String contactName;

    @Schema(description = "联系电话")
    @TableField("contact_mobile")
    private String contactMobile;

    @Schema(description = "省")
    @TableField("province")
    private String province;

    @Schema(description = "市")
    @TableField("city")
    private String city;

    @Schema(description = "区")
    @TableField("district")
    private String district;

    @Schema(description = "街道")
    @TableField("street")
    private String street;

    @Schema(description = "详细地址")
    @TableField("detail_address")
    private String detailAddress;

    @Schema(description = "标签：家/公司等")
    @TableField("tag")
    private String tag;

    @Schema(description = "是否默认 0否 1是")
    @TableField("is_default")
    private Integer isDefault;

    @Schema(description = "逻辑删除 0正常 -1删除")
    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
