package com.sqx.app.servicechat.service;

import com.sqx.app.servicechat.domain.vo.ServiceQuickQuestionVo;
import com.sqx.app.servicechat.domain.vo.ServiceShortcutVo;

import java.util.List;

public interface ServiceChatConfigService {

    List<ServiceQuickQuestionVo> listWelcomeQuestions();

    List<ServiceShortcutVo> listShortcuts();

    String resolveReplyContent(String question);
}
