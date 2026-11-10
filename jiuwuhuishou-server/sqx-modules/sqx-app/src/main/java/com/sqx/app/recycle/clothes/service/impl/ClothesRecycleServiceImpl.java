package com.sqx.app.recycle.clothes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.recycle.clothes.db.service.IClothesCategoryService;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
import com.sqx.app.recycle.clothes.domain.vo.ClothesCategoryVo;
import com.sqx.app.recycle.clothes.domain.vo.ClothesEstimateConfigVo;
import com.sqx.app.recycle.clothes.service.ClothesRecycleService;
import com.sqx.app.recycle.clothes.support.ClothesCategoryDefaults;
import com.sqx.app.recycle.clothes.support.ClothesEstimateDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 旧衣回收业务服务实现
 */
@Service
public class ClothesRecycleServiceImpl implements ClothesRecycleService {

    private static final Integer STATE_ENABLED = 1;

    @Resource
    private IClothesCategoryService iClothesCategoryService;

    @Override
    public List<ClothesCategoryVo> listCategories() {
        List<ClothesCategory> categoryList = iClothesCategoryService.list(new LambdaQueryWrapper<ClothesCategory>()
                .eq(ClothesCategory::getState, STATE_ENABLED)
                .orderByAsc(ClothesCategory::getSort)
                .orderByDesc(ClothesCategory::getCreateTime));
        if (CollectionUtils.isEmpty(categoryList)) {
            return ClothesCategoryDefaults.defaultCategories();
        }
        return categoryList.stream().map(category -> {
            ClothesCategoryVo vo = new ClothesCategoryVo();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public ClothesEstimateConfigVo getEstimateConfig() {
        ClothesEstimateConfigVo vo = new ClothesEstimateConfigVo();
        vo.setWeights(ClothesEstimateDefaults.resolveWeightOptions());
        return vo;
    }
}
