package com.sqx.app.recycle.config.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.config.db.mapper.RecycleCategoryMapper;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import org.springframework.stereotype.Service;

/**
 * 回收分类 DB 服务实现
 */
@Service
public class IRecycleCategoryServiceImpl extends ServiceImpl<RecycleCategoryMapper, RecycleCategory> implements IRecycleCategoryService {
}
