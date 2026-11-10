package com.sqx.app.recycle.digital.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.digital.db.mapper.DigitalPricingRuleMapper;
import com.sqx.app.recycle.digital.db.service.IDigitalPricingRuleService;
import com.sqx.app.recycle.digital.domain.entity.DigitalPricingRule;
import org.springframework.stereotype.Service;

/**
 * 数码估价规则 DB 服务实现
 */
@Service
public class IDigitalPricingRuleServiceImpl extends ServiceImpl<DigitalPricingRuleMapper, DigitalPricingRule> implements IDigitalPricingRuleService {
}
