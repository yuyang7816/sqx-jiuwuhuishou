package com.sqx.app.base.notification.service.impl;

import com.sqx.app.base.notification.service.INotifyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotifyServiceImplTest {

    @Test
    void shouldInstantiateAsNotifyService() {
        NotifyServiceImpl service = new NotifyServiceImpl();
        assertNotNull(service);
        assertTrue(service instanceof INotifyService);
    }
}
