package com.sqx.app.admin.recycle.digital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.digital.domain.bo.*;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalModelVo;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalPricingVo;
import com.sqx.app.admin.recycle.digital.service.AdminDigitalManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.digital.db.service.IDigitalBrandService;
import com.sqx.app.recycle.digital.db.service.IDigitalModelService;
import com.sqx.app.recycle.digital.db.service.IDigitalPricingRuleService;
import com.sqx.app.recycle.digital.domain.entity.DigitalBrand;
import com.sqx.app.recycle.digital.domain.entity.DigitalModel;
import com.sqx.app.recycle.digital.domain.entity.DigitalPricingRule;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminDigitalManageServiceImpl implements AdminDigitalManageService {

    private static final String DEFAULT_SCENE = "digital";

    @Resource
    private IDigitalBrandService iDigitalBrandService;
    @Resource
    private IDigitalModelService iDigitalModelService;
    @Resource
    private IDigitalPricingRuleService iDigitalPricingRuleService;

    @Override
    public void saveBrand(AdminDigitalBrandSaveBo saveBo) {
        DigitalBrand entity = new DigitalBrand();
        BeanUtils.copyProperties(saveBo, entity);
        if (StringUtils.isBlank(entity.getScene())) {
            entity.setScene(DEFAULT_SCENE);
        }
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
        iDigitalBrandService.save(entity);
    }

    @Override
    public IPage<DigitalBrand> pageBrand(AdminDigitalBrandPageBo pageBo) {
        Page<DigitalBrand> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<DigitalBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(pageBo.getScene()), DigitalBrand::getScene, pageBo.getScene())
                .like(StringUtils.isNotBlank(pageBo.getName()), DigitalBrand::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), DigitalBrand::getState, pageBo.getState())
                .orderByAsc(DigitalBrand::getSort)
                .orderByDesc(DigitalBrand::getCreateTime);
        return iDigitalBrandService.page(initPage, wrapper);
    }

    @Override
    public DigitalBrand infoBrand(AdminRecycleIdBo infoBo) {
        DigitalBrand entity = iDigitalBrandService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("数码品牌不存在");
        }
        return entity;
    }

    @Override
    public void updateBrand(AdminDigitalBrandUpdateBo updateBo) {
        infoBrand(newId(updateBo.getId()));
        DigitalBrand entity = new DigitalBrand();
        BeanUtils.copyProperties(updateBo, entity);
        iDigitalBrandService.updateById(entity);
    }

    @Override
    public void deleteBrand(AdminRecycleIdBo infoBo) {
        long modelCount = iDigitalModelService.count(new LambdaQueryWrapper<DigitalModel>()
                .eq(DigitalModel::getBrandId, infoBo.getId()));
        if (modelCount > 0) {
            throw new BizException("该品牌下仍有型号，请先删除型号");
        }
        iDigitalBrandService.removeById(infoBo.getId());
    }

    @Override
    public void saveModel(AdminDigitalModelSaveBo saveBo) {
        requireBrand(saveBo.getBrandId());
        DigitalModel entity = new DigitalModel();
        BeanUtils.copyProperties(saveBo, entity);
        if (entity.getState() == null) {
            entity.setState(1);
        }
        iDigitalModelService.save(entity);
    }

    @Override
    public IPage<AdminDigitalModelVo> pageModel(AdminDigitalModelPageBo pageBo) {
        Page<DigitalModel> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<DigitalModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(pageBo.getBrandId()), DigitalModel::getBrandId, pageBo.getBrandId())
                .like(StringUtils.isNotBlank(pageBo.getName()), DigitalModel::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getState()), DigitalModel::getState, pageBo.getState())
                .orderByDesc(DigitalModel::getCreateTime);
        IPage<DigitalModel> iPage = iDigitalModelService.page(initPage, wrapper);
        List<AdminDigitalModelVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (DigitalModel record : iPage.getRecords()) {
                voList.add(toModelVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminDigitalModelVo infoModel(AdminRecycleIdBo infoBo) {
        DigitalModel entity = iDigitalModelService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("数码型号不存在");
        }
        return toModelVo(entity);
    }

    @Override
    public void updateModel(AdminDigitalModelUpdateBo updateBo) {
        infoModel(newId(updateBo.getId()));
        requireBrand(updateBo.getBrandId());
        DigitalModel entity = new DigitalModel();
        BeanUtils.copyProperties(updateBo, entity);
        iDigitalModelService.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(AdminRecycleIdBo infoBo) {
        iDigitalPricingRuleService.remove(new LambdaQueryWrapper<DigitalPricingRule>()
                .eq(DigitalPricingRule::getModelId, infoBo.getId()));
        iDigitalModelService.removeById(infoBo.getId());
    }

    @Override
    public void savePricing(AdminDigitalPricingSaveBo saveBo) {
        requireModel(saveBo.getModelId());
        DigitalPricingRule entity = new DigitalPricingRule();
        BeanUtils.copyProperties(saveBo, entity);
        iDigitalPricingRuleService.save(entity);
    }

    @Override
    public IPage<AdminDigitalPricingVo> pagePricing(AdminDigitalPricingPageBo pageBo) {
        Page<DigitalPricingRule> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<DigitalPricingRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(pageBo.getModelId()), DigitalPricingRule::getModelId, pageBo.getModelId())
                .eq(StringUtils.isNotBlank(pageBo.getRuleKey()), DigitalPricingRule::getRuleKey, pageBo.getRuleKey())
                .orderByDesc(DigitalPricingRule::getCreateTime);
        IPage<DigitalPricingRule> iPage = iDigitalPricingRuleService.page(initPage, wrapper);
        List<AdminDigitalPricingVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (DigitalPricingRule record : iPage.getRecords()) {
                voList.add(toPricingVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminDigitalPricingVo infoPricing(AdminRecycleIdBo infoBo) {
        DigitalPricingRule entity = iDigitalPricingRuleService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("估价规则不存在");
        }
        return toPricingVo(entity);
    }

    @Override
    public void updatePricing(AdminDigitalPricingUpdateBo updateBo) {
        infoPricing(newId(updateBo.getId()));
        requireModel(updateBo.getModelId());
        DigitalPricingRule entity = new DigitalPricingRule();
        BeanUtils.copyProperties(updateBo, entity);
        iDigitalPricingRuleService.updateById(entity);
    }

    @Override
    public void deletePricing(AdminRecycleIdBo infoBo) {
        iDigitalPricingRuleService.removeById(infoBo.getId());
    }

    private void requireBrand(Long brandId) {
        if (Objects.isNull(iDigitalBrandService.getById(brandId))) {
            throw new BizException("数码品牌不存在");
        }
    }

    private void requireModel(Long modelId) {
        if (Objects.isNull(iDigitalModelService.getById(modelId))) {
            throw new BizException("数码型号不存在");
        }
    }

    private AdminDigitalModelVo toModelVo(DigitalModel entity) {
        AdminDigitalModelVo vo = new AdminDigitalModelVo();
        BeanUtils.copyProperties(entity, vo);
        DigitalBrand brand = iDigitalBrandService.getById(entity.getBrandId());
        if (brand != null) {
            vo.setBrandName(brand.getName());
        }
        return vo;
    }

    private AdminDigitalPricingVo toPricingVo(DigitalPricingRule entity) {
        AdminDigitalPricingVo vo = new AdminDigitalPricingVo();
        BeanUtils.copyProperties(entity, vo);
        DigitalModel model = iDigitalModelService.getById(entity.getModelId());
        if (model != null) {
            vo.setModelName(model.getName());
        }
        return vo;
    }

    private AdminRecycleIdBo newId(Long id) {
        AdminRecycleIdBo bo = new AdminRecycleIdBo();
        bo.setId(id);
        return bo;
    }
}
