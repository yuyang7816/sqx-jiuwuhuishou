package com.sqx.app.order.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.order.db.mapper.RecycleOrderMapper;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import org.springframework.stereotype.Service;

/**
 * 回收订单 DB 服务实现
 */
@Service
public class IRecycleOrderServiceImpl extends ServiceImpl<RecycleOrderMapper, RecycleOrder> implements IRecycleOrderService {
}
