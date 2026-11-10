package com.sqx.app.servicechat.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.servicechat.db.mapper.CustomerServiceSessionMapper;
import com.sqx.app.servicechat.db.service.ICustomerServiceSessionService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceSession;
import org.springframework.stereotype.Service;

@Service
public class ICustomerServiceSessionServiceImpl extends ServiceImpl<CustomerServiceSessionMapper, CustomerServiceSession>
        implements ICustomerServiceSessionService {
}
