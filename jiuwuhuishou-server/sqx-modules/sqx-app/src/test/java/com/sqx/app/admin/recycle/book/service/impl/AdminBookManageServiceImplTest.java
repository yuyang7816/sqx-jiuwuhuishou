package com.sqx.app.admin.recycle.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategorySaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryUpdateBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionSaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionUpdateBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.book.db.service.IBookCategoryService;
import com.sqx.app.recycle.book.db.service.IBookWeightOptionService;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
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

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminBookManageServiceImplTest {

    @Mock
    private IBookCategoryService iBookCategoryService;
    @Mock
    private IBookWeightOptionService iBookWeightOptionService;

    @InjectMocks
    private AdminBookManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(BookCategory.class, BookWeightOption.class);
    }

    @Test
    void categoryCrud() {
        AdminBookCategorySaveBo saveBo = new AdminBookCategorySaveBo();
        service.saveCategory(saveBo);
        ArgumentCaptor<BookCategory> captor = ArgumentCaptor.forClass(BookCategory.class);
        verify(iBookCategoryService).save(captor.capture());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());

        saveBo.setSort(3);
        saveBo.setState(2);
        service.saveCategory(saveBo);

        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iBookCategoryService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoCategory(infoBo));

        BookCategory entity = new BookCategory();
        entity.setId(1L);
        when(iBookCategoryService.getById(1L)).thenReturn(entity);
        assertEquals(1L, service.infoCategory(infoBo).getId());

        AdminBookCategoryUpdateBo updateBo = new AdminBookCategoryUpdateBo();
        updateBo.setId(1L);
        service.updateCategory(updateBo);
        verify(iBookCategoryService).updateById(any(BookCategory.class));
        service.deleteCategory(infoBo);
        verify(iBookCategoryService).removeById(1L);

        AdminBookCategoryPageBo pageBo = new AdminBookCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("书");
        pageBo.setState(1);
        Page<BookCategory> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(entity));
        when(iBookCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageCategory(pageBo).getRecords().size());
    }

    @Test
    void weightOptionCrudAndDefaults() {
        AdminBookWeightOptionSaveBo saveBo = new AdminBookWeightOptionSaveBo();
        saveBo.setTitle("5kg");
        service.saveWeightOption(saveBo);
        ArgumentCaptor<BookWeightOption> captor = ArgumentCaptor.forClass(BookWeightOption.class);
        verify(iBookWeightOptionService).save(captor.capture());
        assertEquals("", captor.getValue().getOptionDesc());
        assertEquals(0, BigDecimal.ZERO.compareTo(captor.getValue().getEstimateAmount()));
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());

        saveBo.setDesc("说明");
        saveBo.setEstimateAmount(new BigDecimal("12"));
        saveBo.setSort(2);
        saveBo.setState(2);
        service.saveWeightOption(saveBo);
        verify(iBookWeightOptionService, times(2)).save(any(BookWeightOption.class));

        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(2L);
        when(iBookWeightOptionService.getById(2L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoWeightOption(infoBo));

        BookWeightOption option = new BookWeightOption();
        option.setId(2L);
        when(iBookWeightOptionService.getById(2L)).thenReturn(option);
        assertEquals(2L, service.infoWeightOption(infoBo).getId());

        AdminBookWeightOptionUpdateBo updateBo = new AdminBookWeightOptionUpdateBo();
        updateBo.setId(2L);
        updateBo.setDesc("新说明");
        service.updateWeightOption(updateBo);
        verify(iBookWeightOptionService).updateById(any(BookWeightOption.class));
        service.deleteWeightOption(infoBo);
        verify(iBookWeightOptionService).removeById(2L);

        AdminBookWeightOptionPageBo pageBo = new AdminBookWeightOptionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setTitle("5");
        pageBo.setState(1);
        Page<BookWeightOption> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(option));
        when(iBookWeightOptionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageWeightOption(pageBo).getRecords().size());
    }
}
