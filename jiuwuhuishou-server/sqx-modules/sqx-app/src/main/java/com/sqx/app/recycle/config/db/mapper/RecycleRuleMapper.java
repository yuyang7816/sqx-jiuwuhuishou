package com.sqx.app.recycle.config.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回收规则 Mapper
 */
@Mapper
public interface RecycleRuleMapper extends BaseMapper<RecycleRule> {
}
