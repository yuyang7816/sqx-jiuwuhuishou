package com.sqx.app.admin.recycle.clothes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryPageBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategorySaveBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryUpdateBo;
import com.sqx.app.admin.recycle.clothes.service.AdminClothesManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.clothes.db.service.IClothesCategoryService;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminClothesManageServiceImpl implements AdminClothesManageService {

    @Resource
    private IClothesCategoryService iClothesCategoryService;

    @Override
    public void saveCategory(AdminClothesCategorySaveBo saveBo) {
        ClothesCategory entity = new ClothesCategory();
        BeanUtils.copyProperties(saveBo, entity);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
        iClothesCategoryService.save(entity);
    }

    @Override
    public IPage<ClothesCategory> pageCategory(AdminClothesCategoryPageBo pageBo) {
        Page<ClothesCategory> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ClothesCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getName()), ClothesCategory::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), ClothesCategory::getState, pageBo.getState())
                .orderByAsc(ClothesCategory::getSort)
                .orderByDesc(ClothesCategory::getCreateTime);
        return iClothesCategoryService.page(initPage, wrapper);
    }

    @Override
    public ClothesCategory infoCategory(AdminRecycleIdBo infoBo) {
        ClothesCategory entity = iClothesCategoryService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("旧衣分类不存在");
        }
        return entity;
    }

    @Override
    public void updateCategory(AdminClothesCategoryUpdateBo updateBo) {
        infoCategory(newId(updateBo.getId()));
        ClothesCategory entity = new ClothesCategory();
        BeanUtils.copyProperties(updateBo, entity);
        iClothesCategoryService.updateById(entity);
    }

    @Override
    public void deleteCategory(AdminRecycleIdBo infoBo) {
        iClothesCategoryService.removeById(infoBo.getId());
    }

    private AdminRecycleIdBo newId(Long id) {
        AdminRecycleIdBo bo = new AdminRecycleIdBo();
        bo.setId(id);
        return bo;
    }
}
