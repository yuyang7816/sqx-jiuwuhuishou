package com.sqx.app.brand.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.brand.db.mapper.BrandCooperationMapper;
import com.sqx.app.brand.db.service.IBrandCooperationService;
import com.sqx.app.brand.domain.entity.BrandCooperation;
import org.springframework.stereotype.Service;

@Service
public class IBrandCooperationServiceImpl extends ServiceImpl<BrandCooperationMapper, BrandCooperation>
        implements IBrandCooperationService {
}
