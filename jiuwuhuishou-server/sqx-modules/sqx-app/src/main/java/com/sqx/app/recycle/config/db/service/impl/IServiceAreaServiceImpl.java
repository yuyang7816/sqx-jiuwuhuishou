package com.sqx.app.recycle.config.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.config.db.mapper.ServiceAreaMapper;
import com.sqx.app.recycle.config.db.service.IServiceAreaService;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;
import org.springframework.stereotype.Service;

/**
 * 服务区域 DB 服务实现
 */
@Service
public class IServiceAreaServiceImpl extends ServiceImpl<ServiceAreaMapper, ServiceArea> implements IServiceAreaService {
}
