package com.sqx.app.base.pay.business.service.impl;

import com.sqx.app.base.pay.business.service.BusinessService;
import com.sqx.app.base.pay.business.service.VipPayService;
import com.sqx.app.base.pay.domain.BusinessParam;
import com.sqx.app.base.pay.enums.BusinessType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author chengming
 * @since 2025/4/21
 */
@Slf4j
@Service
public class BusinessServiceImpl implements BusinessService {

    @Resource
    private VipPayService vipPayService;

    @Override
    public void process(BusinessParam param) {
        if (Objects.equals(BusinessType.VIP.getCode(), param.getBusinessType())) {
            vipPayService.preparePay(param);
            return;
        }

        log.info("支付业务处理，businessType={}", param.getBusinessType());
    }
}
