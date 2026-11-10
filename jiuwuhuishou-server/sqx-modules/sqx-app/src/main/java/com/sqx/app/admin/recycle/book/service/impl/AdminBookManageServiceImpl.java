package com.sqx.app.admin.recycle.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategorySaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryUpdateBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionSaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionUpdateBo;
import com.sqx.app.admin.recycle.book.service.AdminBookManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.book.db.service.IBookCategoryService;
import com.sqx.app.recycle.book.db.service.IBookWeightOptionService;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

@Service
public class AdminBookManageServiceImpl implements AdminBookManageService {

    @Resource
    private IBookCategoryService iBookCategoryService;
    @Resource
    private IBookWeightOptionService iBookWeightOptionService;

    @Override
    public void saveCategory(AdminBookCategorySaveBo saveBo) {
        BookCategory entity = new BookCategory();
        BeanUtils.copyProperties(saveBo, entity);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
        iBookCategoryService.save(entity);
    }

    @Override
    public IPage<BookCategory> pageCategory(AdminBookCategoryPageBo pageBo) {
        Page<BookCategory> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getName()), BookCategory::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), BookCategory::getState, pageBo.getState())
                .orderByAsc(BookCategory::getSort)
                .orderByDesc(BookCategory::getCreateTime);
        return iBookCategoryService.page(initPage, wrapper);
    }

    @Override
    public BookCategory infoCategory(AdminRecycleIdBo infoBo) {
        BookCategory entity = iBookCategoryService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("书籍分类不存在");
        }
        return entity;
    }

    @Override
    public void updateCategory(AdminBookCategoryUpdateBo updateBo) {
        infoCategory(newId(updateBo.getId()));
        BookCategory entity = new BookCategory();
        BeanUtils.copyProperties(updateBo, entity);
        iBookCategoryService.updateById(entity);
    }

    @Override
    public void deleteCategory(AdminRecycleIdBo infoBo) {
        iBookCategoryService.removeById(infoBo.getId());
    }

    @Override
    public void saveWeightOption(AdminBookWeightOptionSaveBo saveBo) {
        BookWeightOption entity = new BookWeightOption();
        BeanUtils.copyProperties(saveBo, entity);
        entity.setOptionDesc(saveBo.getDesc());
        fillWeightOptionDefaults(entity);
        iBookWeightOptionService.save(entity);
    }

    @Override
    public IPage<BookWeightOption> pageWeightOption(AdminBookWeightOptionPageBo pageBo) {
        Page<BookWeightOption> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<BookWeightOption> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getTitle()), BookWeightOption::getTitle, pageBo.getTitle())
                .eq(Objects.nonNull(pageBo.getState()), BookWeightOption::getState, pageBo.getState())
                .orderByAsc(BookWeightOption::getSort)
                .orderByDesc(BookWeightOption::getCreateTime);
        return iBookWeightOptionService.page(initPage, wrapper);
    }

    @Override
    public BookWeightOption infoWeightOption(AdminRecycleIdBo infoBo) {
        BookWeightOption entity = iBookWeightOptionService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("书籍预约配置不存在");
        }
        return entity;
    }

    @Override
    public void updateWeightOption(AdminBookWeightOptionUpdateBo updateBo) {
        infoWeightOption(newId(updateBo.getId()));
        BookWeightOption entity = new BookWeightOption();
        BeanUtils.copyProperties(updateBo, entity);
        entity.setOptionDesc(updateBo.getDesc());
        fillWeightOptionDefaults(entity);
        iBookWeightOptionService.updateById(entity);
    }

    @Override
    public void deleteWeightOption(AdminRecycleIdBo infoBo) {
        iBookWeightOptionService.removeById(infoBo.getId());
    }

    private void fillWeightOptionDefaults(BookWeightOption entity) {
        if (entity.getOptionDesc() == null) {
            entity.setOptionDesc("");
        }
        if (entity.getEstimateAmount() == null) {
            entity.setEstimateAmount(BigDecimal.ZERO);
        }
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
    }

    private AdminRecycleIdBo newId(Long id) {
        AdminRecycleIdBo bo = new AdminRecycleIdBo();
        bo.setId(id);
        return bo;
    }
}
