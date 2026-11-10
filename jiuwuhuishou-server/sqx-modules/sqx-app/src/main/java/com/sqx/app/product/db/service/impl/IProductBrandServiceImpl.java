package com.sqx.app.product.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.product.db.mapper.ProductBrandMapper;
import com.sqx.app.product.db.service.IProductBrandService;
import com.sqx.app.product.domain.entity.ProductBrand;
import org.springframework.stereotype.Service;

@Service
public class IProductBrandServiceImpl extends ServiceImpl<ProductBrandMapper, ProductBrand> implements IProductBrandService {
}
