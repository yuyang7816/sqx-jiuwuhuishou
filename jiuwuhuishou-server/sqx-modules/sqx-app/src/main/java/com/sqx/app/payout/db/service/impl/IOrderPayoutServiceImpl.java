package com.sqx.app.payout.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.payout.db.mapper.OrderPayoutMapper;
import com.sqx.app.payout.db.service.IOrderPayoutService;
import com.sqx.app.payout.domain.entity.OrderPayout;
import org.springframework.stereotype.Service;

@Service
public class IOrderPayoutServiceImpl extends ServiceImpl<OrderPayoutMapper, OrderPayout> implements IOrderPayoutService {
}
