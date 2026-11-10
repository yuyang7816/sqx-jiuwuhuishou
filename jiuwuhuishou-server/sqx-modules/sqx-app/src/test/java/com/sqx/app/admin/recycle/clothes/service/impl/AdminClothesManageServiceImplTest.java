package com.sqx.app.admin.recycle.clothes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryPageBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategorySaveBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryUpdateBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.clothes.db.service.IClothesCategoryService;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
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
class AdminClothesManageServiceImplTest {

    @Mock
    private IClothesCategoryService iClothesCategoryService;

    @InjectMocks
    private AdminClothesManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(ClothesCategory.class);
    }

    @Test
    void categoryCrudAndDefaults() {
        AdminClothesCategorySaveBo saveBo = new AdminClothesCategorySaveBo();
        saveBo.setName("旧衣");
        service.saveCategory(saveBo);
        ArgumentCaptor<ClothesCategory> captor = ArgumentCaptor.forClass(ClothesCategory.class);
        verify(iClothesCategoryService).save(captor.capture());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());

        saveBo.setSort(2);
        saveBo.setState(2);
        service.saveCategory(saveBo);
        verify(iClothesCategoryService, times(2)).save(any(ClothesCategory.class));

        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iClothesCategoryService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoCategory(infoBo));

        ClothesCategory entity = new ClothesCategory();
        entity.setId(1L);
        entity.setName("旧衣");
        when(iClothesCategoryService.getById(1L)).thenReturn(entity);
        assertEquals("旧衣", service.infoCategory(infoBo).getName());

        AdminClothesCategoryUpdateBo updateBo = new AdminClothesCategoryUpdateBo();
        updateBo.setId(1L);
        updateBo.setName("更新");
        service.updateCategory(updateBo);
        verify(iClothesCategoryService).updateById(any(ClothesCategory.class));

        service.deleteCategory(infoBo);
        verify(iClothesCategoryService).removeById(1L);
    }

    @Test
    void pageCategoryShouldQuery() {
        AdminClothesCategoryPageBo pageBo = new AdminClothesCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("衣");
        pageBo.setState(1);
        Page<ClothesCategory> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new ClothesCategory()));
        when(iClothesCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageCategory(pageBo).getRecords().size());
    }
}
