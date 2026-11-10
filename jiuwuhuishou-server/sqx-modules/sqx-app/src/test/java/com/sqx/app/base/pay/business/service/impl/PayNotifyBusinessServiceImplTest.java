package com.sqx.app.base.pay.business.service.impl;

import com.sqx.app.base.pay.business.service.VipPayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PayNotifyBusinessServiceImplTest {

    @Mock
    private VipPayService vipPayService;

    @InjectMocks
    private PayNotifyBusinessServiceImpl payNotifyBusinessService;

    @Test
    void onPaymentSuccessShouldActivateVip() {
        payNotifyBusinessService.onPaymentSuccess("wx", "OUT001", "TRADE001");

        verify(vipPayService).activateVipOnPaySuccess("OUT001");
    }

    @Test
    void onPaymentFailedShouldOnlyLog() {
        payNotifyBusinessService.onPaymentFailed("wx", "OUT002", "TRADE002");

        verifyNoMoreInteractions(vipPayService);
    }
}
