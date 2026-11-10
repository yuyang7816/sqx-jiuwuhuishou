package com.sqx.app.help.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.help.db.service.IHelpArticleService;
import com.sqx.app.help.db.service.IHelpCategoryService;
import com.sqx.app.help.domain.bo.HelpArticleInfoBo;
import com.sqx.app.help.domain.bo.HelpArticlePageBo;
import com.sqx.app.help.domain.entity.HelpArticle;
import com.sqx.app.help.domain.entity.HelpCategory;
import com.sqx.app.help.domain.vo.HelpArticleDetailVo;
import com.sqx.app.help.domain.vo.HelpArticleVo;
import com.sqx.app.help.service.HelpArticleService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HelpArticleServiceImpl implements HelpArticleService {

    private static final int CLIENT_TYPE_USER = 1;

    @Resource
    private IHelpArticleService iHelpArticleService;
    @Resource
    private IHelpCategoryService iHelpCategoryService;

    @Override
    public IPage<HelpArticleVo> page(HelpArticlePageBo pageBo) {
        List<Long> categoryIds = listUserCategoryIds();
        if (categoryIds.isEmpty()) {
            Page<HelpArticle> emptyPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
            return emptyPage.convert(item -> new HelpArticleVo());
        }

        Page<HelpArticle> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<HelpArticle> wrapper = new LambdaQueryWrapper<HelpArticle>()
                .in(HelpArticle::getCategoryId, categoryIds)
                .orderByAsc(HelpArticle::getSort)
                .orderByDesc(HelpArticle::getUpdateTime);

        IPage<HelpArticle> articlePage = iHelpArticleService.page(initPage, wrapper);
        Map<Long, String> categoryMap = loadCategoryMap(articlePage.getRecords());
        return articlePage.convert(article -> toVo(article, categoryMap));
    }

    @Override
    public HelpArticleDetailVo info(HelpArticleInfoBo infoBo) {
        HelpArticle article = iHelpArticleService.getById(infoBo.getId());
        if (Objects.isNull(article)) {
            throw new BizException("帮助文章不存在");
        }

        List<Long> categoryIds = listUserCategoryIds();
        if (Objects.isNull(article.getCategoryId()) || !categoryIds.contains(article.getCategoryId())) {
            throw new BizException("帮助文章不存在");
        }

        HelpCategory category = iHelpCategoryService.getById(article.getCategoryId());
        HelpArticleDetailVo vo = new HelpArticleDetailVo();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setContent(article.getContent());
        vo.setTag(Objects.nonNull(category) ? category.getName() : "");
        vo.setUpdateTime(article.getUpdateTime());
        return vo;
    }

    private List<Long> listUserCategoryIds() {
        LambdaQueryWrapper<HelpCategory> wrapper = new LambdaQueryWrapper<HelpCategory>()
                .eq(HelpCategory::getClientType, CLIENT_TYPE_USER)
                .orderByAsc(HelpCategory::getSort);
        return iHelpCategoryService.list(wrapper).stream()
                .map(HelpCategory::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, String> loadCategoryMap(List<HelpArticle> articles) {
        Set<Long> categoryIds = articles.stream()
                .map(HelpArticle::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return iHelpCategoryService.listByIds(categoryIds).stream()
                .collect(Collectors.toMap(HelpCategory::getId, HelpCategory::getName, (left, right) -> left));
    }

    private HelpArticleVo toVo(HelpArticle article, Map<Long, String> categoryMap) {
        HelpArticleVo vo = new HelpArticleVo();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(toSummary(article.getContent()));
        vo.setTag(categoryMap.getOrDefault(article.getCategoryId(), ""));
        vo.setUpdateTime(article.getUpdateTime());
        return vo;
    }

    private String toSummary(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        String text = content.replaceAll("<[^>]+>", " ")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (text.length() <= 80) {
            return text;
        }
        return text.substring(0, 80) + "...";
    }
}
