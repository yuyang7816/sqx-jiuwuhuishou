package com.sqx.app.admin.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceMessageVo;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceSessionVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.servicechat.db.service.ICustomerServiceMessageService;
import com.sqx.app.servicechat.db.service.ICustomerServiceSessionService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceMessage;
import com.sqx.app.servicechat.domain.entity.CustomerServiceSession;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServiceChatManageServiceImplTest {

    @Mock
    private ICustomerServiceSessionService iCustomerServiceSessionService;
    @Mock
    private ICustomerServiceMessageService iCustomerServiceMessageService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private AdminServiceChatManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(CustomerServiceSession.class, CustomerServiceMessage.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminServiceSessionVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminServiceSessionVo.class));
        when(iCustomerServiceSessionService.updateById(any(CustomerServiceSession.class))).thenReturn(true);
    }

    @Test
    void pageSessionShouldShortCircuitWhenUserIdsEmpty() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());

        AdminServiceSessionPageBo pageBo = pageBo(1L, 10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");

        assertTrue(service.pageSession(pageBo).getRecords().isEmpty());
        verify(iCustomerServiceSessionService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageSessionShouldHandleNullUserIdsAndEmptyRecords() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        Page<CustomerServiceSession> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iCustomerServiceSessionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertTrue(service.pageSession(pageBo(1L, 10L)).getRecords().isEmpty());
        verify(iCustomerServiceMessageService, never()).list(any(LambdaQueryWrapper.class));
    }

    @Test
    void pageSessionShouldMapSessionsWithoutPendingReplyFilter() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        stubSessionPageWithLastMessages();

        AdminServiceSessionPageBo pageBo = pageBo(1L, 10L);
        pageBo.setPendingReply(false);

        IPage<AdminServiceSessionVo> result = service.pageSession(pageBo);

        assertEquals(2, result.getRecords().size());
        assertEquals("short text", result.getRecords().get(0).getLastMessagePreview());
        assertEquals("[图片]", result.getRecords().get(1).getLastMessagePreview());
        assertTrue(result.getRecords().get(0).getPendingReply());
        assertFalse(result.getRecords().get(1).getPendingReply());
    }

    @Test
    void pageSessionShouldFilterPendingReplyInMemory() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        stubSessionPageWithLastMessages();

        AdminServiceSessionPageBo pageBo = pageBo(1L, 10L);
        pageBo.setPendingReply(true);

        IPage<AdminServiceSessionVo> result = service.pageSession(pageBo);

        assertEquals(1, result.getRecords().size());
        assertTrue(result.getRecords().get(0).getPendingReply());
    }

    @Test
    void pageSessionShouldQueryWithResolvedUserIds() {
        when(adminLookupSupport.resolveUserIds("u", "p")).thenReturn(Collections.singletonList(9L));

        CustomerServiceSession session = session(1L, 9L, 1);
        Page<CustomerServiceSession> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(session));
        when(iCustomerServiceSessionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(iCustomerServiceMessageService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        AdminServiceSessionPageBo pageBo = pageBo(1L, 10L);
        pageBo.setUserName("u");
        pageBo.setPhone("p");
        pageBo.setStatus(1);

        IPage<AdminServiceSessionVo> result = service.pageSession(pageBo);

        assertEquals(1, result.getRecords().size());
        assertEquals("", result.getRecords().get(0).getLastMessagePreview());
        assertFalse(result.getRecords().get(0).getPendingReply());
    }

    @Test
    void pageSessionShouldUsePutIfAbsentForDuplicateSessionMessages() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        CustomerServiceSession session = session(1L, 100L, 1);
        Page<CustomerServiceSession> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(session));
        when(iCustomerServiceSessionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        CustomerServiceMessage newer = message(1L, 1, "text", "newest");
        CustomerServiceMessage older = message(1L, 2, "text", "older");
        when(iCustomerServiceMessageService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(newer, older));

        IPage<AdminServiceSessionVo> result = service.pageSession(pageBo(1L, 10L));

        assertEquals("newest", result.getRecords().get(0).getLastMessagePreview());
        assertTrue(result.getRecords().get(0).getPendingReply());
    }

    @Test
    void pageSessionShouldTruncateLongTextPreview() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        CustomerServiceSession session = session(1L, 100L, 1);
        Page<CustomerServiceSession> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(session));
        when(iCustomerServiceSessionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        String longText = "abcdefghijklmnopqrstuvwxyz0123456789EXTRA";
        CustomerServiceMessage message = message(1L, 2, "text", longText);
        when(iCustomerServiceMessageService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(message));

        IPage<AdminServiceSessionVo> result = service.pageSession(pageBo(1L, 10L));

        assertEquals(longText.substring(0, 40) + "…", result.getRecords().get(0).getLastMessagePreview());
    }

    @Test
    void sessionInfoShouldReturnVoWhenFound() {
        CustomerServiceSession session = session(5L, 100L, 1);
        when(iCustomerServiceSessionService.getById(5L)).thenReturn(session);
        when(iCustomerServiceMessageService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        AdminServiceSessionInfoBo infoBo = new AdminServiceSessionInfoBo();
        infoBo.setId(5L);

        AdminServiceSessionVo vo = service.sessionInfo(infoBo);

        assertEquals(5L, vo.getId());
        assertEquals("tester", vo.getUserName());
        assertFalse(vo.getPendingReply());
    }

    @Test
    void sessionInfoShouldThrowWhenNotFound() {
        when(iCustomerServiceSessionService.getById(99L)).thenReturn(null);

        AdminServiceSessionInfoBo infoBo = new AdminServiceSessionInfoBo();
        infoBo.setId(99L);

        assertThrows(BizException.class, () -> service.sessionInfo(infoBo));
    }

    @Test
    void closeSessionShouldUpdateStatus() {
        when(iCustomerServiceSessionService.getById(5L)).thenReturn(session(5L, 100L, 1));

        AdminServiceSessionCloseBo closeBo = new AdminServiceSessionCloseBo();
        closeBo.setId(5L);

        service.closeSession(closeBo);

        verify(iCustomerServiceSessionService).updateById(argThat(update ->
                update.getId().equals(5L) && update.getStatus().equals(2)));
    }

    @Test
    void closeSessionShouldThrowWhenNotFound() {
        when(iCustomerServiceSessionService.getById(99L)).thenReturn(null);

        AdminServiceSessionCloseBo closeBo = new AdminServiceSessionCloseBo();
        closeBo.setId(99L);

        assertThrows(BizException.class, () -> service.closeSession(closeBo));
    }

    @Test
    void pageMessageShouldThrowWhenSessionNotFound() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(null);

        AdminServiceMessagePageBo pageBo = new AdminServiceMessagePageBo();
        pageBo.setSessionId(1L);
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        assertThrows(BizException.class, () -> service.pageMessage(pageBo));
    }

    @Test
    void pageMessageShouldReturnMessages() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(session(1L, 100L, 1));

        CustomerServiceMessage message = message(1L, 1, "text", "hi");
        message.setId(10L);
        Page<CustomerServiceMessage> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(message));
        when(iCustomerServiceMessageService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminServiceMessagePageBo pageBo = new AdminServiceMessagePageBo();
        pageBo.setSessionId(1L);
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        IPage<AdminServiceMessageVo> result = service.pageMessage(pageBo);

        assertEquals(1, result.getRecords().size());
        assertEquals("hi", result.getRecords().get(0).getContent());
    }

    @Test
    void saveMessageShouldReopenClosedSession() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(session(1L, 100L, 2));
        when(iCustomerServiceMessageService.save(any(CustomerServiceMessage.class))).thenAnswer(inv -> {
            CustomerServiceMessage message = inv.getArgument(0);
            message.setId(20L);
            return true;
        });

        AdminServiceMessageVo vo = service.saveMessage(saveBo(1L, "text", "reply"));

        assertEquals("reply", vo.getContent());
        verify(iCustomerServiceSessionService, times(2)).updateById(any(CustomerServiceSession.class));
    }

    @Test
    void saveMessageShouldSkipReopenForActiveSession() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(session(1L, 100L, 1));
        when(iCustomerServiceMessageService.save(any(CustomerServiceMessage.class))).thenAnswer(inv -> {
            CustomerServiceMessage message = inv.getArgument(0);
            message.setId(20L);
            return true;
        });

        service.saveMessage(saveBo(1L, "text", "reply"));

        verify(iCustomerServiceSessionService, times(1)).updateById(any(CustomerServiceSession.class));
    }

    @Test
    void saveMessageShouldRejectInvalidType() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(session(1L, 100L, 1));

        assertThrows(BizException.class, () -> service.saveMessage(saveBo(1L, "audio", "x")));
    }

    @Test
    void saveMessageShouldRejectBlankContent() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(session(1L, 100L, 1));

        assertThrows(BizException.class, () -> service.saveMessage(saveBo(1L, "text", "   ")));
    }

    @Test
    void saveMessageShouldSaveImageAndDefaultBlankType() {
        when(iCustomerServiceSessionService.getById(1L)).thenReturn(session(1L, 100L, 1));
        when(iCustomerServiceMessageService.save(any(CustomerServiceMessage.class))).thenAnswer(inv -> {
            CustomerServiceMessage message = inv.getArgument(0);
            message.setId(20L);
            return true;
        });

        AdminServiceMessageVo imageVo = service.saveMessage(saveBo(1L, "IMAGE", "/img/a.png"));
        assertEquals("image", imageVo.getMessageType());

        AdminServiceMessageVo textVo = service.saveMessage(saveBo(1L, "  ", "plain"));
        assertEquals("text", textVo.getMessageType());
    }

    private void stubSessionPageWithLastMessages() {
        CustomerServiceSession pendingSession = session(1L, 100L, 1);
        CustomerServiceSession repliedSession = session(2L, 101L, 1);

        Page<CustomerServiceSession> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(pendingSession, repliedSession));
        when(iCustomerServiceSessionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        CustomerServiceMessage userMsg = message(1L, 1, "text", "short text");
        CustomerServiceMessage imageMsg = message(2L, 2, "image", "/img/a.png");
        when(iCustomerServiceMessageService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(userMsg, imageMsg));
    }

    private AdminServiceSessionPageBo pageBo(long page, long limit) {
        AdminServiceSessionPageBo pageBo = new AdminServiceSessionPageBo();
        pageBo.setPage(page);
        pageBo.setLimit(limit);
        return pageBo;
    }

    private CustomerServiceSession session(Long id, Long userId, int status) {
        CustomerServiceSession session = new CustomerServiceSession();
        session.setId(id);
        session.setUserId(userId);
        session.setStatus(status);
        return session;
    }

    private CustomerServiceMessage message(Long sessionId, int senderType, String messageType, String content) {
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(sessionId);
        message.setSenderType(senderType);
        message.setMessageType(messageType);
        message.setContent(content);
        return message;
    }

    private AdminServiceMessageSaveBo saveBo(Long sessionId, String messageType, String content) {
        AdminServiceMessageSaveBo saveBo = new AdminServiceMessageSaveBo();
        saveBo.setSessionId(sessionId);
        saveBo.setMessageType(messageType);
        saveBo.setContent(content);
        return saveBo;
    }

    @Test
    @SuppressWarnings("unchecked")
    void loadLastMessagesShouldReturnEmptyWhenSessionsEmpty() throws Exception {
        Method method = AdminServiceChatManageServiceImpl.class
                .getDeclaredMethod("loadLastMessages", java.util.List.class);
        method.setAccessible(true);
        Map<?, ?> result = (Map<?, ?>) method.invoke(service, Collections.emptyList());
        assertTrue(result.isEmpty());
        verify(iCustomerServiceMessageService, never()).list(any(LambdaQueryWrapper.class));
    }
}
