package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.AdminHelpArticleInfoBo;
import com.sqx.app.admin.ops.domain.bo.AdminHelpArticlePageBo;
import com.sqx.app.admin.ops.domain.bo.AdminHelpArticleSaveBo;
import com.sqx.app.admin.ops.domain.bo.AdminHelpArticleUpdateBo;
import com.sqx.app.help.db.service.IHelpArticleService;
import com.sqx.app.help.domain.entity.HelpArticle;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminHelpArticleManageServiceImplTest {

    @Mock
    private IHelpArticleService iHelpArticleService;

    @InjectMocks
    private AdminHelpArticleManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(HelpArticle.class);
    }

    @Test
    void crudAndInfo() {
        service.saveInfo(new AdminHelpArticleSaveBo());
        verify(iHelpArticleService).save(any(HelpArticle.class));

        AdminHelpArticleUpdateBo updateBo = new AdminHelpArticleUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iHelpArticleService).updateById(any(HelpArticle.class));

        AdminHelpArticleInfoBo infoBo = new AdminHelpArticleInfoBo();
        infoBo.setId(1L);
        when(iHelpArticleService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        HelpArticle article = new HelpArticle();
        article.setId(1L);
        article.setTitle("帮助");
        when(iHelpArticleService.getById(1L)).thenReturn(article);
        assertEquals("帮助", service.info(infoBo).getTitle());

        service.delete(infoBo);
        verify(iHelpArticleService).removeById(1L);
    }

    @Test
    void pageInfoShouldQuery() {
        AdminHelpArticlePageBo pageBo = new AdminHelpArticlePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setCategoryId(2L);
        pageBo.setTitle("回收");
        Page<HelpArticle> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new HelpArticle()));
        when(iHelpArticleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }
}
