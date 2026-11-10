package com.sqx.app.recycle.digital.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.digital.db.mapper.DigitalBrandMapper;
import com.sqx.app.recycle.digital.db.service.IDigitalBrandService;
import com.sqx.app.recycle.digital.domain.entity.DigitalBrand;
import org.springframework.stereotype.Service;

/**
 * 数码品牌 DB 服务实现
 */
@Service
public class IDigitalBrandServiceImpl extends ServiceImpl<DigitalBrandMapper, DigitalBrand> implements IDigitalBrandService {
}
