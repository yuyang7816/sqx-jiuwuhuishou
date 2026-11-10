package com.sqx.app.base.notification.core.service.impl;

import com.sqx.app.base.notification.core.factory.NotificationSenderFactory;
import com.sqx.app.base.notification.core.model.NotificationRequest;
import com.sqx.app.base.notification.sender.INotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationServiceImplTest {

    @Mock
    private NotificationSenderFactory notificationSenderFactory;
    @Mock
    private INotificationSender notificationSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendSingleChannelShouldDelegateToSender() {
        NotificationRequest request = buildRequest();
        when(notificationSenderFactory.getNotificationSender("instation")).thenReturn(notificationSender);

        notificationService.send(request, "instation");

        verify(notificationSender).send(request);
    }

    @Test
    void sendSingleChannelShouldCatchException() {
        NotificationRequest request = buildRequest();
        when(notificationSenderFactory.getNotificationSender("app")).thenReturn(notificationSender);
        doThrow(new RuntimeException("send failed")).when(notificationSender).send(request);

        assertDoesNotThrow(() -> notificationService.send(request, "app"));
    }

    @Test
    void sendAllChannelsShouldIterateEveryChannel() {
        NotificationRequest request = buildRequest();
        when(notificationSenderFactory.getNotificationSender(any())).thenReturn(notificationSender);

        notificationService.send(request);

        verify(notificationSenderFactory).getNotificationSender("instation");
        verify(notificationSenderFactory).getNotificationSender("mp");
        verify(notificationSenderFactory).getNotificationSender("ma");
        verify(notificationSenderFactory).getNotificationSender("app");
        verify(notificationSender, times(4)).send(request);
    }

    @Test
    void sendMultipleChannelsShouldRejectEmptyInput() {
        NotificationRequest request = buildRequest();

        assertThrows(IllegalArgumentException.class, () -> notificationService.send(request, (String[]) null));
        assertThrows(IllegalArgumentException.class, () -> notificationService.send(request, new String[0]));
    }

    @Test
    void sendMultipleChannelsShouldDispatchEachChannel() {
        NotificationRequest request = buildRequest();
        when(notificationSenderFactory.getNotificationSender(any())).thenReturn(notificationSender);

        notificationService.send(request, "instation", "mp");

        verify(notificationSenderFactory).getNotificationSender("instation");
        verify(notificationSenderFactory).getNotificationSender("mp");
        verify(notificationSender, times(2)).send(eq(request));
    }

    private NotificationRequest buildRequest() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(1L);
        request.setTitle("title");
        request.setContent("content");
        return request;
    }
}
