package com.sqx.app.order.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.order.db.mapper.RecycleOrderItemMapper;
import com.sqx.app.order.db.service.IRecycleOrderItemService;
import com.sqx.app.order.domain.entity.RecycleOrderItem;
import org.springframework.stereotype.Service;

/**
 * 回收订单明细 DB 服务实现
 */
@Service
public class IRecycleOrderItemServiceImpl extends ServiceImpl<RecycleOrderItemMapper, RecycleOrderItem> implements IRecycleOrderItemService {
}
