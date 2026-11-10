package com.sqx.app.pickup.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.pickup.db.mapper.PickupTaskLogMapper;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import org.springframework.stereotype.Service;

@Service
public class IPickupTaskLogServiceImpl extends ServiceImpl<PickupTaskLogMapper, PickupTaskLog> implements IPickupTaskLogService {
}
