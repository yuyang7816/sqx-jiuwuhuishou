package com.sqx.app.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.utils.SysConfigUtil;
import com.sqx.app.servicechat.db.service.ICustomerServiceMessageService;
import com.sqx.app.servicechat.db.service.ICustomerServiceSessionService;
import com.sqx.app.servicechat.domain.bo.ServiceMessagePageBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageQuickBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageSaveBo;
import com.sqx.app.servicechat.domain.entity.CustomerServiceMessage;
import com.sqx.app.servicechat.domain.entity.CustomerServiceSession;
import com.sqx.app.servicechat.domain.vo.ServiceMessageQuickVo;
import com.sqx.app.servicechat.domain.vo.ServiceMessageVo;
import com.sqx.app.servicechat.service.ServiceChatConfigService;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceMessageServiceImplTest {

    private static final Long USER_ID = 100L;

    @Mock
    private ICustomerServiceSessionService iCustomerServiceSessionService;
    @Mock
    private ICustomerServiceMessageService iCustomerServiceMessageService;
    @Mock
    private ServiceChatConfigService serviceChatConfigService;

    @InjectMocks
    private ServiceMessageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(CustomerServiceSession.class, CustomerServiceMessage.class);
    }

    @BeforeEach
    void setUp() {
        when(iCustomerServiceMessageService.save(any(CustomerServiceMessage.class))).thenAnswer(inv -> {
            CustomerServiceMessage message = inv.getArgument(0);
            message.setId(1L);
            return true;
        });
        when(iCustomerServiceSessionService.updateById(any(CustomerServiceSession.class))).thenReturn(true);
        when(serviceChatConfigService.resolveReplyContent(anyString()))
                .thenReturn("您好，已收到您的问题，我们会尽快为您解答。如需人工协助，可点击「转人工」。");
    }

    @Test
    void pageShouldReturnEmptyWhenNoSession() {
        when(iCustomerServiceSessionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        ServiceMessagePageBo pageBo = new ServiceMessagePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        IPage<ServiceMessageVo> result = service.page(USER_ID, pageBo);

        assertTrue(result.getRecords().isEmpty());
        verify(iCustomerServiceMessageService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageShouldReturnMessagesWhenSessionExists() {
        CustomerServiceSession session = new CustomerServiceSession();
        session.setId(10L);
        session.setUserId(USER_ID);
        when(iCustomerServiceSessionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(session);

        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setId(1L);
        message.setSessionId(10L);
        message.setContent("hello");
        message.setMessageType("text");
        message.setSenderType(1);

        Page<CustomerServiceMessage> messagePage = new Page<>(1, 10);
        messagePage.setRecords(Collections.singletonList(message));
        when(iCustomerServiceMessageService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(messagePage);

        ServiceMessagePageBo pageBo = new ServiceMessagePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        IPage<ServiceMessageVo> result = service.page(USER_ID, pageBo);

        assertEquals(1, result.getRecords().size());
        assertEquals("hello", result.getRecords().get(0).getContent());
    }

    @Test
    void saveShouldRejectInvalidMessageType() {
        stubExistingSession(10L);

        ServiceMessageSaveBo saveBo = new ServiceMessageSaveBo();
        saveBo.setMessageType("video");
        saveBo.setContent("content");

        assertThrows(BizException.class, () -> service.save(USER_ID, saveBo));
    }

    @Test
    void saveShouldSaveTextMessageWithExistingSession() {
        stubExistingSession(10L);

        ServiceMessageSaveBo saveBo = new ServiceMessageSaveBo();
        saveBo.setMessageType("text");
        saveBo.setContent("hello");

        ServiceMessageVo vo = service.save(USER_ID, saveBo);

        assertEquals("text", vo.getMessageType());
        assertEquals("hello", vo.getContent());
        verify(iCustomerServiceMessageService).save(any(CustomerServiceMessage.class));
        verify(iCustomerServiceSessionService).updateById(any(CustomerServiceSession.class));
        verify(iCustomerServiceSessionService, never()).save(any(CustomerServiceSession.class));
    }

    @Test
    void saveShouldSaveImageMessage() {
        stubExistingSession(10L);

        ServiceMessageSaveBo saveBo = new ServiceMessageSaveBo();
        saveBo.setMessageType("IMAGE");
        saveBo.setContent("/img/a.png");

        ServiceMessageVo vo = service.save(USER_ID, saveBo);

        assertEquals("image", vo.getMessageType());
        assertEquals("/img/a.png", vo.getContent());
    }

    @Test
    void saveShouldDefaultBlankMessageTypeToText() {
        stubExistingSession(10L);

        ServiceMessageSaveBo saveBo = new ServiceMessageSaveBo();
        saveBo.setMessageType("  ");
        saveBo.setContent("default type");

        ServiceMessageVo vo = service.save(USER_ID, saveBo);

        assertEquals("text", vo.getMessageType());
    }

    @Test
    void saveShouldCreateSessionWhenMissing() {
        when(iCustomerServiceSessionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iCustomerServiceSessionService.save(any(CustomerServiceSession.class))).thenAnswer(inv -> {
            CustomerServiceSession session = inv.getArgument(0);
            session.setId(20L);
            return true;
        });

        ServiceMessageSaveBo saveBo = new ServiceMessageSaveBo();
        saveBo.setMessageType("text");
        saveBo.setContent("new session");

        ServiceMessageVo vo = service.save(USER_ID, saveBo);

        assertEquals("new session", vo.getContent());
        verify(iCustomerServiceSessionService).save(any(CustomerServiceSession.class));
    }

    @Test
    void quickAskShouldRejectBlankQuestion() {
        stubExistingSession(10L);

        ServiceMessageQuickBo quickBo = new ServiceMessageQuickBo();
        quickBo.setQuestion("   ");

        assertThrows(BizException.class, () -> service.quickAsk(USER_ID, quickBo));
    }

    @Test
    void quickAskShouldHandleTransferWithConfigHint() {
        stubExistingSession(10L);

        try (MockedStatic<SysConfigUtil> config = mockStatic(SysConfigUtil.class)) {
            config.when(() -> SysConfigUtil.getConfigValue("service_chat_transfer_hint"))
                    .thenReturn("自定义转接提示");

            ServiceMessageQuickVo vo = service.quickAsk(USER_ID, quickBo("转人工"));

            assertTrue(vo.getTransferring());
            assertEquals("自定义转接提示", vo.getTransferHint());
            assertNotNull(vo.getUserMessage());
            assertNull(vo.getReplyMessage());
            verify(iCustomerServiceMessageService, times(1)).save(any(CustomerServiceMessage.class));
            verify(serviceChatConfigService, never()).resolveReplyContent(anyString());
        }
    }

    @Test
    void quickAskShouldHandleTransferWithBlankConfigHint() {
        stubExistingSession(10L);

        try (MockedStatic<SysConfigUtil> config = mockStatic(SysConfigUtil.class)) {
            config.when(() -> SysConfigUtil.getConfigValue("service_chat_transfer_hint")).thenReturn("  ");

            ServiceMessageQuickVo vo = service.quickAsk(USER_ID, quickBo("转人工"));

            assertEquals("正在转接人工，请稍等···", vo.getTransferHint());
        }
    }

    @Test
    void quickAskShouldHandleTransferWithConfigException() {
        stubExistingSession(10L);

        try (MockedStatic<SysConfigUtil> config = mockStatic(SysConfigUtil.class)) {
            config.when(() -> SysConfigUtil.getConfigValue("service_chat_transfer_hint"))
                    .thenThrow(new RuntimeException("redis down"));

            ServiceMessageQuickVo vo = service.quickAsk(USER_ID, quickBo("转人工"));

            assertEquals("正在转接人工，请稍等···", vo.getTransferHint());
        }
    }

    @Test
    void quickAskShouldReplyKnownQuestionFromConfigService() {
        stubExistingSession(10L);
        when(serviceChatConfigService.resolveReplyContent("如何预约上门回收？"))
                .thenReturn("亲，旧家电回收非常方便！\n1. 在首页点击「上门回收」");

        ServiceMessageQuickVo vo = service.quickAsk(USER_ID, quickBo("如何预约上门回收？"));

        assertFalse(vo.getTransferring());
        assertNotNull(vo.getReplyMessage());
        assertTrue(vo.getReplyMessage().getContent().contains("旧家电回收非常方便"));
        verify(iCustomerServiceMessageService, times(2)).save(any(CustomerServiceMessage.class));
        verify(serviceChatConfigService).resolveReplyContent("如何预约上门回收？");
    }

    @Test
    void quickAskShouldReplyFallbackForUnknownQuestion() {
        stubExistingSession(10L);
        when(serviceChatConfigService.resolveReplyContent("未知问题"))
                .thenReturn("您好，已收到您的问题，我们会尽快为您解答。如需人工协助，可点击「转人工」。");

        ServiceMessageQuickVo vo = service.quickAsk(USER_ID, quickBo("未知问题"));

        assertEquals("您好，已收到您的问题，我们会尽快为您解答。如需人工协助，可点击「转人工」。",
                vo.getReplyMessage().getContent());
    }

    private void stubExistingSession(Long sessionId) {
        CustomerServiceSession session = new CustomerServiceSession();
        session.setId(sessionId);
        session.setUserId(USER_ID);
        session.setStatus(1);
        when(iCustomerServiceSessionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(session);
    }

    private ServiceMessageQuickBo quickBo(String question) {
        ServiceMessageQuickBo quickBo = new ServiceMessageQuickBo();
        quickBo.setQuestion(question);
        return quickBo;
    }
}
