package com.sqx.app.payout.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.payout.db.mapper.OrderPayoutLogMapper;
import com.sqx.app.payout.db.service.IOrderPayoutLogService;
import com.sqx.app.payout.domain.entity.OrderPayoutLog;
import org.springframework.stereotype.Service;

@Service
public class IOrderPayoutLogServiceImpl extends ServiceImpl<OrderPayoutLogMapper, OrderPayoutLog> implements IOrderPayoutLogService {
}
