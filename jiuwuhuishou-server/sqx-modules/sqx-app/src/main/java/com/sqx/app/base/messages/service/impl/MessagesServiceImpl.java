package com.sqx.app.base.messages.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.messages.domain.vo.MessagesVo;
import com.sqx.app.base.messages.db.service.IMessagesService;
import com.sqx.app.base.messages.domain.bo.MessagesInfoBo;
import com.sqx.app.base.messages.domain.bo.MessagesPageBo;
import com.sqx.app.base.messages.domain.bo.MessagesSaveBo;
import com.sqx.app.base.messages.domain.bo.MessagesUpdateBo;
import com.sqx.app.base.messages.domain.entity.Messages;
import com.sqx.app.base.messages.service.MessagesService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 消息信息表 服务实现类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-15 16:28:04
 */
@Service
public class MessagesServiceImpl implements MessagesService {

    @Resource
    private IMessagesService iMessagesService;

    @Override
    public void saveInfo(MessagesSaveBo messagesSaveBo) {
        Messages messages = new Messages();
        BeanUtils.copyProperties(messagesSaveBo, messages);

        iMessagesService.save(messages);
    }

    @Override
    public IPage<MessagesVo> pageInfo(MessagesPageBo messagesPageBo) {
        Page<Messages> initPage = PageUtil.initPage(messagesPageBo.getPage(), messagesPageBo.getLimit());

        IPage<MessagesVo> voIPage = iMessagesService.pageInfo(initPage, messagesPageBo);
        return voIPage;
    }

    @Override
    public MessagesVo info(MessagesInfoBo messagesInfoBo) {
        Messages messages = iMessagesService.getById(messagesInfoBo.getId());
        if (Objects.isNull(messages)) {
            throw new BizException("信息不存在");
        }

        MessagesVo vo = new MessagesVo();
        BeanUtils.copyProperties(messages, vo);
        return vo;
    }

    @Override
    public void updateInfo(MessagesUpdateBo messagesUpdateBo) {
        Messages messages = new Messages();
        BeanUtils.copyProperties(messagesUpdateBo, messages);

        iMessagesService.updateById(messages);
    }

    @Override
    public void delete(MessagesInfoBo messagesInfoBo) {
        iMessagesService.removeById(messagesInfoBo.getId());
    }
}
