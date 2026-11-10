package com.sqx.app.recycle.clothes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.recycle.clothes.db.service.IClothesCategoryService;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
import com.sqx.app.recycle.clothes.domain.vo.ClothesCategoryVo;
import com.sqx.app.recycle.clothes.domain.vo.ClothesEstimateConfigVo;
import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;
import com.sqx.app.recycle.clothes.support.ClothesCategoryDefaults;
import com.sqx.app.recycle.clothes.support.ClothesEstimateDefaults;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import com.sqx.testsupport.PrivateCtor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClothesRecycleServiceTest {

    @Mock
    private IClothesCategoryService iClothesCategoryService;

    @InjectMocks
    private ClothesRecycleServiceImpl clothesRecycleService;

    @BeforeAll
    static void init() {
        MybatisPlusLambdaInit.init(ClothesCategory.class);
        PrivateCtor.invoke(ClothesCategoryDefaults.class);
        PrivateCtor.invoke(ClothesEstimateDefaults.class);
    }

    @Test
    void listCategoriesShouldFallbackAndMap() {
        when(iClothesCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertEquals(ClothesCategoryDefaults.defaultCategories().size(), clothesRecycleService.listCategories().size());

        ClothesCategory category = new ClothesCategory();
        category.setId(1L);
        category.setName("外套");
        when(iClothesCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));
        List<ClothesCategoryVo> list = clothesRecycleService.listCategories();
        assertEquals("外套", list.get(0).getName());
    }

    @Test
    void getEstimateConfigShouldResolveWeightsFromConfigAndDefaults() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("clothes_weight_options")))
                    .thenReturn("[{\"title\":\"A\",\"desc\":\"d\",\"estimateAmount\":1.5},{\"title\":\" \",\"estimateAmount\":2},null,{\"title\":\"B\",\"sort\":9}]");
            ClothesEstimateConfigVo vo = clothesRecycleService.getEstimateConfig();
            assertEquals(2, vo.getWeights().size());
            assertEquals("A", vo.getWeights().get(0).getTitle());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("clothes_weight_options"))).thenReturn("[]");
            List<ClothesWeightOptionVo> defaults = clothesRecycleService.getEstimateConfig().getWeights();
            assertEquals(3, defaults.size());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("clothes_weight_options"))).thenReturn(" ");
            assertEquals(3, clothesRecycleService.getEstimateConfig().getWeights().size());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("clothes_weight_options")))
                    .thenThrow(new RuntimeException("x"));
            assertEquals(3, clothesRecycleService.getEstimateConfig().getWeights().size());
        }
    }
}
