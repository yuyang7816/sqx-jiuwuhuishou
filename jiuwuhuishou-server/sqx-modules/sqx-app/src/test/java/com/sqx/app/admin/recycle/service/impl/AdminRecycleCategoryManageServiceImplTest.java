package com.sqx.app.admin.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleCategoryInfoBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleCategoryPageBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleCategorySaveBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleCategoryUpdateBo;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
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
class AdminRecycleCategoryManageServiceImplTest {

    @Mock
    private IRecycleCategoryService iRecycleCategoryService;

    @InjectMocks
    private AdminRecycleCategoryManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(RecycleCategory.class);
    }

    @Test
    void crudAndPage() {
        service.saveInfo(new AdminRecycleCategorySaveBo());
        verify(iRecycleCategoryService).save(any(RecycleCategory.class));

        AdminRecycleCategoryUpdateBo updateBo = new AdminRecycleCategoryUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iRecycleCategoryService).updateById(any(RecycleCategory.class));

        AdminRecycleCategoryInfoBo infoBo = new AdminRecycleCategoryInfoBo();
        infoBo.setId(1L);
        when(iRecycleCategoryService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        RecycleCategory entity = new RecycleCategory();
        entity.setId(1L);
        entity.setName("家电");
        when(iRecycleCategoryService.getById(1L)).thenReturn(entity);
        assertEquals("家电", service.info(infoBo).getName());

        service.delete(infoBo);
        verify(iRecycleCategoryService).removeById(1L);

        AdminRecycleCategoryPageBo pageBo = new AdminRecycleCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("家");
        pageBo.setCode("appliance");
        pageBo.setState(1);
        Page<RecycleCategory> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(entity));
        when(iRecycleCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }
}
