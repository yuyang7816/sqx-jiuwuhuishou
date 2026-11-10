package com.sqx.app.admin.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.admin.recycle.service.AdminRecycleRuleManageService;
import com.sqx.app.recycle.config.db.service.IRecycleRuleService;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminRecycleRuleManageServiceImpl implements AdminRecycleRuleManageService {

    @Resource
    private IRecycleRuleService iRecycleRuleService;

    @Override
    public void saveInfo(AdminRecycleRuleSaveBo saveBo) {
        RecycleRule entity = new RecycleRule();
        BeanUtils.copyProperties(saveBo, entity);
        iRecycleRuleService.save(entity);
    }

    @Override
    public IPage<RecycleRule> pageInfo(AdminRecycleRulePageBo pageBo) {
        Page<RecycleRule> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<RecycleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getTitle()), RecycleRule::getTitle, pageBo.getTitle())
                .eq(StringUtils.isNotBlank(pageBo.getRuleType()), RecycleRule::getRuleType, pageBo.getRuleType())
                .eq(Objects.nonNull(pageBo.getState()), RecycleRule::getState, pageBo.getState())
                .orderByAsc(RecycleRule::getSort)
                .orderByDesc(RecycleRule::getCreateTime);
        return iRecycleRuleService.page(initPage, wrapper);
    }

    @Override
    public RecycleRule info(AdminRecycleRuleInfoBo infoBo) {
        RecycleRule entity = iRecycleRuleService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("回收规则不存在");
        }
        return entity;
    }

    @Override
    public void updateInfo(AdminRecycleRuleUpdateBo updateBo) {
        RecycleRule entity = new RecycleRule();
        BeanUtils.copyProperties(updateBo, entity);
        iRecycleRuleService.updateById(entity);
    }

    @Override
    public void delete(AdminRecycleRuleInfoBo infoBo) {
        iRecycleRuleService.removeById(infoBo.getId());
    }
}
