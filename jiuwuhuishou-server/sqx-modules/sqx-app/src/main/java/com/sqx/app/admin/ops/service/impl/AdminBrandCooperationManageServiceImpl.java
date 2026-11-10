package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminBrandCooperationManageService;
import com.sqx.app.brand.db.service.IBrandCooperationService;
import com.sqx.app.brand.domain.entity.BrandCooperation;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminBrandCooperationManageServiceImpl implements AdminBrandCooperationManageService {

    @Resource
    private IBrandCooperationService iBrandCooperationService;

    @Override
    public IPage<BrandCooperation> pageInfo(AdminBrandCooperationPageBo pageBo) {
        Page<BrandCooperation> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<BrandCooperation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getBrandName()), BrandCooperation::getBrandName, pageBo.getBrandName())
                .like(StringUtils.isNotBlank(pageBo.getContactName()), BrandCooperation::getContactName, pageBo.getContactName())
                .like(StringUtils.isNotBlank(pageBo.getMobile()), BrandCooperation::getMobile, pageBo.getMobile())
                .eq(Objects.nonNull(pageBo.getStatus()), BrandCooperation::getStatus, pageBo.getStatus())
                .orderByDesc(BrandCooperation::getCreateTime);
        return iBrandCooperationService.page(initPage, wrapper);
    }

    @Override
    public BrandCooperation info(AdminBrandCooperationInfoBo infoBo) {
        BrandCooperation entity = iBrandCooperationService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("品牌合作意向不存在");
        }
        return entity;
    }

    @Override
    public void updateStatus(AdminBrandCooperationUpdateStatusBo updateBo) {
        BrandCooperation entity = iBrandCooperationService.getById(updateBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("品牌合作意向不存在");
        }
        entity.setStatus(updateBo.getStatus());
        if (StringUtils.isNotBlank(updateBo.getRemark())) {
            entity.setRemark(updateBo.getRemark());
        }
        iBrandCooperationService.updateById(entity);
    }
}
