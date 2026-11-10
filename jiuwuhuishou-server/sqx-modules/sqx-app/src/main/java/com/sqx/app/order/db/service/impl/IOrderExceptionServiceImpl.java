package com.sqx.app.order.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.order.db.mapper.OrderExceptionMapper;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.domain.entity.OrderException;
import org.springframework.stereotype.Service;

/**
 * 订单异常反馈 DB 服务实现
 */
@Service
public class IOrderExceptionServiceImpl extends ServiceImpl<OrderExceptionMapper, OrderException> implements IOrderExceptionService {
}
