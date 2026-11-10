package com.sqx.app.recycle.digital.service.impl;

import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.domain.entity.ProductSpec;
import com.sqx.app.product.support.ProductRecycleSupport;
import com.sqx.app.recycle.digital.db.service.IDigitalPricingRuleService;
import com.sqx.app.recycle.digital.domain.bo.DigitalEstimateBo;
import com.sqx.app.recycle.digital.domain.entity.DigitalPricingRule;
import com.sqx.app.recycle.digital.domain.vo.DigitalEstimateVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSearchResultVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSpecPageVo;
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
import java.time.LocalDateTime;
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
class DigitalRecycleServiceTest {

    @Mock
    private ProductRecycleSupport productRecycleSupport;
    @Mock
    private IDigitalPricingRuleService iDigitalPricingRuleService;

    @InjectMocks
    private DigitalRecycleServiceImpl digitalRecycleService;

    @BeforeAll
    static void init() {
        MybatisPlusLambdaInit.init(DigitalPricingRule.class);
    }

    @Test
    void estimateShouldPreferProductSpecValuation() {
        ProductModel model = model(1L, 1L, "iPhone 15 Pro", "6120.00");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.applySpecValuation(any(BigDecimal[].class), eq(1L), any())).thenAnswer(invocation -> {
            BigDecimal[] holder = invocation.getArgument(0);
            holder[0] = new BigDecimal("5630.40");
            return true;
        });

        DigitalEstimateBo estimateBo = new DigitalEstimateBo();
        estimateBo.setModelId(1L);
        estimateBo.setCondition("fair");
        DigitalEstimateVo result = digitalRecycleService.estimate(estimateBo);
        assertEquals(new BigDecimal("5630.40"), result.getEstimatePrice());
    }

    @Test
    void estimateShouldRejectDisabledModel() {
        when(productRecycleSupport.requireEnabledModel(1L)).thenThrow(new BizException("型号不存在或已下架"));
        DigitalEstimateBo estimateBo = new DigitalEstimateBo();
        estimateBo.setModelId(1L);
        assertThrows(BizException.class, () -> digitalRecycleService.estimate(estimateBo));
    }

    @Test
    void estimateShouldUsePricingRuleAndHasModelSpecsPath() {
        ProductModel model = model(1L, 1L, "Phone", "1000.00");
        when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
        when(productRecycleSupport.applySpecValuation(any(), anyLong(), any())).thenReturn(false);
        when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Collections.emptyList());

        DigitalPricingRule rule = new DigitalPricingRule();
        rule.setAdjustRate(new BigDecimal("-0.10"));
        when(iDigitalPricingRuleService.getOne(any())).thenReturn(rule);

        assertEquals(new BigDecimal("900.00"), digitalRecycleService.estimate(1L, "fair").getEstimatePrice());

        when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Collections.singletonList(new ProductSpec()));
        DigitalEstimateBo bo = new DigitalEstimateBo();
        bo.setModelId(1L);
        bo.setCondition("good");
        Map<String, String> params = new LinkedHashMap<>();
        params.put(" ", "x");
        params.put("storage", "256G");
        bo.setParams(params);
        assertEquals(new BigDecimal("1000.00"), digitalRecycleService.estimate(bo).getEstimatePrice());

        ProductModel nullPrice = model(2L, 1L, "X", null);
        when(productRecycleSupport.requireEnabledModel(2L)).thenReturn(nullPrice);
        when(productRecycleSupport.applySpecValuation(any(), eq(2L), any())).thenReturn(false);
        when(productRecycleSupport.listSpecsByModelId(2L)).thenReturn(Collections.emptyList());
        when(iDigitalPricingRuleService.getOne(any())).thenReturn(null);
        DigitalEstimateBo blankCond = new DigitalEstimateBo();
        blankCond.setModelId(2L);
        assertEquals(new BigDecimal("0.00"), digitalRecycleService.estimate(blankCond).getEstimatePrice());
    }

    @Test
    void listBrandsModelsAndSearchShouldCoverBranches() {
        when(productRecycleSupport.listEnabledBrandsByRecycleCode("digital")).thenReturn(Collections.emptyList());
        assertTrue(digitalRecycleService.listBrands("digital").isEmpty());
        assertTrue(digitalRecycleService.searchModels("a", "digital", 10).isEmpty());

        ProductBrand brand = brand(1L, "Apple");
        ProductBrand brand2 = brand(2L, "Other");
        when(productRecycleSupport.listEnabledBrandsByRecycleCode("luxury")).thenReturn(Arrays.asList(brand, brand2));
        assertEquals(2, digitalRecycleService.listBrands("LUXURY").size());

        when(productRecycleSupport.requireEnabledBrand(1L)).thenReturn(brand);
        when(productRecycleSupport.listEnabledModelsByBrandId(1L, "iphone")).thenReturn(Collections.emptyList());
        assertTrue(digitalRecycleService.listModels(1L, "iphone").isEmpty());

        ProductModel m1 = model(10L, 1L, "iPhone", "2000");
        m1.setCreateTime(LocalDateTime.now().minusDays(1));
        ProductModel m2 = model(11L, 1L, "旧机", null);
        m2.setCreateTime(null);
        ProductModel m1b = model(13L, 1L, "iPhone SE", "2000");
        m1b.setCreateTime(LocalDateTime.now());
        ProductModel m3 = model(12L, 99L, "Orphan", "3000");
        when(productRecycleSupport.listEnabledModelsByBrandId(1L, null)).thenReturn(Arrays.asList(m1, m2, m1b));
        when(productRecycleSupport.listEnabledModelsByBrandId(2L, null)).thenReturn(Collections.singletonList(m3));
        when(productRecycleSupport.listEnabledBrandsByRecycleCode("digital")).thenReturn(Arrays.asList(brand, brand2));

        assertEquals(3, digitalRecycleService.listModels(1L, null).size());
        List<DigitalSearchResultVo> search = digitalRecycleService.searchModels("apple", "digital", 0);
        assertFalse(search.isEmpty());
        // 同价按 createTime 再排序
        assertTrue(digitalRecycleService.searchModels("iphone", "digital", 100).size() <= 50);
        assertFalse(digitalRecycleService.searchModels("", "digital", 5).isEmpty());

        when(productRecycleSupport.listEnabledBrandsByRecycleCode("digital")).thenReturn(Collections.singletonList(brand));
        when(productRecycleSupport.listEnabledModelsByBrandId(1L, null)).thenReturn(Collections.emptyList());
        assertTrue(digitalRecycleService.searchModels("x", "digital", 5).isEmpty());
    }

    @Test
    void hotKeywordsShouldUseConfigOrFallback() {
        ProductBrand brand = brand(1L, "Apple");
        ProductModel model = model(10L, 1L, "iPhone", "1");
        when(productRecycleSupport.listEnabledBrandsByRecycleCode("digital")).thenReturn(Collections.singletonList(brand));
        when(productRecycleSupport.listEnabledModelsByBrandId(1L, null)).thenReturn(Collections.singletonList(model));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("digital_hot_keywords_digital")))
                    .thenReturn("[\"A\",\" \",\"B\",\"C\",\"D\",\"E\",\"F\",\"G\",\"H\",\"I\"]");
            assertEquals(8, digitalRecycleService.hotKeywords("digital").size());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("[]");
            assertEquals(Collections.singletonList("iPhone"), digitalRecycleService.hotKeywords("digital"));

            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenThrow(new RuntimeException("x"));
            assertEquals(Collections.singletonList("iPhone"), digitalRecycleService.hotKeywords("digital"));
        }
    }

    @Test
    void getSpecPageShouldBuildBoundSections() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);

            DigitalSpecPageVo empty = digitalRecycleService.getSpecPage(null, "luxury", "b", "m");
            assertEquals("luxury", empty.getScene());
            assertTrue(empty.getSections().isEmpty());

            ProductModel model = model(1L, 2L, "Bag", "100");
            model.setSpecJson("{\"storage\":\"256G\",\"color\":\"黑色\",\"series\":\"Pro\",\"material\":\"铝\"}");
            ProductBrand brand = brand(2L, "Brand");
            when(productRecycleSupport.requireEnabledModel(1L)).thenReturn(model);
            when(productRecycleSupport.requireEnabledBrand(2L)).thenReturn(brand);

            ProductSpec storage = spec("storage");
            ProductSpec color = spec("颜色");
            ProductSpec series = spec("系列");
            ProductSpec material = spec("材质");
            ProductSpec damage = spec("损伤");
            ProductSpec condition = spec("成色");
            ProductSpec age = spec("年限");
            ProductSpec custom = spec("自定义规格");
            ProductSpec dup = spec("容量版本");
            ProductSpec blankOpt = spec("空选项");
            when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Arrays.asList(
                    null, spec(" "), blankOpt, storage, color, series, material, damage, condition, age, custom, dup));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("空选项"))).thenReturn(Collections.emptyList());
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("storage"))).thenReturn(Arrays.asList("128G", "256G", " "));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("颜色"))).thenReturn(Collections.singletonList("黑色"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("系列"))).thenReturn(Collections.singletonList("Pro"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("材质"))).thenReturn(Collections.singletonList("铝"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("损伤"))).thenReturn(Collections.singletonList("无"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("成色"))).thenReturn(Collections.singletonList("良好"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("年限"))).thenReturn(Collections.singletonList("1年"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("自定义规格"))).thenReturn(Collections.singletonList("X"));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("容量版本"))).thenReturn(Collections.singletonList("256G"));

            DigitalSpecPageVo page = digitalRecycleService.getSpecPage(1L, "digital", null, null);
            assertFalse(page.getSections().isEmpty());
            assertEquals("Brand", page.getBrandName());

            model.setSpecJson(" ");
            assertNotNull(digitalRecycleService.getSpecPage(1L, "digital", null, null));

            model.setSpecJson("{bad");
            assertNotNull(digitalRecycleService.getSpecPage(1L, "digital", null, null));

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("digital_spec_digital")))
                    .thenReturn("{\"sections\":[null,{\"key\":\"storage\",\"label\":\"容量版本\",\"layout\":\"grid\",\"options\":[\"128G\"]}]}");
            assertNotNull(digitalRecycleService.getSpecPage(1L, "digital", null, null));

            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenThrow(new RuntimeException("x"));
            assertNotNull(digitalRecycleService.getSpecPage(1L, "digital", null, null));

            // firstSpecValue 全空 key + resolveSectionKey memory->storage（不走模板精确匹配）
            model.setSpecJson("{\"foo\":\"bar\"}");
            ProductSpec memory = spec("memory");
            when(productRecycleSupport.listSpecsByModelId(1L)).thenReturn(Collections.singletonList(memory));
            when(productRecycleSupport.listSpecOptionNames(eq(1L), eq("memory"))).thenReturn(Collections.singletonList("128G"));
            assertNotNull(digitalRecycleService.getSpecPage(1L, "digital", null, null));
        }
    }

    private ProductModel model(Long id, Long brandId, String name, String price) {
        ProductModel model = new ProductModel();
        model.setId(id);
        model.setBrandId(brandId);
        model.setName(name);
        model.setBasePrice(price == null ? null : new BigDecimal(price));
        model.setState(1);
        return model;
    }

    private ProductBrand brand(Long id, String name) {
        ProductBrand brand = new ProductBrand();
        brand.setId(id);
        brand.setName(name);
        return brand;
    }

    private ProductSpec spec(String name) {
        ProductSpec spec = new ProductSpec();
        spec.setAttributeName(name);
        return spec;
    }

    @Test
    void privateHelpersShouldCoverEmptyOrderOptionsAndBlankTemplateLookup() throws Exception {
        java.lang.reflect.Method orderOptions = DigitalRecycleServiceImpl.class
                .getDeclaredMethod("orderOptions", List.class, String.class);
        orderOptions.setAccessible(true);
        assertTrue(((List<?>) orderOptions.invoke(digitalRecycleService, Collections.emptyList(), "x")).isEmpty());

        java.lang.reflect.Method findTemplateSection = DigitalRecycleServiceImpl.class
                .getDeclaredMethod("findTemplateSection", String.class, List.class);
        findTemplateSection.setAccessible(true);
        assertNull(findTemplateSection.invoke(digitalRecycleService, " ", Collections.emptyList()));
        assertNull(findTemplateSection.invoke(digitalRecycleService, "x", null));

        java.lang.reflect.Method firstSpecValue = DigitalRecycleServiceImpl.class
                .getDeclaredMethod("firstSpecValue", com.alibaba.fastjson2.JSONObject.class, String[].class);
        firstSpecValue.setAccessible(true);
        assertNull(firstSpecValue.invoke(digitalRecycleService, new Object[]{null, new String[]{"a"}}));
        assertNull(firstSpecValue.invoke(digitalRecycleService, new com.alibaba.fastjson2.JSONObject(), new String[]{"a"}));
    }
}
