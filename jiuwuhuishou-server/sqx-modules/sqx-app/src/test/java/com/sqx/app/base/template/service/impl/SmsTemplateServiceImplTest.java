package com.sqx.app.base.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.enums.AppError;
import com.sqx.app.base.template.db.service.ISmsTemplateService;
import com.sqx.app.base.template.domain.bo.SmsTemplateInfoBo;
import com.sqx.app.base.template.domain.bo.SmsTemplatePageBo;
import com.sqx.app.base.template.domain.bo.SmsTemplateSaveBo;
import com.sqx.app.base.template.domain.bo.SmsTemplateUpdateBo;
import com.sqx.app.base.template.domain.entity.SmsTemplate;
import com.sqx.app.base.template.domain.vo.SmsTemplateVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.exception.SqxException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SmsTemplateServiceImplTest {

    @Mock
    private ISmsTemplateService iSmsTemplateService;

    @InjectMocks
    private SmsTemplateServiceImpl smsTemplateService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(SmsTemplate.class);
    }

    @Test
    void saveInfoShouldPersistTemplate() {
        SmsTemplateSaveBo saveBo = new SmsTemplateSaveBo();
        saveBo.setBizCode("login");
        saveBo.setContent("验证码 ${code}");

        smsTemplateService.saveInfo(saveBo);

        verify(iSmsTemplateService).save(any(SmsTemplate.class));
    }

    @Test
    void pageInfoShouldHandleEmptyAndMappedRecords() {
        SmsTemplatePageBo pageBo = new SmsTemplatePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setBizCode("login");
        pageBo.setContent("验证码");

        Page<SmsTemplate> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iSmsTemplateService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(emptyPage);
        assertTrue(smsTemplateService.pageInfo(pageBo).getData().getRecords().isEmpty());

        SmsTemplate template = new SmsTemplate();
        template.setId(1L);
        template.setBizCode("login");
        template.setContent("验证码 ${code}");
        emptyPage.setRecords(Collections.singletonList(template));

        pageBo.setBizCode(null);
        pageBo.setContent(null);
        IPage<SmsTemplateVo> result = smsTemplateService.pageInfo(pageBo).getData();
        assertEquals(1, result.getRecords().size());
        assertEquals("login", result.getRecords().get(0).getBizCode());
    }

    @Test
    void infoShouldThrowWhenMissingOrReturnVo() {
        SmsTemplateInfoBo infoBo = new SmsTemplateInfoBo();
        infoBo.setId(99L);
        when(iSmsTemplateService.getById(99L)).thenReturn(null);
        assertThrows(SqxException.class, () -> smsTemplateService.info(infoBo));

        SmsTemplate template = new SmsTemplate();
        template.setId(99L);
        template.setBizCode("register");
        template.setContent("欢迎注册");
        when(iSmsTemplateService.getById(99L)).thenReturn(template);

        SmsTemplateVo vo = smsTemplateService.info(infoBo).getData();
        assertEquals("register", vo.getBizCode());
        assertEquals("欢迎注册", vo.getContent());
    }

    @Test
    void updateAndDeleteShouldDelegate() {
        SmsTemplateUpdateBo updateBo = new SmsTemplateUpdateBo();
        updateBo.setId(1L);
        updateBo.setBizCode("login");
        updateBo.setContent("new content");
        smsTemplateService.updateInfo(updateBo);
        verify(iSmsTemplateService).updateById(any(SmsTemplate.class));

        SmsTemplateInfoBo infoBo = new SmsTemplateInfoBo();
        infoBo.setId(1L);
        smsTemplateService.delete(infoBo);
        verify(iSmsTemplateService).removeById(1L);
    }

    @Test
    void getContentByBizCodeShouldReturnContentOrThrow() {
        when(iSmsTemplateService.getOne(any(QueryWrapper.class))).thenReturn(null);
        BizException notFound = assertThrows(BizException.class,
                () -> smsTemplateService.getContentByBizCode("missing"));
        assertEquals(AppError.SMS_TEMPLATE_NOT_FOUND.getCode(), notFound.getErrorCode().getCode());

        SmsTemplate template = new SmsTemplate();
        template.setBizCode("login");
        template.setContent("验证码 ${code}");
        when(iSmsTemplateService.getOne(any(QueryWrapper.class))).thenReturn(template);
        assertEquals("验证码 ${code}", smsTemplateService.getContentByBizCode("login"));
    }
}
