package com.sqx.app.welfare.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.welfare.db.mapper.WelfareTaskMapper;
import com.sqx.app.welfare.db.service.IWelfareTaskService;
import com.sqx.app.welfare.domain.entity.WelfareTask;
import org.springframework.stereotype.Service;

@Service
public class IWelfareTaskServiceImpl extends ServiceImpl<WelfareTaskMapper, WelfareTask> implements IWelfareTaskService {
}
