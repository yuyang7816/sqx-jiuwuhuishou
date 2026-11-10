package com.sqx.app.admin.recycle.appliance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.appliance.domain.bo.*;
import com.sqx.app.admin.recycle.appliance.domain.vo.AdminApplianceModelVo;
import com.sqx.app.admin.recycle.appliance.service.AdminApplianceManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.appliance.db.service.IApplianceCategoryService;
import com.sqx.app.recycle.appliance.db.service.IApplianceHeroFlowService;
import com.sqx.app.recycle.appliance.db.service.IApplianceModelService;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceCategory;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceModel;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminApplianceManageServiceImpl implements AdminApplianceManageService {

    @Resource
    private IApplianceCategoryService iApplianceCategoryService;
    @Resource
    private IApplianceModelService iApplianceModelService;
    @Resource
    private IApplianceHeroFlowService iApplianceHeroFlowService;

    @Override
    public void saveCategory(AdminApplianceCategorySaveBo saveBo) {
        ApplianceCategory entity = new ApplianceCategory();
        BeanUtils.copyProperties(saveBo, entity);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
        iApplianceCategoryService.save(entity);
    }

    @Override
    public IPage<ApplianceCategory> pageCategory(AdminApplianceCategoryPageBo pageBo) {
        Page<ApplianceCategory> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ApplianceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getName()), ApplianceCategory::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), ApplianceCategory::getState, pageBo.getState())
                .orderByAsc(ApplianceCategory::getSort)
                .orderByDesc(ApplianceCategory::getCreateTime);
        return iApplianceCategoryService.page(initPage, wrapper);
    }

    @Override
    public ApplianceCategory infoCategory(AdminRecycleIdBo infoBo) {
        ApplianceCategory entity = iApplianceCategoryService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("家电品类不存在");
        }
        return entity;
    }

    @Override
    public void updateCategory(AdminApplianceCategoryUpdateBo updateBo) {
        infoCategory(newId(updateBo.getId()));
        ApplianceCategory entity = new ApplianceCategory();
        BeanUtils.copyProperties(updateBo, entity);
        iApplianceCategoryService.updateById(entity);
    }

    @Override
    public void deleteCategory(AdminRecycleIdBo infoBo) {
        long modelCount = iApplianceModelService.count(new LambdaQueryWrapper<ApplianceModel>()
                .eq(ApplianceModel::getCategoryId, infoBo.getId()));
        if (modelCount > 0) {
            throw new BizException("该品类下仍有型号，请先删除型号");
        }
        iApplianceCategoryService.removeById(infoBo.getId());
    }

    @Override
    public void saveModel(AdminApplianceModelSaveBo saveBo) {
        requireCategory(saveBo.getCategoryId());
        ApplianceModel entity = new ApplianceModel();
        BeanUtils.copyProperties(saveBo, entity);
        if (entity.getState() == null) {
            entity.setState(1);
        }
        iApplianceModelService.save(entity);
    }

    @Override
    public IPage<AdminApplianceModelVo> pageModel(AdminApplianceModelPageBo pageBo) {
        Page<ApplianceModel> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ApplianceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(pageBo.getCategoryId()), ApplianceModel::getCategoryId, pageBo.getCategoryId())
                .like(StringUtils.isNotBlank(pageBo.getName()), ApplianceModel::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), ApplianceModel::getState, pageBo.getState())
                .orderByDesc(ApplianceModel::getCreateTime);
        IPage<ApplianceModel> iPage = iApplianceModelService.page(initPage, wrapper);
        List<AdminApplianceModelVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (ApplianceModel record : iPage.getRecords()) {
                voList.add(toModelVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminApplianceModelVo infoModel(AdminRecycleIdBo infoBo) {
        ApplianceModel entity = iApplianceModelService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("家电型号不存在");
        }
        return toModelVo(entity);
    }

    @Override
    public void updateModel(AdminApplianceModelUpdateBo updateBo) {
        infoModel(newId(updateBo.getId()));
        requireCategory(updateBo.getCategoryId());
        ApplianceModel entity = new ApplianceModel();
        BeanUtils.copyProperties(updateBo, entity);
        iApplianceModelService.updateById(entity);
    }

    @Override
    public void deleteModel(AdminRecycleIdBo infoBo) {
        iApplianceModelService.removeById(infoBo.getId());
    }

    @Override
    public void saveHeroFlow(AdminApplianceHeroFlowSaveBo saveBo) {
        ApplianceHeroFlow entity = new ApplianceHeroFlow();
        BeanUtils.copyProperties(saveBo, entity);
        fillHeroFlowDefaults(entity);
        iApplianceHeroFlowService.save(entity);
    }

    @Override
    public IPage<ApplianceHeroFlow> pageHeroFlow(AdminApplianceHeroFlowPageBo pageBo) {
        Page<ApplianceHeroFlow> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ApplianceHeroFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getName()), ApplianceHeroFlow::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), ApplianceHeroFlow::getState, pageBo.getState())
                .orderByAsc(ApplianceHeroFlow::getSort)
                .orderByDesc(ApplianceHeroFlow::getCreateTime);
        return iApplianceHeroFlowService.page(initPage, wrapper);
    }

    @Override
    public ApplianceHeroFlow infoHeroFlow(AdminRecycleIdBo infoBo) {
        ApplianceHeroFlow entity = iApplianceHeroFlowService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("家电回收流程步骤不存在");
        }
        return entity;
    }

    @Override
    public void updateHeroFlow(AdminApplianceHeroFlowUpdateBo updateBo) {
        infoHeroFlow(newId(updateBo.getId()));
        ApplianceHeroFlow entity = new ApplianceHeroFlow();
        BeanUtils.copyProperties(updateBo, entity);
        fillHeroFlowDefaults(entity);
        iApplianceHeroFlowService.updateById(entity);
    }

    @Override
    public void deleteHeroFlow(AdminRecycleIdBo infoBo) {
        iApplianceHeroFlowService.removeById(infoBo.getId());
    }

    private void fillHeroFlowDefaults(ApplianceHeroFlow entity) {
        if (entity.getIcon() == null) {
            entity.setIcon("");
        }
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
    }

    private void requireCategory(Long categoryId) {
        if (Objects.isNull(iApplianceCategoryService.getById(categoryId))) {
            throw new BizException("家电品类不存在");
        }
    }

    private AdminApplianceModelVo toModelVo(ApplianceModel entity) {
        AdminApplianceModelVo vo = new AdminApplianceModelVo();
        BeanUtils.copyProperties(entity, vo);
        ApplianceCategory category = iApplianceCategoryService.getById(entity.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        return vo;
    }

    private AdminRecycleIdBo newId(Long id) {
        AdminRecycleIdBo bo = new AdminRecycleIdBo();
        bo.setId(id);
        return bo;
    }
}
