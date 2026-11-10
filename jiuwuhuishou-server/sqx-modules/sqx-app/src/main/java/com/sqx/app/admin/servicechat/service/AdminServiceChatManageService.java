package com.sqx.app.admin.servicechat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceMessageVo;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceSessionVo;

public interface AdminServiceChatManageService {

    IPage<AdminServiceSessionVo> pageSession(AdminServiceSessionPageBo pageBo);

    AdminServiceSessionVo sessionInfo(AdminServiceSessionInfoBo infoBo);

    void closeSession(AdminServiceSessionCloseBo closeBo);

    IPage<AdminServiceMessageVo> pageMessage(AdminServiceMessagePageBo pageBo);

    AdminServiceMessageVo saveMessage(AdminServiceMessageSaveBo saveBo);
}
