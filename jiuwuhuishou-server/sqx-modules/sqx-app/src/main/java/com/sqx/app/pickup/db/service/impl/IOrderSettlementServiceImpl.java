package com.sqx.app.pickup.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.pickup.db.mapper.OrderSettlementMapper;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import org.springframework.stereotype.Service;

@Service
public class IOrderSettlementServiceImpl extends ServiceImpl<OrderSettlementMapper, OrderSettlement> implements IOrderSettlementService {
}
