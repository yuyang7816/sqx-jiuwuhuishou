package com.sqx.app.recycle.config.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.config.db.mapper.RecycleRuleMapper;
import com.sqx.app.recycle.config.db.service.IRecycleRuleService;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import org.springframework.stereotype.Service;

/**
 * 回收规则 DB 服务实现
 */
@Service
public class IRecycleRuleServiceImpl extends ServiceImpl<RecycleRuleMapper, RecycleRule> implements IRecycleRuleService {
}
