package com.sqx.app.express.sf.domain;

import lombok.Data;

/**
 * 顺丰下单结果
 */
@Data
public class SfPlaceOrderResult {
    private String orderId;
    private String mailNo;
}
