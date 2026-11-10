package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminHelpArticleManageService;
import com.sqx.app.help.db.service.IHelpArticleService;
import com.sqx.app.help.domain.entity.HelpArticle;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminHelpArticleManageServiceImpl implements AdminHelpArticleManageService {

    @Resource
    private IHelpArticleService iHelpArticleService;

    @Override
    public void saveInfo(AdminHelpArticleSaveBo saveBo) {
        HelpArticle entity = new HelpArticle();
        BeanUtils.copyProperties(saveBo, entity);
        iHelpArticleService.save(entity);
    }

    @Override
    public IPage<HelpArticle> pageInfo(AdminHelpArticlePageBo pageBo) {
        Page<HelpArticle> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<HelpArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(pageBo.getCategoryId()), HelpArticle::getCategoryId, pageBo.getCategoryId())
                .like(StringUtils.isNotBlank(pageBo.getTitle()), HelpArticle::getTitle, pageBo.getTitle())
                .orderByAsc(HelpArticle::getSort)
                .orderByDesc(HelpArticle::getUpdateTime);
        return iHelpArticleService.page(initPage, wrapper);
    }

    @Override
    public HelpArticle info(AdminHelpArticleInfoBo infoBo) {
        HelpArticle entity = iHelpArticleService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("帮助文章不存在");
        }
        return entity;
    }

    @Override
    public void updateInfo(AdminHelpArticleUpdateBo updateBo) {
        HelpArticle entity = new HelpArticle();
        BeanUtils.copyProperties(updateBo, entity);
        iHelpArticleService.updateById(entity);
    }

    @Override
    public void delete(AdminHelpArticleInfoBo infoBo) {
        iHelpArticleService.removeById(infoBo.getId());
    }
}
