package com.sqx.app.base.pay.business.service.impl;

import com.sqx.app.base.pay.business.service.VipPayService;
import com.sqx.common.pay.notify.IPayNotifyBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author chengming
 * @since 2025/4/21
 *
 * 回调业务
 */
@Slf4j
@Service
public class PayNotifyBusinessServiceImpl implements IPayNotifyBusinessService {

    @Resource
    private VipPayService vipPayService;

    @Override
    public void onPaymentSuccess(String channel, String outTradeNo, String tradeNo) {
        log.info("支付回调成功，channel={}, outTradeNo={}, tradeNo={}", channel, outTradeNo, tradeNo);
        vipPayService.activateVipOnPaySuccess(outTradeNo);
    }

    @Override
    public void onPaymentFailed(String channel, String outTradeNo, String tradeNo) {
        log.info("支付回调失败，channel={}, outTradeNo={}, tradeNo={}", channel, outTradeNo, tradeNo);
    }
}
