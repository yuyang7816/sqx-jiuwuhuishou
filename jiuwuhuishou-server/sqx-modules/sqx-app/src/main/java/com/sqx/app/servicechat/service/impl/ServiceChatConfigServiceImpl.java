package com.sqx.app.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.servicechat.db.service.ICustomerServiceQuickQuestionService;
import com.sqx.app.servicechat.db.service.ICustomerServiceShortcutService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;
import com.sqx.app.servicechat.domain.vo.ServiceQuickQuestionVo;
import com.sqx.app.servicechat.domain.vo.ServiceShortcutVo;
import com.sqx.app.servicechat.service.ServiceChatConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ServiceChatConfigServiceImpl implements ServiceChatConfigService {

    private static final String DEFAULT_REPLY = "您好，已收到您的问题，我们会尽快为您解答。如需人工协助，可点击「转人工」。";

    @Resource
    private ICustomerServiceQuickQuestionService iCustomerServiceQuickQuestionService;
    @Resource
    private ICustomerServiceShortcutService iCustomerServiceShortcutService;

    @Override
    public List<ServiceQuickQuestionVo> listWelcomeQuestions() {
        List<CustomerServiceQuickQuestion> list = iCustomerServiceQuickQuestionService.list(
                new LambdaQueryWrapper<CustomerServiceQuickQuestion>()
                        .eq(CustomerServiceQuickQuestion::getShowWelcome, 1)
                        .orderByAsc(CustomerServiceQuickQuestion::getSort)
                        .orderByDesc(CustomerServiceQuickQuestion::getCreateTime)
        );
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toQuestionVo).collect(Collectors.toList());
    }

    @Override
    public List<ServiceShortcutVo> listShortcuts() {
        List<CustomerServiceShortcut> list = iCustomerServiceShortcutService.list(
                new LambdaQueryWrapper<CustomerServiceShortcut>()
                        .orderByAsc(CustomerServiceShortcut::getSort)
                        .orderByDesc(CustomerServiceShortcut::getCreateTime)
        );
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toShortcutVo).collect(Collectors.toList());
    }

    @Override
    public String resolveReplyContent(String question) {
        String text = StringUtils.trimToEmpty(question);
        if (StringUtils.isBlank(text)) {
            return DEFAULT_REPLY;
        }
        CustomerServiceQuickQuestion entity = iCustomerServiceQuickQuestionService.getOne(
                new LambdaQueryWrapper<CustomerServiceQuickQuestion>()
                        .eq(CustomerServiceQuickQuestion::getQuestion, text)
                        .last("LIMIT 1")
        );
        if (Objects.nonNull(entity) && StringUtils.isNotBlank(entity.getContent())) {
            return entity.getContent();
        }
        return DEFAULT_REPLY;
    }

    private ServiceQuickQuestionVo toQuestionVo(CustomerServiceQuickQuestion entity) {
        ServiceQuickQuestionVo vo = new ServiceQuickQuestionVo();
        vo.setId(entity.getId());
        vo.setQuestion(entity.getQuestion());
        vo.setContent(entity.getContent());
        return vo;
    }

    private ServiceShortcutVo toShortcutVo(CustomerServiceShortcut entity) {
        ServiceShortcutVo vo = new ServiceShortcutVo();
        vo.setId(entity.getId());
        vo.setContent(entity.getContent());
        return vo;
    }
}
