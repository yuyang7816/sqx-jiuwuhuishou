package com.sqx.app.recycle.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.db.service.IRecycleRuleService;
import com.sqx.app.recycle.config.db.service.IServiceAreaService;
import com.sqx.app.recycle.config.domain.bo.ServiceAreaCheckBo;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;
import com.sqx.app.recycle.config.domain.vo.RecycleCategoryVo;
import com.sqx.app.recycle.config.domain.vo.RecycleRuleVo;
import com.sqx.app.recycle.config.domain.vo.ServiceAreaCheckVo;
import com.sqx.app.recycle.config.service.RecycleConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回收配置业务服务实现
 */
@Service
public class RecycleConfigServiceImpl implements RecycleConfigService {

    private static final Integer STATE_ENABLED = 1;

    @Resource
    private IRecycleCategoryService iRecycleCategoryService;

    @Resource
    private IRecycleRuleService iRecycleRuleService;

    @Resource
    private IServiceAreaService iServiceAreaService;

    @Override
    public List<RecycleCategoryVo> listCategories() {
        List<RecycleCategory> categoryList = iRecycleCategoryService.list(new LambdaQueryWrapper<RecycleCategory>()
                .eq(RecycleCategory::getState, STATE_ENABLED)
                .orderByAsc(RecycleCategory::getSort)
                .orderByDesc(RecycleCategory::getCreateTime));
        if (CollectionUtils.isEmpty(categoryList)) {
            return Collections.emptyList();
        }
        return categoryList.stream().map(category -> {
            RecycleCategoryVo vo = new RecycleCategoryVo();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecycleRuleVo> listRules() {
        List<RecycleRule> ruleList = iRecycleRuleService.list(new LambdaQueryWrapper<RecycleRule>()
                .eq(RecycleRule::getState, STATE_ENABLED)
                .orderByAsc(RecycleRule::getSort)
                .orderByDesc(RecycleRule::getCreateTime));
        if (CollectionUtils.isEmpty(ruleList)) {
            return Collections.emptyList();
        }
        return ruleList.stream().map(rule -> {
            RecycleRuleVo vo = new RecycleRuleVo();
            BeanUtils.copyProperties(rule, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public ServiceAreaCheckVo checkServiceArea(ServiceAreaCheckBo checkBo) {
        LambdaQueryWrapper<ServiceArea> queryWrapper = new LambdaQueryWrapper<ServiceArea>()
                .eq(ServiceArea::getState, STATE_ENABLED)
                .eq(ServiceArea::getProvince, checkBo.getProvince())
                .eq(ServiceArea::getCity, checkBo.getCity());
        if (StringUtils.isNotBlank(checkBo.getDistrict())) {
            queryWrapper.and(wrapper -> wrapper.eq(ServiceArea::getDistrict, checkBo.getDistrict())
                    .or()
                    .isNull(ServiceArea::getDistrict)
                    .or()
                    .eq(ServiceArea::getDistrict, ""));
        }
        long count = iServiceAreaService.count(queryWrapper);
        ServiceAreaCheckVo vo = new ServiceAreaCheckVo();
        vo.setInService(count > 0);
        return vo;
    }
}
