package com.sqx.app.welfare.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.welfare.db.mapper.UserSignRecordMapper;
import com.sqx.app.welfare.db.service.IUserSignRecordService;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import org.springframework.stereotype.Service;

@Service
public class IUserSignRecordServiceImpl extends ServiceImpl<UserSignRecordMapper, UserSignRecord> implements IUserSignRecordService {
}
