package com.sqx.app.recycle.clothes.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.clothes.db.mapper.ClothesCategoryMapper;
import com.sqx.app.recycle.clothes.db.service.IClothesCategoryService;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
import org.springframework.stereotype.Service;

/**
 * 旧衣回收分类 DB 服务实现
 */
@Service
public class IClothesCategoryServiceImpl extends ServiceImpl<ClothesCategoryMapper, ClothesCategory> implements IClothesCategoryService {
}
