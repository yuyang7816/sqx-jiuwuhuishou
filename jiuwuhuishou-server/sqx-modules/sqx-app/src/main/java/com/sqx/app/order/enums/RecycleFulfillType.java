package com.sqx.app.order.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 回收履约方式：上门 / 邮寄
 */
@Getter
public enum RecycleFulfillType {

    VISIT("visit", "上门订单"),
    MAIL("mail", "邮寄订单");

    private final String code;
    private final String label;

    RecycleFulfillType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static RecycleFulfillType of(String code) {
        if (StringUtils.isBlank(code)) {
            return VISIT;
        }
        for (RecycleFulfillType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        return VISIT;
    }

    public boolean isMail() {
        return this == MAIL;
    }
}
