package com.sqx.app.admin.servicechat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;

public interface AdminServiceChatConfigService {

    void saveQuickQuestion(AdminServiceQuickQuestionSaveBo saveBo);

    IPage<CustomerServiceQuickQuestion> pageQuickQuestion(AdminServiceQuickQuestionPageBo pageBo);

    CustomerServiceQuickQuestion infoQuickQuestion(AdminServiceConfigIdBo infoBo);

    void updateQuickQuestion(AdminServiceQuickQuestionUpdateBo updateBo);

    void deleteQuickQuestion(AdminServiceConfigIdBo infoBo);

    void saveShortcut(AdminServiceShortcutSaveBo saveBo);

    IPage<CustomerServiceShortcut> pageShortcut(AdminServiceShortcutPageBo pageBo);

    CustomerServiceShortcut infoShortcut(AdminServiceConfigIdBo infoBo);

    void updateShortcut(AdminServiceShortcutUpdateBo updateBo);

    void deleteShortcut(AdminServiceConfigIdBo infoBo);
}
