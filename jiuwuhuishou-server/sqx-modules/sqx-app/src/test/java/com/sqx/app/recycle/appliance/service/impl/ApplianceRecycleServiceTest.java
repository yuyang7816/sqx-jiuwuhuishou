package com.sqx.app.recycle.appliance.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductCategory;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.domain.entity.ProductSpec;
import com.sqx.app.product.support.ProductRecycleSupport;
import com.sqx.app.recycle.appliance.db.service.IApplianceHeroFlowService;
import com.sqx.app.recycle.appliance.domain.bo.ApplianceEstimateBo;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryOverviewVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceEstimateVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceHeroFlowVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceModelVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecPageVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ApplianceRecycleServiceTest {

    @Mock
    private ProductRecycleSupport productRecycleSupport;
    @Mock
    private IApplianceHeroFlowService iApplianceHeroFlowService;

    @InjectMocks
    private ApplianceRecycleServiceImpl applianceRecycleService;

    @BeforeAll
    static void init() {
        MybatisPlusLambdaInit.init(ApplianceHeroFlow.class);
    }

    @Test
    void estimateShouldUseMinMaxWhenNoProductSpecHit() {
        ProductModel model = model(1L, "测试冰箱", "680.00");
        ProductCategory category = category(2L, "冰箱");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);
        when(productRecycleSupport.applySpecValuation(any(BigDecimal[].class), eq(1L), any())).thenReturn(false);

        ApplianceEstimateBo estimateBo = new ApplianceEstimateBo();
        estimateBo.setModelId(1L);
        estimateBo.setCondition("good");
        estimateBo.setDamage("无明显损伤");
        estimateBo.setAge("1-3年");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("doorType", "十字对开门");
        params.put("capacity", "400L以上");
        estimateBo.setParams(params);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            ApplianceEstimateVo result = applianceRecycleService.estimate(estimateBo);
            // 无 product_spec 命中时，不再用品类加减规则，区间即型号最低/最高估值
            assertEquals(new BigDecimal("680.00"), result.getEstimatePrice());
            assertEquals(new BigDecimal("680.00"), result.getLowPrice());
            assertEquals(new BigDecimal("680.00"), result.getHighPrice());
            assertEquals(Integer.valueOf(0), result.getReward());
        }
    }

    @Test
    void estimateShouldBuildRangeFromMinMaxPlusSpecAdjust() {
        ProductModel model = model(1L, "区间机", "500.00");
        model.setMinPrice(new BigDecimal("400.00"));
        model.setMaxPrice(new BigDecimal("600.00"));
        ProductCategory category = category(2L, "冰箱");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);
        when(productRecycleSupport.applySpecValuation(any(BigDecimal[].class), anyLong(), any())).thenAnswer(invocation -> {
            BigDecimal[] holder = invocation.getArgument(0);
            BigDecimal current = holder[0] == null ? BigDecimal.ZERO : holder[0];
            holder[0] = current.add(new BigDecimal("50.00"));
            return true;
        });

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            ApplianceEstimateVo result = applianceRecycleService.estimate(1L, "fair");
            assertEquals(new BigDecimal("450.00"), result.getLowPrice());
            assertEquals(new BigDecimal("650.00"), result.getHighPrice());
            assertEquals(new BigDecimal("550.00"), result.getEstimatePrice());
        }
    }

    @Test
    void estimateShouldRejectDisabledModel() {
        when(productRecycleSupport.requireEnabledModel(1L)).thenThrow(new BizException("型号不存在或已下架"));
        ApplianceEstimateBo estimateBo = new ApplianceEstimateBo();
        estimateBo.setModelId(1L);
        assertThrows(BizException.class, () -> applianceRecycleService.estimate(estimateBo));
    }

    @Test
    void estimateShouldPreferProductSpecValuationAndDelegateSimpleApi() {
        ProductModel model = model(1L, "冰箱A", "680.00");
        ProductCategory category = category(2L, "冰箱");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);
        when(productRecycleSupport.applySpecValuation(any(BigDecimal[].class), anyLong(), any())).thenAnswer(invocation -> {
            BigDecimal[] holder = invocation.getArgument(0);
            BigDecimal current = holder[0] == null ? BigDecimal.ZERO : holder[0];
            holder[0] = current.add(new BigDecimal("220.00"));
            return true;
        });

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            ApplianceEstimateVo result = applianceRecycleService.estimate(1L, "fair");
            assertEquals(new BigDecimal("900.00"), result.getEstimatePrice());
            assertEquals(new BigDecimal("900.00"), result.getLowPrice());
            assertEquals(new BigDecimal("900.00"), result.getHighPrice());
        }
    }

    @Test
    void estimateShouldClampToMinPriceAndMapConditions() {
        ProductModel model = model(1L, "低价机", "10.00");
        ProductCategory category = category(2L, "冰箱");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);
        when(productRecycleSupport.applySpecValuation(any(), anyLong(), any())).thenReturn(false);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);

            ApplianceEstimateBo poor = new ApplianceEstimateBo();
            poor.setModelId(1L);
            poor.setCondition("poor");
            assertTrue(applianceRecycleService.estimate(poor).getEstimatePrice().compareTo(new BigDecimal("60")) >= 0);

            ApplianceEstimateBo custom = new ApplianceEstimateBo();
            custom.setModelId(1L);
            custom.setCondition("九成新");
            assertNotNull(applianceRecycleService.estimate(custom).getEstimatePrice());

            ApplianceEstimateBo fromParams = new ApplianceEstimateBo();
            fromParams.setModelId(1L);
            Map<String, String> params = new LinkedHashMap<>();
            params.put("condition", "成色一般");
            fromParams.setParams(params);
            assertNotNull(applianceRecycleService.estimate(fromParams).getEstimatePrice());
        }
    }

    @Test
    void listApisShouldCoverEmptyDbAndDefaults() {
        when(productRecycleSupport.listEnabledCategories("appliance")).thenReturn(Collections.emptyList());
        assertTrue(applianceRecycleService.listCategories().isEmpty());
        assertTrue(applianceRecycleService.listCategoryOverview().isEmpty());

        when(iApplianceHeroFlowService.list(any(Wrapper.class))).thenReturn(Collections.emptyList());
        List<ApplianceHeroFlowVo> defaults = applianceRecycleService.listHeroFlows();
        assertEquals(4, defaults.size());

        ApplianceHeroFlow flow = new ApplianceHeroFlow();
        flow.setName("步骤");
        flow.setIcon("i");
        flow.setSort(1);
        when(iApplianceHeroFlowService.list(any(Wrapper.class))).thenReturn(Collections.singletonList(flow));
        assertEquals("步骤", applianceRecycleService.listHeroFlows().get(0).getName());

        when(productRecycleSupport.listEnabledModelsByCategoryId(9L)).thenReturn(Collections.emptyList());
        assertTrue(applianceRecycleService.listModels(9L).isEmpty());
    }

    @Test
    void listCategoryOverviewAndSpecPageShouldBuildRangesAndBrands() {
        ProductCategory category = category(2L, "冰箱");
        category.setImage("icon.png");
        ProductModel m1 = model(1L, "型号A", "100");
        m1.setSpecJson("{\"brand\":\"x\",\"door\":\"双门\"}");
        ProductModel m2 = model(2L, "型号B", null);
        ProductModel m3 = model(3L, "型号C", "200");
        when(productRecycleSupport.listEnabledCategories("appliance")).thenReturn(Collections.singletonList(category));
        when(productRecycleSupport.listEnabledModelsByCategoryId(2L)).thenReturn(Arrays.asList(m1, m2, m3));
        when(productRecycleSupport.requireEnabledCategory(2L)).thenReturn(category);

        List<ApplianceCategoryOverviewVo> overview = applianceRecycleService.listCategoryOverview();
        assertEquals(1, overview.size());
        assertTrue(overview.get(0).getPrice().contains("100"));

        ProductBrand brand = new ProductBrand();
        brand.setId(7L);
        brand.setName("海尔");
        when(productRecycleSupport.listEnabledBrandsByCategoryIds(anyList())).thenReturn(Collections.singletonList(brand));
        when(productRecycleSupport.listEnabledModelsByBrandId(7L, null)).thenReturn(Collections.singletonList(m1));
        ProductSpec spec = new ProductSpec();
        spec.setAttributeName("门型");
        spec.setMultiSelect(1);
        ProductSpec damage = new ProductSpec();
        damage.setAttributeName("damage");
        ProductSpec blank = new ProductSpec();
        blank.setAttributeName(" ");
        when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Arrays.asList(null, blank, damage, spec));
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("门型"))).thenReturn(Arrays.asList("双门", "三门"));
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("damage"))).thenReturn(Collections.singletonList("轻微划痕"));
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("condition"))).thenReturn(Collections.singletonList("良好"));
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("age"))).thenReturn(Collections.singletonList("1年内"));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            ApplianceSpecPageVo page = applianceRecycleService.getSpecPage("冰箱");
            assertEquals("冰箱", page.getConfig().getCategoryName());
            assertFalse(page.getConfig().getBrands().isEmpty());
            assertEquals(Integer.valueOf(1),
                    page.getConfig().getBrands().get(0).getModels().get(0).getFields().get(0).getMultiSelect());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("appliance_spec_冰箱")))
                    .thenReturn("{\"title\":\"自定义\",\"subtitle\":\"s\",\"icon\":\"i\",\"cardLabel\":\"c\",\"reward\":1,\"fields\":[{\"key\":\"doorType\",\"label\":\"门型\",\"options\":[\"双门\"]}]}");
            assertEquals("自定义", applianceRecycleService.getSpecPage("冰箱").getConfig().getTitle());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenThrow(new RuntimeException("x"));
            assertNotNull(applianceRecycleService.getSpecPage(null).getConfig());
        }
    }

    @Test
    void listCategoryOverviewShouldHandleNoModelsAndInvalidSpecJson() {
        ProductCategory category = category(2L, "空调");
        when(productRecycleSupport.listEnabledCategories("appliance")).thenReturn(Collections.singletonList(category));
        when(productRecycleSupport.listEnabledModelsByCategoryId(2L)).thenReturn(Collections.emptyList());
        ApplianceCategoryOverviewVo vo = applianceRecycleService.listCategoryOverview().get(0);
        assertEquals("空调回收", vo.getPriceTitle());
        assertEquals("￥0", vo.getPrice());

        ProductModel onlyNullPrice = model(1L, "X", null);
        onlyNullPrice.setSpecJson("{bad");
        when(productRecycleSupport.listEnabledModelsByCategoryId(2L)).thenReturn(Collections.singletonList(onlyNullPrice));
        ApplianceCategoryOverviewVo vo2 = applianceRecycleService.listCategoryOverview().get(0);
        assertEquals("￥0", vo2.getPrice());
        List<ApplianceModelVo> models = applianceRecycleService.listModels(2L);
        assertEquals(1, models.size());
    }

    @Test
    void estimateAndSpecShouldCoverNullConditionSamePriceAndBlankSpecJson() {
        ProductModel model = model(1L, "同价机", "150");
        ProductCategory category = category(2L, "冰箱");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);
        when(productRecycleSupport.applySpecValuation(any(), anyLong(), any())).thenReturn(false);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            ApplianceEstimateBo noCond = new ApplianceEstimateBo();
            noCond.setModelId(1L);
            assertNull(applianceRecycleService.estimate(noCond).getCondition());
        }

        when(productRecycleSupport.listEnabledCategories("appliance")).thenReturn(Collections.singletonList(category));
        ProductModel same1 = model(1L, "A", "150");
        same1.setSpecJson("");
        ProductModel same2 = model(2L, "B", "150");
        same2.setSpecJson("{}");
        when(productRecycleSupport.listEnabledModelsByCategoryId(2L)).thenReturn(Arrays.asList(same1, same2));
        assertEquals("￥150", applianceRecycleService.listCategoryOverview().get(0).getPrice());

        when(productRecycleSupport.requireEnabledCategory(2L)).thenReturn(category);
        ProductBrand brand = new ProductBrand();
        brand.setId(7L);
        brand.setName("B");
        when(productRecycleSupport.listEnabledBrandsByCategoryIds(anyList())).thenReturn(Collections.singletonList(brand));
        when(productRecycleSupport.listEnabledModelsByBrandId(7L, null)).thenReturn(Collections.singletonList(same1));
        ProductSpec emptyOpts = new ProductSpec();
        emptyOpts.setAttributeName("自定义空");
        ProductSpec nullFieldFriend = new ProductSpec();
        nullFieldFriend.setAttributeName("未知字段");
        when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Arrays.asList(emptyOpts, nullFieldFriend));
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("自定义空"))).thenReturn(Collections.emptyList());
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("未知字段"))).thenReturn(Collections.singletonList("V"));
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("damage"))).thenReturn(Collections.emptyList());
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("condition"))).thenReturn(Collections.emptyList());
        when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("age"))).thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("appliance_spec_冰箱")))
                    .thenReturn("{\"title\":\"T\",\"fields\":[null,{\"key\":\"doorType\",\"label\":\"门型\"}]}");
            assertFalse(applianceRecycleService.getSpecPage("冰箱").getConfig().getBrands().isEmpty());

            same1.setSpecJson("{bad-json");
            ProductSpec first = new ProductSpec();
            first.setAttributeName("DoorTypeDup");
            ProductSpec dup = new ProductSpec();
            dup.setAttributeName("doortypedup"); // 归一化后与上一字段重复，触发 boundNormalizedKeys 分支 continue
            when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Arrays.asList(first, dup));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), anyString()))
                    .thenReturn(Collections.singletonList("x"));
            assertFalse(applianceRecycleService.getSpecPage("冰箱").getConfig().getBrands().isEmpty());

            when(productRecycleSupport.listEnabledBrandsByCategoryIds(anyList())).thenReturn(Collections.emptyList());
            assertTrue(applianceRecycleService.getSpecPage("冰箱").getConfig().getBrands().isEmpty());
        }
    }

    @Test
    void getSpecPageShouldRejectEmptyCategoriesAndSkipBrandsWithoutModels() {
        when(productRecycleSupport.listEnabledCategories("appliance")).thenReturn(Collections.emptyList());
        assertThrows(BizException.class, () -> applianceRecycleService.getSpecPage("冰箱"));

        ProductCategory category = category(2L, "冰箱");
        when(productRecycleSupport.listEnabledCategories("appliance")).thenReturn(Collections.singletonList(category));
        when(productRecycleSupport.requireEnabledCategory(2L)).thenReturn(category);
        ProductBrand brand = new ProductBrand();
        brand.setId(1L);
        brand.setName("空品牌");
        when(productRecycleSupport.listEnabledBrandsByCategoryIds(anyList())).thenReturn(Collections.singletonList(brand));
        when(productRecycleSupport.listEnabledModelsByBrandId(1L, null)).thenReturn(Collections.emptyList());
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            assertTrue(applianceRecycleService.getSpecPage("不存在").getConfig().getBrands().isEmpty());
        }
    }

    private ProductModel model(Long id, String name, String price) {
        ProductModel model = new ProductModel();
        model.setId(id);
        model.setName(name);
        BigDecimal amount = price == null ? null : new BigDecimal(price);
        model.setBasePrice(amount);
        model.setMinPrice(amount);
        model.setMaxPrice(amount);
        model.setState(1);
        return model;
    }

    private ProductCategory category(Long id, String name) {
        ProductCategory category = new ProductCategory();
        category.setId(id);
        category.setName(name);
        return category;
    }

    @Test
    void privateHelpersShouldHandleNullPriceAndEmptySpecs() throws Exception {
        java.lang.reflect.Method formatPrice = ApplianceRecycleServiceImpl.class
                .getDeclaredMethod("formatPrice", BigDecimal.class);
        formatPrice.setAccessible(true);
        assertEquals("0", formatPrice.invoke(applianceRecycleService, new Object[]{null}));

        java.lang.reflect.Method toStringSpecMap = ApplianceRecycleServiceImpl.class
                .getDeclaredMethod("toStringSpecMap", com.alibaba.fastjson2.JSONObject.class);
        toStringSpecMap.setAccessible(true);
        assertTrue(((java.util.Map<?, ?>) toStringSpecMap.invoke(applianceRecycleService, new Object[]{null})).isEmpty());
        com.alibaba.fastjson2.JSONObject empty = new com.alibaba.fastjson2.JSONObject();
        assertTrue(((java.util.Map<?, ?>) toStringSpecMap.invoke(applianceRecycleService, empty)).isEmpty());
        empty.put(" ", "v");
        empty.put("k", null);
        empty.put("ok", "1");
        assertEquals(1, ((java.util.Map<?, ?>) toStringSpecMap.invoke(applianceRecycleService, empty)).size());

        java.lang.reflect.Method findTemplateField = ApplianceRecycleServiceImpl.class
                .getDeclaredMethod("findTemplateField", String.class, List.class);
        findTemplateField.setAccessible(true);
        assertNull(findTemplateField.invoke(applianceRecycleService, " ", Collections.emptyList()));
        assertNull(findTemplateField.invoke(applianceRecycleService, "x", null));
    }
}
