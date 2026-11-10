package com.sqx.app.admin.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.admin.recycle.service.AdminServiceAreaManageService;
import com.sqx.app.recycle.config.db.service.IServiceAreaService;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminServiceAreaManageServiceImpl implements AdminServiceAreaManageService {

    @Resource
    private IServiceAreaService iServiceAreaService;

    @Override
    public void saveInfo(AdminServiceAreaSaveBo saveBo) {
        ServiceArea entity = new ServiceArea();
        BeanUtils.copyProperties(saveBo, entity);
        iServiceAreaService.save(entity);
    }

    @Override
    public IPage<ServiceArea> pageInfo(AdminServiceAreaPageBo pageBo) {
        Page<ServiceArea> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ServiceArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getProvince()), ServiceArea::getProvince, pageBo.getProvince())
                .like(StringUtils.isNotBlank(pageBo.getCity()), ServiceArea::getCity, pageBo.getCity())
                .like(StringUtils.isNotBlank(pageBo.getDistrict()), ServiceArea::getDistrict, pageBo.getDistrict())
                .eq(Objects.nonNull(pageBo.getState()), ServiceArea::getState, pageBo.getState())
                .orderByDesc(ServiceArea::getCreateTime);
        return iServiceAreaService.page(initPage, wrapper);
    }

    @Override
    public ServiceArea info(AdminServiceAreaInfoBo infoBo) {
        ServiceArea entity = iServiceAreaService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("服务区域不存在");
        }
        return entity;
    }

    @Override
    public void updateInfo(AdminServiceAreaUpdateBo updateBo) {
        ServiceArea entity = new ServiceArea();
        BeanUtils.copyProperties(updateBo, entity);
        iServiceAreaService.updateById(entity);
    }

    @Override
    public void delete(AdminServiceAreaInfoBo infoBo) {
        iServiceAreaService.removeById(infoBo.getId());
    }
}
