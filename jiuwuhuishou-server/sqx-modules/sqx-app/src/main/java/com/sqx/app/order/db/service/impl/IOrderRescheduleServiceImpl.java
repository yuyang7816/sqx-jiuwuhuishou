package com.sqx.app.order.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.order.db.mapper.OrderRescheduleMapper;
import com.sqx.app.order.db.service.IOrderRescheduleService;
import com.sqx.app.order.domain.entity.OrderReschedule;
import org.springframework.stereotype.Service;

/**
 * 订单改约记录 DB 服务实现
 */
@Service
public class IOrderRescheduleServiceImpl extends ServiceImpl<OrderRescheduleMapper, OrderReschedule> implements IOrderRescheduleService {
}
