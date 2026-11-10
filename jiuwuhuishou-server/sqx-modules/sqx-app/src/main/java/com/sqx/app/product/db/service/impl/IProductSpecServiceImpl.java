package com.sqx.app.product.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.product.db.mapper.ProductSpecMapper;
import com.sqx.app.product.db.service.IProductSpecService;
import com.sqx.app.product.domain.entity.ProductSpec;
import org.springframework.stereotype.Service;

@Service
public class IProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements IProductSpecService {
}
