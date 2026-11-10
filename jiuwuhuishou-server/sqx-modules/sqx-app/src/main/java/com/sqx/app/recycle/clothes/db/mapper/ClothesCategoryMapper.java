package com.sqx.app.recycle.clothes.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 旧衣回收分类 Mapper
 */
@Mapper
public interface ClothesCategoryMapper extends BaseMapper<ClothesCategory> {
}
