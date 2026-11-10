package com.sqx.app.servicechat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.servicechat.domain.bo.ServiceMessagePageBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageQuickBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageSaveBo;
import com.sqx.app.servicechat.domain.vo.ServiceMessageQuickVo;
import com.sqx.app.servicechat.domain.vo.ServiceMessageVo;

public interface ServiceMessageService {

    IPage<ServiceMessageVo> page(Long userId, ServiceMessagePageBo pageBo);

    ServiceMessageVo save(Long userId, ServiceMessageSaveBo saveBo);

    ServiceMessageQuickVo quickAsk(Long userId, ServiceMessageQuickBo quickBo);
}
