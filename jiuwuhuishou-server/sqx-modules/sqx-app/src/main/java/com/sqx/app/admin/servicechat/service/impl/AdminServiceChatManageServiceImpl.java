package com.sqx.app.admin.servicechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceMessageVo;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceSessionVo;
import com.sqx.app.admin.servicechat.service.AdminServiceChatManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.servicechat.db.service.ICustomerServiceMessageService;
import com.sqx.app.servicechat.db.service.ICustomerServiceSessionService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceMessage;
import com.sqx.app.servicechat.domain.entity.CustomerServiceSession;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceChatManageServiceImpl implements AdminServiceChatManageService {

    private static final int SENDER_USER = 1;
    private static final int SENDER_SERVICE = 2;
    private static final int SESSION_ACTIVE = 1;
    private static final int SESSION_CLOSED = 2;

    @Resource
    private ICustomerServiceSessionService iCustomerServiceSessionService;
    @Resource
    private ICustomerServiceMessageService iCustomerServiceMessageService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public IPage<AdminServiceSessionVo> pageSession(AdminServiceSessionPageBo pageBo) {
        Page<CustomerServiceSession> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<CustomerServiceSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, CustomerServiceSession::getUserId, userIds)
                .eq(Objects.nonNull(pageBo.getStatus()), CustomerServiceSession::getStatus, pageBo.getStatus())
                .orderByDesc(CustomerServiceSession::getLastMessageTime)
                .orderByDesc(CustomerServiceSession::getId);

        IPage<CustomerServiceSession> iPage = iCustomerServiceSessionService.page(initPage, wrapper);
        List<CustomerServiceSession> records = iPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageUtil.copyPage(iPage, Collections.emptyList());
        }

        Map<Long, CustomerServiceMessage> lastMessageMap = loadLastMessages(records);
        List<AdminServiceSessionVo> voList = new ArrayList<>();
        for (CustomerServiceSession session : records) {
            AdminServiceSessionVo vo = toSessionVo(session, lastMessageMap.get(session.getId()));
            if (Boolean.TRUE.equals(pageBo.getPendingReply()) && !Boolean.TRUE.equals(vo.getPendingReply())) {
                continue;
            }
            voList.add(vo);
        }

        // pendingReply 为内存过滤时，保持当前页内过滤结果返回（运营常用筛选）
        if (Boolean.TRUE.equals(pageBo.getPendingReply())) {
            return PageUtil.copyPage(iPage, voList);
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminServiceSessionVo sessionInfo(AdminServiceSessionInfoBo infoBo) {
        CustomerServiceSession session = requireSession(infoBo.getId());
        Map<Long, CustomerServiceMessage> lastMessageMap = loadLastMessages(Collections.singletonList(session));
        return toSessionVo(session, lastMessageMap.get(session.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeSession(AdminServiceSessionCloseBo closeBo) {
        CustomerServiceSession session = requireSession(closeBo.getId());
        CustomerServiceSession update = new CustomerServiceSession();
        update.setId(session.getId());
        update.setStatus(SESSION_CLOSED);
        iCustomerServiceSessionService.updateById(update);
    }

    @Override
    public IPage<AdminServiceMessageVo> pageMessage(AdminServiceMessagePageBo pageBo) {
        requireSession(pageBo.getSessionId());
        Page<CustomerServiceMessage> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<CustomerServiceMessage> wrapper = new LambdaQueryWrapper<CustomerServiceMessage>()
                .eq(CustomerServiceMessage::getSessionId, pageBo.getSessionId())
                .orderByAsc(CustomerServiceMessage::getCreateTime)
                .orderByAsc(CustomerServiceMessage::getId);
        IPage<CustomerServiceMessage> messagePage = iCustomerServiceMessageService.page(initPage, wrapper);
        List<AdminServiceMessageVo> voList = messagePage.getRecords().stream()
                .map(this::toMessageVo)
                .collect(Collectors.toList());
        return PageUtil.copyPage(messagePage, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminServiceMessageVo saveMessage(AdminServiceMessageSaveBo saveBo) {
        CustomerServiceSession session = requireSession(saveBo.getSessionId());
        if (!Objects.equals(session.getStatus(), SESSION_ACTIVE)) {
            // 回复时自动重新打开会话
            CustomerServiceSession reopen = new CustomerServiceSession();
            reopen.setId(session.getId());
            reopen.setStatus(SESSION_ACTIVE);
            iCustomerServiceSessionService.updateById(reopen);
        }

        String messageType = StringUtils.defaultIfBlank(saveBo.getMessageType(), "text").toLowerCase();
        if (!"text".equals(messageType) && !"image".equals(messageType)) {
            throw new BizException("不支持的消息类型");
        }
        String content = StringUtils.trimToEmpty(saveBo.getContent());
        if (StringUtils.isBlank(content)) {
            throw new BizException("消息内容不能为空");
        }

        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(session.getId());
        message.setUserId(session.getUserId());
        message.setSenderType(SENDER_SERVICE);
        message.setMessageType(messageType);
        message.setContent(content);
        iCustomerServiceMessageService.save(message);

        CustomerServiceSession touch = new CustomerServiceSession();
        touch.setId(session.getId());
        touch.setLastMessageTime(LocalDateTime.now());
        iCustomerServiceSessionService.updateById(touch);

        return toMessageVo(message);
    }

    private CustomerServiceSession requireSession(Long sessionId) {
        CustomerServiceSession session = iCustomerServiceSessionService.getById(sessionId);
        if (Objects.isNull(session)) {
            throw new BizException("客服会话不存在");
        }
        return session;
    }

    private Map<Long, CustomerServiceMessage> loadLastMessages(List<CustomerServiceSession> sessions) {
        Map<Long, CustomerServiceMessage> result = new HashMap<>();
        if (CollectionUtils.isEmpty(sessions)) {
            return result;
        }
        Set<Long> sessionIds = sessions.stream().map(CustomerServiceSession::getId).collect(Collectors.toCollection(HashSet::new));
        List<CustomerServiceMessage> messages = iCustomerServiceMessageService.list(
                new LambdaQueryWrapper<CustomerServiceMessage>()
                        .in(CustomerServiceMessage::getSessionId, sessionIds)
                        .orderByDesc(CustomerServiceMessage::getCreateTime)
                        .orderByDesc(CustomerServiceMessage::getId)
        );
        for (CustomerServiceMessage message : messages) {
            result.putIfAbsent(message.getSessionId(), message);
        }
        return result;
    }

    private AdminServiceSessionVo toSessionVo(CustomerServiceSession session, CustomerServiceMessage lastMessage) {
        AdminServiceSessionVo vo = new AdminServiceSessionVo();
        BeanUtils.copyProperties(session, vo);
        adminLookupSupport.fillUserFields(session.getUserId(), vo);
        if (Objects.nonNull(lastMessage)) {
            vo.setLastSenderType(lastMessage.getSenderType());
            vo.setLastMessageType(lastMessage.getMessageType());
            vo.setPendingReply(Objects.equals(lastMessage.getSenderType(), SENDER_USER));
            if ("image".equalsIgnoreCase(lastMessage.getMessageType())) {
                vo.setLastMessagePreview("[图片]");
            } else {
                String content = StringUtils.defaultString(lastMessage.getContent());
                vo.setLastMessagePreview(content.length() > 40 ? content.substring(0, 40) + "…" : content);
            }
        } else {
            vo.setPendingReply(false);
            vo.setLastMessagePreview("");
        }
        return vo;
    }

    private AdminServiceMessageVo toMessageVo(CustomerServiceMessage message) {
        AdminServiceMessageVo vo = new AdminServiceMessageVo();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }
}
