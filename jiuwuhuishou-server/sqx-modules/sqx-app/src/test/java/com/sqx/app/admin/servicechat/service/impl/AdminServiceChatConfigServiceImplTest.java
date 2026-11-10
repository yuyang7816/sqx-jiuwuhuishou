package com.sqx.app.admin.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceConfigIdBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceQuickQuestionPageBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceQuickQuestionSaveBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceQuickQuestionUpdateBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceShortcutPageBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceShortcutSaveBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceShortcutUpdateBo;
import com.sqx.app.servicechat.db.service.ICustomerServiceQuickQuestionService;
import com.sqx.app.servicechat.db.service.ICustomerServiceShortcutService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServiceChatConfigServiceImplTest {

    @Mock
    private ICustomerServiceQuickQuestionService iCustomerServiceQuickQuestionService;
    @Mock
    private ICustomerServiceShortcutService iCustomerServiceShortcutService;

    @InjectMocks
    private AdminServiceChatConfigServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(CustomerServiceQuickQuestion.class, CustomerServiceShortcut.class);
    }

    @Test
    void saveQuickQuestionShouldFillDefaultsAndPersist() {
        when(iCustomerServiceQuickQuestionService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        AdminServiceQuickQuestionSaveBo saveBo = new AdminServiceQuickQuestionSaveBo();
        saveBo.setQuestion("如何预约？");
        saveBo.setContent("请选择品类后下单");

        service.saveQuickQuestion(saveBo);

        ArgumentCaptor<CustomerServiceQuickQuestion> captor = ArgumentCaptor.forClass(CustomerServiceQuickQuestion.class);
        verify(iCustomerServiceQuickQuestionService).save(captor.capture());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getShowWelcome());
    }

    @Test
    void saveQuickQuestionShouldRejectDuplicate() {
        when(iCustomerServiceQuickQuestionService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
        AdminServiceQuickQuestionSaveBo saveBo = new AdminServiceQuickQuestionSaveBo();
        saveBo.setQuestion("重复问题");
        saveBo.setContent("内容");
        assertThrows(BizException.class, () -> service.saveQuickQuestion(saveBo));
    }

    @Test
    void pageQuickQuestionShouldQueryWithOptionalFilter() {
        Page<CustomerServiceQuickQuestion> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iCustomerServiceQuickQuestionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminServiceQuickQuestionPageBo pageBo = new AdminServiceQuickQuestionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setQuestion("预约");
        assertTrue(service.pageQuickQuestion(pageBo).getRecords().isEmpty());

        pageBo.setQuestion(null);
        assertTrue(service.pageQuickQuestion(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoQuickQuestionShouldValidateExists() {
        AdminServiceConfigIdBo idBo = new AdminServiceConfigIdBo();
        idBo.setId(9L);
        when(iCustomerServiceQuickQuestionService.getById(9L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoQuickQuestion(idBo));

        CustomerServiceQuickQuestion entity = new CustomerServiceQuickQuestion();
        entity.setId(9L);
        entity.setQuestion("Q");
        when(iCustomerServiceQuickQuestionService.getById(9L)).thenReturn(entity);
        assertEquals("Q", service.infoQuickQuestion(idBo).getQuestion());
    }

    @Test
    void updateQuickQuestionShouldFillNullDefaults() {
        CustomerServiceQuickQuestion existing = new CustomerServiceQuickQuestion();
        existing.setId(1L);
        when(iCustomerServiceQuickQuestionService.getById(1L)).thenReturn(existing);
        when(iCustomerServiceQuickQuestionService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        AdminServiceQuickQuestionUpdateBo updateBo = new AdminServiceQuickQuestionUpdateBo();
        updateBo.setId(1L);
        updateBo.setQuestion("新问题");
        updateBo.setContent("新内容");
        service.updateQuickQuestion(updateBo);

        ArgumentCaptor<CustomerServiceQuickQuestion> captor = ArgumentCaptor.forClass(CustomerServiceQuickQuestion.class);
        verify(iCustomerServiceQuickQuestionService).updateById(captor.capture());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getShowWelcome());
    }

    @Test
    void deleteQuickQuestionShouldRemoveById() {
        AdminServiceConfigIdBo idBo = new AdminServiceConfigIdBo();
        idBo.setId(3L);
        service.deleteQuickQuestion(idBo);
        verify(iCustomerServiceQuickQuestionService).removeById(3L);
    }

    @Test
    void saveShortcutShouldFillSortDefaultAndRejectDuplicate() {
        when(iCustomerServiceShortcutService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
        AdminServiceShortcutSaveBo dup = new AdminServiceShortcutSaveBo();
        dup.setContent("提示词");
        assertThrows(BizException.class, () -> service.saveShortcut(dup));

        when(iCustomerServiceShortcutService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        AdminServiceShortcutSaveBo saveBo = new AdminServiceShortcutSaveBo();
        saveBo.setContent("新提示词");
        service.saveShortcut(saveBo);
        ArgumentCaptor<CustomerServiceShortcut> captor = ArgumentCaptor.forClass(CustomerServiceShortcut.class);
        verify(iCustomerServiceShortcutService).save(captor.capture());
        assertEquals(0, captor.getValue().getSort());
    }

    @Test
    void pageAndInfoShortcutShouldWork() {
        Page<CustomerServiceShortcut> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iCustomerServiceShortcutService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminServiceShortcutPageBo pageBo = new AdminServiceShortcutPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setContent("词");
        assertTrue(service.pageShortcut(pageBo).getRecords().isEmpty());
        pageBo.setContent("");
        assertTrue(service.pageShortcut(pageBo).getRecords().isEmpty());

        AdminServiceConfigIdBo idBo = new AdminServiceConfigIdBo();
        idBo.setId(8L);
        when(iCustomerServiceShortcutService.getById(8L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoShortcut(idBo));

        CustomerServiceShortcut entity = new CustomerServiceShortcut();
        entity.setId(8L);
        entity.setContent("hi");
        when(iCustomerServiceShortcutService.getById(8L)).thenReturn(entity);
        assertEquals("hi", service.infoShortcut(idBo).getContent());
    }

    @Test
    void updateAndDeleteShortcutShouldPersist() {
        CustomerServiceShortcut existing = new CustomerServiceShortcut();
        existing.setId(2L);
        when(iCustomerServiceShortcutService.getById(2L)).thenReturn(existing);
        when(iCustomerServiceShortcutService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        AdminServiceShortcutUpdateBo updateBo = new AdminServiceShortcutUpdateBo();
        updateBo.setId(2L);
        updateBo.setContent("更新提示");
        service.updateShortcut(updateBo);
        ArgumentCaptor<CustomerServiceShortcut> captor = ArgumentCaptor.forClass(CustomerServiceShortcut.class);
        verify(iCustomerServiceShortcutService).updateById(captor.capture());
        assertEquals(0, captor.getValue().getSort());

        AdminServiceConfigIdBo idBo = new AdminServiceConfigIdBo();
        idBo.setId(2L);
        service.deleteShortcut(idBo);
        verify(iCustomerServiceShortcutService).removeById(2L);
    }
}
