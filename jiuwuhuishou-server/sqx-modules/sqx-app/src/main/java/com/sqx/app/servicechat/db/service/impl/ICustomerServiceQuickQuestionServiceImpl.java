package com.sqx.app.servicechat.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.servicechat.db.mapper.CustomerServiceQuickQuestionMapper;
import com.sqx.app.servicechat.db.service.ICustomerServiceQuickQuestionService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import org.springframework.stereotype.Service;

@Service
public class ICustomerServiceQuickQuestionServiceImpl
        extends ServiceImpl<CustomerServiceQuickQuestionMapper, CustomerServiceQuickQuestion>
        implements ICustomerServiceQuickQuestionService {
}
