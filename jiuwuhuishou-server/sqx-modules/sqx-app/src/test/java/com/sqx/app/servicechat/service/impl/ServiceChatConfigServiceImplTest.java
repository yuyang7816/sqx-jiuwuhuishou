package com.sqx.app.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.servicechat.db.service.ICustomerServiceQuickQuestionService;
import com.sqx.app.servicechat.db.service.ICustomerServiceShortcutService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;
import com.sqx.app.servicechat.domain.vo.ServiceQuickQuestionVo;
import com.sqx.app.servicechat.domain.vo.ServiceShortcutVo;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceChatConfigServiceImplTest {

    @Mock
    private ICustomerServiceQuickQuestionService iCustomerServiceQuickQuestionService;
    @Mock
    private ICustomerServiceShortcutService iCustomerServiceShortcutService;

    @InjectMocks
    private ServiceChatConfigServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(CustomerServiceQuickQuestion.class, CustomerServiceShortcut.class);
    }

    @Test
    void listWelcomeQuestionsShouldHandleNullEmptyAndMap() {
        when(iCustomerServiceQuickQuestionService.list(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertTrue(service.listWelcomeQuestions().isEmpty());

        when(iCustomerServiceQuickQuestionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.listWelcomeQuestions().isEmpty());

        CustomerServiceQuickQuestion q = new CustomerServiceQuickQuestion();
        q.setId(1L);
        q.setQuestion("如何预约？");
        q.setContent("内容A");
        when(iCustomerServiceQuickQuestionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(q));

        List<ServiceQuickQuestionVo> list = service.listWelcomeQuestions();
        assertEquals(1, list.size());
        assertEquals("如何预约？", list.get(0).getQuestion());
        assertEquals("内容A", list.get(0).getContent());
    }

    @Test
    void listShortcutsShouldHandleNullEmptyAndMap() {
        when(iCustomerServiceShortcutService.list(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertTrue(service.listShortcuts().isEmpty());

        when(iCustomerServiceShortcutService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.listShortcuts().isEmpty());

        CustomerServiceShortcut s1 = new CustomerServiceShortcut();
        s1.setId(2L);
        s1.setContent("转人工");
        when(iCustomerServiceShortcutService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(s1));

        List<ServiceShortcutVo> list = service.listShortcuts();
        assertEquals(1, list.size());
        assertEquals("转人工", list.get(0).getContent());
    }

    @Test
    void resolveReplyContentShouldFallbackAndMatch() {
        assertTrue(service.resolveReplyContent(null).contains("已收到您的问题"));
        assertTrue(service.resolveReplyContent("   ").contains("已收到您的问题"));

        when(iCustomerServiceQuickQuestionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertTrue(service.resolveReplyContent("未知问题").contains("已收到您的问题"));

        CustomerServiceQuickQuestion blankContent = new CustomerServiceQuickQuestion();
        blankContent.setContent("  ");
        when(iCustomerServiceQuickQuestionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(blankContent);
        assertTrue(service.resolveReplyContent("空内容").contains("已收到您的问题"));

        CustomerServiceQuickQuestion matched = new CustomerServiceQuickQuestion();
        matched.setContent("自动回复内容");
        when(iCustomerServiceQuickQuestionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(matched);
        assertEquals("自动回复内容", service.resolveReplyContent("如何预约？"));
    }
}
