package com.sqx.app.recycle.appliance.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.appliance.db.mapper.ApplianceModelMapper;
import com.sqx.app.recycle.appliance.db.service.IApplianceModelService;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceModel;
import org.springframework.stereotype.Service;

/**
 * 家电型号 DB 服务实现
 */
@Service
public class IApplianceModelServiceImpl extends ServiceImpl<ApplianceModelMapper, ApplianceModel> implements IApplianceModelService {
}
