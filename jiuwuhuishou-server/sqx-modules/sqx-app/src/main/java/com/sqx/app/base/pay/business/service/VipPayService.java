package com.sqx.app.base.pay.business.service;

import com.sqx.app.base.pay.domain.BusinessParam;

/**
 * VIP 会员支付业务
 */
public interface VipPayService {

    /**
     * 支付前校验套餐并缓存订单上下文
     */
    void preparePay(BusinessParam param);

    /**
     * 支付成功后开通会员
     */
    void activateVipOnPaySuccess(String orderNo);
}
