package com.sqx.app.recycle.clothes.service;

import com.sqx.app.recycle.clothes.domain.vo.ClothesCategoryVo;
import com.sqx.app.recycle.clothes.domain.vo.ClothesEstimateConfigVo;

import java.util.List;

/**
 * 旧衣回收业务服务
 */
public interface ClothesRecycleService {

    List<ClothesCategoryVo> listCategories();

    ClothesEstimateConfigVo getEstimateConfig();
}
