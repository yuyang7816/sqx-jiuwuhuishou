package com.sqx.app.base.pay.business.service.impl;

import com.sqx.app.base.pay.business.service.VipPayService;
import com.sqx.app.base.pay.domain.BusinessParam;
import com.sqx.app.base.pay.enums.BusinessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BusinessServiceImplTest {

    @Mock
    private VipPayService vipPayService;

    @InjectMocks
    private BusinessServiceImpl businessService;

    @Test
    void processShouldPrepareVipPayWhenBusinessTypeIsVip() {
        BusinessParam param = new BusinessParam();
        param.setBusinessType(BusinessType.VIP.getCode());

        businessService.process(param);

        verify(vipPayService).preparePay(param);
    }

    @Test
    void processShouldSkipVipPayForOtherBusinessTypes() {
        BusinessParam param = new BusinessParam();
        param.setBusinessType("other");

        businessService.process(param);

        verifyNoInteractions(vipPayService);
    }
}
