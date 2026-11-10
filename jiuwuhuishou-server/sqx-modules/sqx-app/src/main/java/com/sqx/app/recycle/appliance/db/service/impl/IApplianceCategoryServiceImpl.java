package com.sqx.app.recycle.appliance.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.appliance.db.mapper.ApplianceCategoryMapper;
import com.sqx.app.recycle.appliance.db.service.IApplianceCategoryService;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceCategory;
import org.springframework.stereotype.Service;

/**
 * 家电品类 DB 服务实现
 */
@Service
public class IApplianceCategoryServiceImpl extends ServiceImpl<ApplianceCategoryMapper, ApplianceCategory> implements IApplianceCategoryService {
}
