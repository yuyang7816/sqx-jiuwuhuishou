package com.sqx.app.servicechat.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.servicechat.db.mapper.CustomerServiceMessageMapper;
import com.sqx.app.servicechat.db.service.ICustomerServiceMessageService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceMessage;
import org.springframework.stereotype.Service;

@Service
public class ICustomerServiceMessageServiceImpl extends ServiceImpl<CustomerServiceMessageMapper, CustomerServiceMessage>
        implements ICustomerServiceMessageService {
}
