package com.sqx.app.settlement.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.settlement.db.mapper.OrderDisputeMapper;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import org.springframework.stereotype.Service;

@Service
public class IOrderDisputeServiceImpl extends ServiceImpl<OrderDisputeMapper, OrderDispute> implements IOrderDisputeService {
}
