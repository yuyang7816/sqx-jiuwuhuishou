package com.sqx.app.servicechat.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.servicechat.db.mapper.CustomerServiceShortcutMapper;
import com.sqx.app.servicechat.db.service.ICustomerServiceShortcutService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;
import org.springframework.stereotype.Service;

@Service
public class ICustomerServiceShortcutServiceImpl
        extends ServiceImpl<CustomerServiceShortcutMapper, CustomerServiceShortcut>
        implements ICustomerServiceShortcutService {
}
