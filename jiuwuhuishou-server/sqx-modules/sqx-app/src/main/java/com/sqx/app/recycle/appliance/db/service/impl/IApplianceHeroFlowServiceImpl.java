package com.sqx.app.recycle.appliance.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.appliance.db.mapper.ApplianceHeroFlowMapper;
import com.sqx.app.recycle.appliance.db.service.IApplianceHeroFlowService;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;
import org.springframework.stereotype.Service;

/**
 * 家电回收流程步骤 DB 服务实现
 */
@Service
public class IApplianceHeroFlowServiceImpl extends ServiceImpl<ApplianceHeroFlowMapper, ApplianceHeroFlow>
        implements IApplianceHeroFlowService {
}
