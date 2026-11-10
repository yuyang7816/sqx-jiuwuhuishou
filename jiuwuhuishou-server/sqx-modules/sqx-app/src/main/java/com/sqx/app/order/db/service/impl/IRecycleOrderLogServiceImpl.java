package com.sqx.app.order.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.order.db.mapper.RecycleOrderLogMapper;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import org.springframework.stereotype.Service;

/**
 * 回收订单日志 DB 服务实现
 */
@Service
public class IRecycleOrderLogServiceImpl extends ServiceImpl<RecycleOrderLogMapper, RecycleOrderLog> implements IRecycleOrderLogService {
}
