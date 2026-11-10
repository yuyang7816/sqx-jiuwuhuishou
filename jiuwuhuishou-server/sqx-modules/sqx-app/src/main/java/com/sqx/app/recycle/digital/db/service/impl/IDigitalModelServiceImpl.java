package com.sqx.app.recycle.digital.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.digital.db.mapper.DigitalModelMapper;
import com.sqx.app.recycle.digital.db.service.IDigitalModelService;
import com.sqx.app.recycle.digital.domain.entity.DigitalModel;
import org.springframework.stereotype.Service;

/**
 * 数码型号 DB 服务实现
 */
@Service
public class IDigitalModelServiceImpl extends ServiceImpl<DigitalModelMapper, DigitalModel> implements IDigitalModelService {
}
