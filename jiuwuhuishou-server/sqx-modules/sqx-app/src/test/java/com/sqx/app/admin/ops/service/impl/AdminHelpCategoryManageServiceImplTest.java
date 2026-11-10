package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.AdminHelpCategoryInfoBo;
import com.sqx.app.admin.ops.domain.bo.AdminHelpCategoryPageBo;
import com.sqx.app.admin.ops.domain.bo.AdminHelpCategorySaveBo;
import com.sqx.app.admin.ops.domain.bo.AdminHelpCategoryUpdateBo;
import com.sqx.app.help.db.service.IHelpCategoryService;
import com.sqx.app.help.domain.entity.HelpCategory;
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
class AdminHelpCategoryManageServiceImplTest {

    @Mock
    private IHelpCategoryService iHelpCategoryService;

    @InjectMocks
    private AdminHelpCategoryManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(HelpCategory.class);
    }

    @Test
    void crudAndInfo() {
        service.saveInfo(new AdminHelpCategorySaveBo());
        verify(iHelpCategoryService).save(any(HelpCategory.class));

        AdminHelpCategoryUpdateBo updateBo = new AdminHelpCategoryUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iHelpCategoryService).updateById(any(HelpCategory.class));

        AdminHelpCategoryInfoBo infoBo = new AdminHelpCategoryInfoBo();
        infoBo.setId(1L);
        when(iHelpCategoryService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        HelpCategory category = new HelpCategory();
        category.setId(1L);
        category.setName("用户");
        when(iHelpCategoryService.getById(1L)).thenReturn(category);
        assertEquals("用户", service.info(infoBo).getName());

        service.delete(infoBo);
        verify(iHelpCategoryService).removeById(1L);
    }

    @Test
    void pageInfoShouldQuery() {
        AdminHelpCategoryPageBo pageBo = new AdminHelpCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("帮助");
        pageBo.setClientType(1);
        Page<HelpCategory> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new HelpCategory()));
        when(iHelpCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }
}
