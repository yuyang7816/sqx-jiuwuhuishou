package com.sqx.app.welfare.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.welfare.db.mapper.UserWelfareMapper;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.domain.entity.UserWelfare;
import org.springframework.stereotype.Service;

@Service
public class IUserWelfareServiceImpl extends ServiceImpl<UserWelfareMapper, UserWelfare> implements IUserWelfareService {
}
