package com.sqx.app.recycle.appliance.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.appliance.db.mapper.AppliancePricingRuleMapper;
import com.sqx.app.recycle.appliance.db.service.IAppliancePricingRuleService;
import com.sqx.app.recycle.appliance.domain.entity.AppliancePricingRule;
import org.springframework.stereotype.Service;

/**
 * 家电估价规则 DB 服务实现
 */
@Service
public class IAppliancePricingRuleServiceImpl extends ServiceImpl<AppliancePricingRuleMapper, AppliancePricingRule> implements IAppliancePricingRuleService {
}
