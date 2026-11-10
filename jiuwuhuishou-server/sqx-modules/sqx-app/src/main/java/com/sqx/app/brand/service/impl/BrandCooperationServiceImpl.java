package com.sqx.app.brand.service.impl;

import com.sqx.app.brand.db.service.IBrandCooperationService;
import com.sqx.app.brand.domain.bo.BrandCooperationSaveBo;
import com.sqx.app.brand.domain.entity.BrandCooperation;
import com.sqx.app.brand.service.BrandCooperationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class BrandCooperationServiceImpl implements BrandCooperationService {

    private static final Integer STATUS_PENDING = 1;

    @Resource
    private IBrandCooperationService iBrandCooperationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long userId, BrandCooperationSaveBo saveBo) {
        BrandCooperation entity = new BrandCooperation();
        BeanUtils.copyProperties(saveBo, entity);
        entity.setUserId(userId);
        entity.setStatus(STATUS_PENDING);
        entity.setBrandImage(StringUtils.defaultString(saveBo.getBrandImage()));
        iBrandCooperationService.save(entity);
    }
}
