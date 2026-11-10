package com.sqx.app.order.enums;

import lombok.Getter;

/**
 * 回收订单类型
 */
@Getter
public enum RecycleOrderType {

    CLOTHES("clothes", "旧衣回收"),
    BOOK("book", "书籍回收"),
    APPLIANCE("appliance", "家电回收"),
    DIGITAL("digital", "数码回收"),
    LUXURY("luxury", "奢侈品回收");

    private final String code;
    private final String label;

    RecycleOrderType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static RecycleOrderType of(String code) {
        for (RecycleOrderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的订单类型: " + code);
    }
}
