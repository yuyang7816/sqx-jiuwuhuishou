package com.sqx.app.recycle.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.recycle.book.db.service.IBookCategoryService;
import com.sqx.app.recycle.book.db.service.IBookWeightOptionService;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
import com.sqx.app.recycle.book.domain.vo.BookCategoryVo;
import com.sqx.app.recycle.book.domain.vo.BookEstimateConfigVo;
import com.sqx.app.recycle.book.service.BookRecycleService;
import com.sqx.app.recycle.book.support.BookCategoryDefaults;
import com.sqx.app.recycle.book.support.BookEstimateDefaults;
import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书籍回收业务服务实现
 */
@Service
public class BookRecycleServiceImpl implements BookRecycleService {

    private static final Integer STATE_ENABLED = 1;

    @Resource
    private IBookCategoryService iBookCategoryService;
    @Resource
    private IBookWeightOptionService iBookWeightOptionService;

    @Override
    public List<BookCategoryVo> listCategories() {
        List<BookCategory> categoryList = iBookCategoryService.list(new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getState, STATE_ENABLED)
                .orderByAsc(BookCategory::getSort)
                .orderByDesc(BookCategory::getCreateTime));
        if (CollectionUtils.isEmpty(categoryList)) {
            return BookCategoryDefaults.defaultCategories();
        }
        return categoryList.stream().map(category -> {
            BookCategoryVo vo = new BookCategoryVo();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClothesWeightOptionVo> listWeightOptions() {
        List<BookWeightOption> optionList = iBookWeightOptionService.list(new LambdaQueryWrapper<BookWeightOption>()
                .eq(BookWeightOption::getState, STATE_ENABLED)
                .orderByAsc(BookWeightOption::getSort)
                .orderByDesc(BookWeightOption::getCreateTime));
        if (CollectionUtils.isEmpty(optionList)) {
            return BookEstimateDefaults.defaultWeightOptions();
        }
        return optionList.stream().map(option -> {
            ClothesWeightOptionVo vo = new ClothesWeightOptionVo();
            vo.setTitle(option.getTitle());
            vo.setDesc(option.getOptionDesc());
            vo.setEstimateAmount(option.getEstimateAmount());
            vo.setSort(option.getSort());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public BookEstimateConfigVo getEstimateConfig() {
        BookEstimateConfigVo vo = new BookEstimateConfigVo();
        vo.setWeights(listWeightOptions());
        return vo;
    }
}
