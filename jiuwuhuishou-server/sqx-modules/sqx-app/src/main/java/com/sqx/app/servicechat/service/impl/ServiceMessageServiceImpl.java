package com.sqx.app.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.utils.SysConfigUtil;
import com.sqx.app.servicechat.db.service.ICustomerServiceMessageService;
import com.sqx.app.servicechat.db.service.ICustomerServiceSessionService;
import com.sqx.app.servicechat.domain.bo.ServiceMessagePageBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageQuickBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageSaveBo;
import com.sqx.app.servicechat.domain.entity.CustomerServiceMessage;
import com.sqx.app.servicechat.domain.entity.CustomerServiceSession;
import com.sqx.app.servicechat.domain.vo.ServiceMessageQuickVo;
import com.sqx.app.servicechat.domain.vo.ServiceMessageVo;
import com.sqx.app.servicechat.service.ServiceChatConfigService;
import com.sqx.app.servicechat.service.ServiceMessageService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ServiceMessageServiceImpl implements ServiceMessageService {

    private static final int SENDER_USER = 1;
    private static final int SENDER_SERVICE = 2;
    private static final int SESSION_ACTIVE = 1;
    private static final String TRANSFER_QUESTION = "转人工";
    private static final String TRANSFER_HINT_CONFIG_KEY = "service_chat_transfer_hint";
    private static final String DEFAULT_TRANSFER_HINT = "正在转接人工，请稍等···";

    @Resource
    private ICustomerServiceSessionService iCustomerServiceSessionService;
    @Resource
    private ICustomerServiceMessageService iCustomerServiceMessageService;
    @Resource
    private ServiceChatConfigService serviceChatConfigService;

    @Override
    public IPage<ServiceMessageVo> page(Long userId, ServiceMessagePageBo pageBo) {
        CustomerServiceSession session = getSession(userId);
        Page<CustomerServiceMessage> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        if (Objects.isNull(session)) {
            return initPage.convert(item -> new ServiceMessageVo());
        }

        LambdaQueryWrapper<CustomerServiceMessage> wrapper = new LambdaQueryWrapper<CustomerServiceMessage>()
                .eq(CustomerServiceMessage::getSessionId, session.getId())
                .orderByAsc(CustomerServiceMessage::getCreateTime);

        IPage<CustomerServiceMessage> messagePage = iCustomerServiceMessageService.page(initPage, wrapper);
        return messagePage.convert(this::toVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceMessageVo save(Long userId, ServiceMessageSaveBo saveBo) {
        String messageType = StringUtils.defaultIfBlank(saveBo.getMessageType(), "text").toLowerCase();
        if (!"text".equals(messageType) && !"image".equals(messageType)) {
            throw new BizException("不支持的消息类型");
        }

        CustomerServiceSession session = getOrCreateSession(userId);
        CustomerServiceMessage message = saveMessage(session, userId, SENDER_USER, messageType, saveBo.getContent());
        touchSession(session.getId());

        return toVo(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceMessageQuickVo quickAsk(Long userId, ServiceMessageQuickBo quickBo) {
        String question = StringUtils.trimToEmpty(quickBo.getQuestion());
        if (StringUtils.isBlank(question)) {
            throw new BizException("问题不能为空");
        }

        CustomerServiceSession session = getOrCreateSession(userId);
        CustomerServiceMessage userMessage = saveMessage(session, userId, SENDER_USER, "text", question);
        touchSession(session.getId());

        ServiceMessageQuickVo vo = new ServiceMessageQuickVo();
        vo.setUserMessage(toVo(userMessage));

        if (TRANSFER_QUESTION.equals(question)) {
            vo.setTransferring(true);
            vo.setTransferHint(getTransferHint());
            return vo;
        }

        String replyContent = serviceChatConfigService.resolveReplyContent(question);
        CustomerServiceMessage replyMessage = saveMessage(session, userId, SENDER_SERVICE, "text", replyContent);
        touchSession(session.getId());
        vo.setTransferring(false);
        vo.setReplyMessage(toVo(replyMessage));
        return vo;
    }

    private CustomerServiceMessage saveMessage(CustomerServiceSession session, Long userId,
                                               int senderType, String messageType, String content) {
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(session.getId());
        message.setUserId(userId);
        message.setSenderType(senderType);
        message.setMessageType(messageType);
        message.setContent(content);
        iCustomerServiceMessageService.save(message);
        return message;
    }

    private void touchSession(Long sessionId) {
        CustomerServiceSession updateSession = new CustomerServiceSession();
        updateSession.setId(sessionId);
        updateSession.setLastMessageTime(LocalDateTime.now());
        iCustomerServiceSessionService.updateById(updateSession);
    }

    private String getTransferHint() {
        try {
            String value = SysConfigUtil.getConfigValue(TRANSFER_HINT_CONFIG_KEY);
            return StringUtils.defaultIfBlank(value, DEFAULT_TRANSFER_HINT);
        } catch (Exception ignored) {
            return DEFAULT_TRANSFER_HINT;
        }
    }

    private CustomerServiceSession getSession(Long userId) {
        return iCustomerServiceSessionService.getOne(new LambdaQueryWrapper<CustomerServiceSession>()
                .eq(CustomerServiceSession::getUserId, userId)
                .last("LIMIT 1"));
    }

    private CustomerServiceSession getOrCreateSession(Long userId) {
        CustomerServiceSession session = getSession(userId);
        if (Objects.nonNull(session)) {
            return session;
        }
        CustomerServiceSession newSession = new CustomerServiceSession();
        newSession.setUserId(userId);
        newSession.setStatus(SESSION_ACTIVE);
        iCustomerServiceSessionService.save(newSession);
        return newSession;
    }

    private ServiceMessageVo toVo(CustomerServiceMessage message) {
        ServiceMessageVo vo = new ServiceMessageVo();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }
}
