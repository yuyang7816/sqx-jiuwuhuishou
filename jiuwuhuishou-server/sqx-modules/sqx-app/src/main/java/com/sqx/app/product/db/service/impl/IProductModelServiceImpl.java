package com.sqx.app.product.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.product.db.mapper.ProductModelMapper;
import com.sqx.app.product.db.service.IProductModelService;
import com.sqx.app.product.domain.entity.ProductModel;
import org.springframework.stereotype.Service;

@Service
public class IProductModelServiceImpl extends ServiceImpl<ProductModelMapper, ProductModel> implements IProductModelService {
}
