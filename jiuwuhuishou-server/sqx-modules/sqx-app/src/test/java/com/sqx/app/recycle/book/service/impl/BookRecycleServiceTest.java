package com.sqx.app.recycle.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.recycle.book.db.service.IBookCategoryService;
import com.sqx.app.recycle.book.db.service.IBookWeightOptionService;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
import com.sqx.app.recycle.book.domain.vo.BookCategoryVo;
import com.sqx.app.recycle.book.domain.vo.BookEstimateConfigVo;
import com.sqx.app.recycle.book.support.BookCategoryDefaults;
import com.sqx.app.recycle.book.support.BookEstimateDefaults;
import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import com.sqx.testsupport.PrivateCtor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRecycleServiceTest {

    @Mock
    private IBookCategoryService iBookCategoryService;
    @Mock
    private IBookWeightOptionService iBookWeightOptionService;

    @InjectMocks
    private BookRecycleServiceImpl bookRecycleService;

    @BeforeAll
    static void init() {
        MybatisPlusLambdaInit.init(BookCategory.class, BookWeightOption.class);
        PrivateCtor.invoke(BookCategoryDefaults.class);
        PrivateCtor.invoke(BookEstimateDefaults.class);
    }

    @Test
    void listCategoriesShouldFallbackDefaults() {
        when(iBookCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        List<BookCategoryVo> list = bookRecycleService.listCategories();
        assertFalse(list.isEmpty());
        assertEquals(BookCategoryDefaults.defaultCategories().size(), list.size());
    }

    @Test
    void listCategoriesShouldMapDbRows() {
        BookCategory category = new BookCategory();
        category.setId(1L);
        category.setName("教材");
        category.setState(1);
        when(iBookCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));
        List<BookCategoryVo> list = bookRecycleService.listCategories();
        assertEquals(1, list.size());
        assertEquals("教材", list.get(0).getName());
    }

    @Test
    void listWeightOptionsShouldFallbackAndMap() {
        when(iBookWeightOptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        List<ClothesWeightOptionVo> defaults = bookRecycleService.listWeightOptions();
        assertEquals(3, defaults.size());

        BookWeightOption option = new BookWeightOption();
        option.setTitle("5kg");
        option.setOptionDesc("小批量");
        option.setEstimateAmount(new BigDecimal("3.00"));
        option.setSort(1);
        when(iBookWeightOptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(option));
        List<ClothesWeightOptionVo> mapped = bookRecycleService.listWeightOptions();
        assertEquals("5kg", mapped.get(0).getTitle());
        assertEquals("小批量", mapped.get(0).getDesc());
    }

    @Test
    void getEstimateConfigShouldReuseWeightOptions() {
        when(iBookWeightOptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        BookEstimateConfigVo config = bookRecycleService.getEstimateConfig();
        assertEquals(3, config.getWeights().size());
    }
}
