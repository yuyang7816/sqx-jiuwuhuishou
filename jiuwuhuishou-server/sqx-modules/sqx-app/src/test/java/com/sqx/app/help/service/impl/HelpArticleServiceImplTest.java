package com.sqx.app.help.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.help.db.service.IHelpArticleService;
import com.sqx.app.help.db.service.IHelpCategoryService;
import com.sqx.app.help.domain.bo.HelpArticleInfoBo;
import com.sqx.app.help.domain.bo.HelpArticlePageBo;
import com.sqx.app.help.domain.entity.HelpArticle;
import com.sqx.app.help.domain.entity.HelpCategory;
import com.sqx.app.help.domain.vo.HelpArticleDetailVo;
import com.sqx.app.help.domain.vo.HelpArticleVo;
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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HelpArticleServiceImplTest {

    @Mock
    private IHelpArticleService iHelpArticleService;
    @Mock
    private IHelpCategoryService iHelpCategoryService;

    @InjectMocks
    private HelpArticleServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(HelpArticle.class, HelpCategory.class);
    }

    @Test
    void pageShouldShortCircuitWhenNoUserCategories() {
        when(iHelpCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        HelpArticlePageBo pageBo = new HelpArticlePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        assertTrue(service.page(pageBo).getRecords().isEmpty());
        verify(iHelpArticleService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageShouldMapSummaryAndCategory() {
        HelpCategory category = new HelpCategory();
        category.setId(10L);
        category.setName("下单");
        when(iHelpCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));

        HelpArticle blank = new HelpArticle();
        blank.setId(1L);
        blank.setTitle("a");
        blank.setCategoryId(null);
        blank.setContent("   ");

        HelpArticle shortHtml = new HelpArticle();
        shortHtml.setId(2L);
        shortHtml.setTitle("b");
        shortHtml.setCategoryId(10L);
        shortHtml.setContent("<p>hello&nbsp;world</p>");

        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append('x');
        }
        HelpArticle longArticle = new HelpArticle();
        longArticle.setId(3L);
        longArticle.setTitle("c");
        longArticle.setCategoryId(10L);
        longArticle.setContent(longText.toString());

        when(iHelpArticleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenAnswer(inv -> {
            Page<HelpArticle> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(blank, shortHtml, longArticle));
            return page;
        });
        when(iHelpCategoryService.listByIds(anyCollection())).thenReturn(Collections.singletonList(category));

        HelpArticlePageBo pageBo = new HelpArticlePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        java.util.List<HelpArticleVo> records = service.page(pageBo).getRecords();
        assertEquals(3, records.size());
        HelpArticleVo shortVo = records.get(1);
        assertEquals("hello world", shortVo.getSummary());
        assertEquals("下单", shortVo.getTag());
        HelpArticleVo longVo = records.get(2);
        assertTrue(longVo.getSummary().endsWith("..."));
        assertEquals(83, longVo.getSummary().length());
    }

    @Test
    void pageShouldHandleEmptyCategoryMap() {
        HelpCategory category = new HelpCategory();
        category.setId(10L);
        when(iHelpCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));

        HelpArticle article = new HelpArticle();
        article.setId(1L);
        article.setCategoryId(null);
        article.setContent(null);
        Page<HelpArticle> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(article));
        when(iHelpArticleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        HelpArticlePageBo pageBo = new HelpArticlePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        HelpArticleVo vo = service.page(pageBo).getRecords().get(0);
        assertEquals("", vo.getSummary());
        assertEquals("", vo.getTag());
        verify(iHelpCategoryService, never()).listByIds(anyCollection());
    }

    @Test
    void infoShouldValidateVisibility() {
        HelpArticleInfoBo infoBo = new HelpArticleInfoBo();
        infoBo.setId(1L);
        when(iHelpArticleService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        HelpArticle article = new HelpArticle();
        article.setId(1L);
        article.setCategoryId(99L);
        article.setTitle("t");
        article.setContent("c");
        when(iHelpArticleService.getById(1L)).thenReturn(article);
        when(iHelpCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertThrows(BizException.class, () -> service.info(infoBo));

        HelpCategory category = new HelpCategory();
        category.setId(10L);
        category.setName("常见问题");
        when(iHelpCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));
        assertThrows(BizException.class, () -> service.info(infoBo));

        article.setCategoryId(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        article.setCategoryId(10L);
        when(iHelpCategoryService.getById(10L)).thenReturn(category);
        HelpArticleDetailVo vo = service.info(infoBo);
        assertEquals("常见问题", vo.getTag());
        assertEquals("t", vo.getTitle());

        when(iHelpCategoryService.getById(10L)).thenReturn(null);
        assertEquals("", service.info(infoBo).getTag());
    }
}
