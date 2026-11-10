package com.sqx.app.pickup.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.pickup.db.mapper.PickupTaskMapper;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import org.springframework.stereotype.Service;

@Service
public class IPickupTaskServiceImpl extends ServiceImpl<PickupTaskMapper, PickupTask> implements IPickupTaskService {
}
