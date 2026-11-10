package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminHelpCategoryManageService;
import com.sqx.app.help.db.service.IHelpCategoryService;
import com.sqx.app.help.domain.entity.HelpCategory;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminHelpCategoryManageServiceImpl implements AdminHelpCategoryManageService {

    @Resource
    private IHelpCategoryService iHelpCategoryService;

    @Override
    public void saveInfo(AdminHelpCategorySaveBo saveBo) {
        HelpCategory entity = new HelpCategory();
        BeanUtils.copyProperties(saveBo, entity);
        iHelpCategoryService.save(entity);
    }

    @Override
    public IPage<HelpCategory> pageInfo(AdminHelpCategoryPageBo pageBo) {
        Page<HelpCategory> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<HelpCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getName()), HelpCategory::getName, pageBo.getName())
                .eq(Objects.nonNull(pageBo.getClientType()), HelpCategory::getClientType, pageBo.getClientType())
                .orderByAsc(HelpCategory::getSort)
                .orderByDesc(HelpCategory::getCreateTime);
        return iHelpCategoryService.page(initPage, wrapper);
    }

    @Override
    public HelpCategory info(AdminHelpCategoryInfoBo infoBo) {
        HelpCategory entity = iHelpCategoryService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("帮助分类不存在");
        }
        return entity;
    }

    @Override
    public void updateInfo(AdminHelpCategoryUpdateBo updateBo) {
        HelpCategory entity = new HelpCategory();
        BeanUtils.copyProperties(updateBo, entity);
        iHelpCategoryService.updateById(entity);
    }

    @Override
    public void delete(AdminHelpCategoryInfoBo infoBo) {
        iHelpCategoryService.removeById(infoBo.getId());
    }
}
