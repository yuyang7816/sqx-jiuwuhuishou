package com.sqx.app.product.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.product.db.mapper.ProductCategoryMapper;
import com.sqx.app.product.db.service.IProductCategoryService;
import com.sqx.app.product.domain.entity.ProductCategory;
import org.springframework.stereotype.Service;

@Service
public class IProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements IProductCategoryService {
}
