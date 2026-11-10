package com.sqx.app.recycle.config.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回收分类 Mapper
 */
@Mapper
public interface RecycleCategoryMapper extends BaseMapper<RecycleCategory> {
}
