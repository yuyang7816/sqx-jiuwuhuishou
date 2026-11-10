package com.sqx.app.base.messages.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.messages.db.service.IMessagesService;
import com.sqx.app.base.messages.domain.bo.MessagesInfoBo;
import com.sqx.app.base.messages.domain.bo.MessagesPageBo;
import com.sqx.app.base.messages.domain.bo.MessagesSaveBo;
import com.sqx.app.base.messages.domain.bo.MessagesUpdateBo;
import com.sqx.app.base.messages.domain.entity.Messages;
import com.sqx.app.base.messages.domain.vo.MessagesVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MessagesServiceImplTest {

    @Mock
    private IMessagesService iMessagesService;

    @InjectMocks
    private MessagesServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(Messages.class);
    }

    @Test
    void crudAndInfo() {
        service.saveInfo(new MessagesSaveBo());
        verify(iMessagesService).save(any(Messages.class));

        MessagesUpdateBo updateBo = new MessagesUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iMessagesService).updateById(any(Messages.class));

        MessagesInfoBo infoBo = new MessagesInfoBo();
        infoBo.setId(1L);
        when(iMessagesService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        Messages messages = new Messages();
        messages.setId(1L);
        when(iMessagesService.getById(1L)).thenReturn(messages);
        assertEquals(1L, service.info(infoBo).getId());

        service.delete(infoBo);
        verify(iMessagesService).removeById(1L);
    }

    @Test
    void pageInfoShouldDelegate() {
        MessagesPageBo pageBo = new MessagesPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        Page<MessagesVo> voPage = new Page<>(1, 10);
        when(iMessagesService.pageInfo(any(Page.class), eq(pageBo))).thenReturn(voPage);
        assertSame(voPage, service.pageInfo(pageBo));
    }
}
