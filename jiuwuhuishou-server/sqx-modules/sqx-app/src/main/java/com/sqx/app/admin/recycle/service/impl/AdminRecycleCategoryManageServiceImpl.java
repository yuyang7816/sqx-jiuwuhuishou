package com.sqx.app.admin.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.admin.recycle.service.AdminRecycleCategoryManageService;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminRecycleCategoryManageServiceImpl implements AdminRecycleCategoryManageService {

    @Resource
    private IRecycleCategoryService iRecycleCategoryService;

    @Override
    public void saveInfo(AdminRecycleCategorySaveBo saveBo) {
        RecycleCategory entity = new RecycleCategory();
        BeanUtils.copyProperties(saveBo, entity);
        iRecycleCategoryService.save(entity);
    }

    @Override
    public IPage<RecycleCategory> pageInfo(AdminRecycleCategoryPageBo pageBo) {
        Page<RecycleCategory> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<RecycleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getName()), RecycleCategory::getName, pageBo.getName())
                .eq(StringUtils.isNotBlank(pageBo.getCode()), RecycleCategory::getCode, pageBo.getCode())
                .eq(Objects.nonNull(pageBo.getState()), RecycleCategory::getState, pageBo.getState())
                .orderByAsc(RecycleCategory::getSort)
                .orderByDesc(RecycleCategory::getCreateTime);
        return iRecycleCategoryService.page(initPage, wrapper);
    }

    @Override
    public RecycleCategory info(AdminRecycleCategoryInfoBo infoBo) {
        RecycleCategory entity = iRecycleCategoryService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("回收分类不存在");
        }
        return entity;
    }

    @Override
    public void updateInfo(AdminRecycleCategoryUpdateBo updateBo) {
        RecycleCategory entity = new RecycleCategory();
        BeanUtils.copyProperties(updateBo, entity);
        iRecycleCategoryService.updateById(entity);
    }

    @Override
    public void delete(AdminRecycleCategoryInfoBo infoBo) {
        iRecycleCategoryService.removeById(infoBo.getId());
    }
}
