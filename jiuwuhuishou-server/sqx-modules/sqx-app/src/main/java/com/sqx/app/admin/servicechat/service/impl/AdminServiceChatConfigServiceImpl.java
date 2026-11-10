package com.sqx.app.admin.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.admin.servicechat.service.AdminServiceChatConfigService;
import com.sqx.app.servicechat.db.service.ICustomerServiceQuickQuestionService;
import com.sqx.app.servicechat.db.service.ICustomerServiceShortcutService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminServiceChatConfigServiceImpl implements AdminServiceChatConfigService {

    @Resource
    private ICustomerServiceQuickQuestionService iCustomerServiceQuickQuestionService;
    @Resource
    private ICustomerServiceShortcutService iCustomerServiceShortcutService;

    @Override
    public void saveQuickQuestion(AdminServiceQuickQuestionSaveBo saveBo) {
        assertQuestionUnique(null, saveBo.getQuestion());
        CustomerServiceQuickQuestion entity = new CustomerServiceQuickQuestion();
        BeanUtils.copyProperties(saveBo, entity);
        fillQuickQuestionDefaults(entity);
        iCustomerServiceQuickQuestionService.save(entity);
    }

    @Override
    public IPage<CustomerServiceQuickQuestion> pageQuickQuestion(AdminServiceQuickQuestionPageBo pageBo) {
        Page<CustomerServiceQuickQuestion> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<CustomerServiceQuickQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getQuestion()), CustomerServiceQuickQuestion::getQuestion, pageBo.getQuestion())
                .orderByAsc(CustomerServiceQuickQuestion::getSort)
                .orderByDesc(CustomerServiceQuickQuestion::getCreateTime);
        return iCustomerServiceQuickQuestionService.page(initPage, wrapper);
    }

    @Override
    public CustomerServiceQuickQuestion infoQuickQuestion(AdminServiceConfigIdBo infoBo) {
        CustomerServiceQuickQuestion entity = iCustomerServiceQuickQuestionService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("快捷问题不存在");
        }
        return entity;
    }

    @Override
    public void updateQuickQuestion(AdminServiceQuickQuestionUpdateBo updateBo) {
        infoQuickQuestion(toIdBo(updateBo.getId()));
        assertQuestionUnique(updateBo.getId(), updateBo.getQuestion());
        CustomerServiceQuickQuestion entity = new CustomerServiceQuickQuestion();
        BeanUtils.copyProperties(updateBo, entity);
        fillQuickQuestionDefaults(entity);
        iCustomerServiceQuickQuestionService.updateById(entity);
    }

    @Override
    public void deleteQuickQuestion(AdminServiceConfigIdBo infoBo) {
        iCustomerServiceQuickQuestionService.removeById(infoBo.getId());
    }

    @Override
    public void saveShortcut(AdminServiceShortcutSaveBo saveBo) {
        assertShortcutUnique(null, saveBo.getContent());
        CustomerServiceShortcut entity = new CustomerServiceShortcut();
        BeanUtils.copyProperties(saveBo, entity);
        if (Objects.isNull(entity.getSort())) {
            entity.setSort(0);
        }
        iCustomerServiceShortcutService.save(entity);
    }

    @Override
    public IPage<CustomerServiceShortcut> pageShortcut(AdminServiceShortcutPageBo pageBo) {
        Page<CustomerServiceShortcut> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<CustomerServiceShortcut> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getContent()), CustomerServiceShortcut::getContent, pageBo.getContent())
                .orderByAsc(CustomerServiceShortcut::getSort)
                .orderByDesc(CustomerServiceShortcut::getCreateTime);
        return iCustomerServiceShortcutService.page(initPage, wrapper);
    }

    @Override
    public CustomerServiceShortcut infoShortcut(AdminServiceConfigIdBo infoBo) {
        CustomerServiceShortcut entity = iCustomerServiceShortcutService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("快捷提示词不存在");
        }
        return entity;
    }

    @Override
    public void updateShortcut(AdminServiceShortcutUpdateBo updateBo) {
        infoShortcut(toIdBo(updateBo.getId()));
        assertShortcutUnique(updateBo.getId(), updateBo.getContent());
        CustomerServiceShortcut entity = new CustomerServiceShortcut();
        BeanUtils.copyProperties(updateBo, entity);
        if (Objects.isNull(entity.getSort())) {
            entity.setSort(0);
        }
        iCustomerServiceShortcutService.updateById(entity);
    }

    @Override
    public void deleteShortcut(AdminServiceConfigIdBo infoBo) {
        iCustomerServiceShortcutService.removeById(infoBo.getId());
    }

    private void fillQuickQuestionDefaults(CustomerServiceQuickQuestion entity) {
        if (Objects.isNull(entity.getSort())) {
            entity.setSort(0);
        }
        if (Objects.isNull(entity.getShowWelcome())) {
            entity.setShowWelcome(1);
        }
    }

    private void assertQuestionUnique(Long excludeId, String question) {
        String text = StringUtils.trimToEmpty(question);
        LambdaQueryWrapper<CustomerServiceQuickQuestion> wrapper = new LambdaQueryWrapper<CustomerServiceQuickQuestion>()
                .eq(CustomerServiceQuickQuestion::getQuestion, text)
                .ne(Objects.nonNull(excludeId), CustomerServiceQuickQuestion::getId, excludeId)
                .last("LIMIT 1");
        if (iCustomerServiceQuickQuestionService.count(wrapper) > 0) {
            throw new BizException("该问题已存在");
        }
    }

    private void assertShortcutUnique(Long excludeId, String content) {
        String text = StringUtils.trimToEmpty(content);
        LambdaQueryWrapper<CustomerServiceShortcut> wrapper = new LambdaQueryWrapper<CustomerServiceShortcut>()
                .eq(CustomerServiceShortcut::getContent, text)
                .ne(Objects.nonNull(excludeId), CustomerServiceShortcut::getId, excludeId)
                .last("LIMIT 1");
        if (iCustomerServiceShortcutService.count(wrapper) > 0) {
            throw new BizException("该提示词已存在");
        }
    }

    private AdminServiceConfigIdBo toIdBo(Long id) {
        AdminServiceConfigIdBo infoBo = new AdminServiceConfigIdBo();
        infoBo.setId(id);
        return infoBo;
    }
}
