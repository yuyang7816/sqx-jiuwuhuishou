package com.sqx.app.product.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.admin.product.domain.vo.ProductSpecAttributeVo;
import com.sqx.app.product.db.service.IProductBrandService;
import com.sqx.app.product.db.service.IProductCategoryService;
import com.sqx.app.product.db.service.IProductModelService;
import com.sqx.app.product.db.service.IProductSpecService;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductCategory;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.domain.entity.ProductSpec;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductRecycleSupportTest {

    @Mock
    private IRecycleCategoryService iRecycleCategoryService;
    @Mock
    private IProductCategoryService iProductCategoryService;
    @Mock
    private IProductBrandService iProductBrandService;
    @Mock
    private IProductModelService iProductModelService;
    @Mock
    private IProductSpecService iProductSpecService;

    @InjectMocks
    private ProductRecycleSupport support;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(
                RecycleCategory.class,
                ProductCategory.class,
                ProductBrand.class,
                ProductModel.class,
                ProductSpec.class
        );
    }

    @Test
    void requireRecycleCategoryByCodeShouldReturnOrThrow() {
        when(iRecycleCategoryService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> support.requireRecycleCategoryByCode("x"));

        RecycleCategory category = recycleCategory(1L, "appliance");
        when(iRecycleCategoryService.getOne(any(LambdaQueryWrapper.class))).thenReturn(category);
        assertEquals("appliance", support.requireRecycleCategoryByCode("appliance").getCode());
    }

    @Test
    void listEnabledCategoriesShouldQueryByRecycleCode() {
        RecycleCategory recycle = recycleCategory(1L, "digital");
        when(iRecycleCategoryService.getOne(any(LambdaQueryWrapper.class))).thenReturn(recycle);

        ProductCategory category = category(10L, 1L, "手机");
        when(iProductCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));

        assertEquals(1, support.listEnabledCategories("digital").size());
    }

    @Test
    void requireEnabledCategoryShouldValidateState() {
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> support.requireEnabledCategory(10L));

        ProductCategory disabled = category(10L, 1L, "手机");
        disabled.setState(0);
        when(iProductCategoryService.getById(10L)).thenReturn(disabled);
        assertThrows(BizException.class, () -> support.requireEnabledCategory(10L));

        ProductCategory enabled = category(10L, 1L, "手机");
        when(iProductCategoryService.getById(10L)).thenReturn(enabled);
        assertEquals("手机", support.requireEnabledCategory(10L).getName());
    }

    @Test
    void listEnabledBrandsByCategoryIdsShouldHandleEmptyAndQuery() {
        assertTrue(support.listEnabledBrandsByCategoryIds(null).isEmpty());
        assertTrue(support.listEnabledBrandsByCategoryIds(Collections.emptyList()).isEmpty());

        ProductBrand brand = brand(20L, 10L, "Apple");
        when(iProductBrandService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(brand));
        assertEquals(1, support.listEnabledBrandsByCategoryIds(Collections.singletonList(10L)).size());
    }

    @Test
    void listEnabledBrandsByRecycleCodeShouldChainCategories() {
        RecycleCategory recycle = recycleCategory(1L, "digital");
        when(iRecycleCategoryService.getOne(any(LambdaQueryWrapper.class))).thenReturn(recycle);

        ProductCategory category = category(10L, 1L, "手机");
        when(iProductCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(category));

        ProductBrand brand = brand(20L, 10L, "Apple");
        when(iProductBrandService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(brand));

        assertEquals(1, support.listEnabledBrandsByRecycleCode("digital").size());
    }

    @Test
    void requireEnabledBrandShouldValidateState() {
        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> support.requireEnabledBrand(20L));

        ProductBrand disabled = brand(20L, 10L, "Apple");
        disabled.setState(0);
        when(iProductBrandService.getById(20L)).thenReturn(disabled);
        assertThrows(BizException.class, () -> support.requireEnabledBrand(20L));

        ProductBrand enabled = brand(20L, 10L, "Apple");
        when(iProductBrandService.getById(20L)).thenReturn(enabled);
        assertEquals("Apple", support.requireEnabledBrand(20L).getName());
    }

    @Test
    void listEnabledModelsByBrandIdShouldFilterKeyword() {
        ProductModel model = model(30L, 20L, "iPhone 13");
        when(iProductModelService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(model));

        assertEquals(1, support.listEnabledModelsByBrandId(20L, "iphone").size());
        assertEquals(1, support.listEnabledModelsByBrandId(20L, null).size());
    }

    @Test
    void listEnabledModelsByCategoryIdShouldHandleEmptyBrandsAndQuery() {
        when(iProductBrandService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(support.listEnabledModelsByCategoryId(10L).isEmpty());

        ProductBrand brand = brand(20L, 10L, "Apple");
        when(iProductBrandService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(brand));

        ProductModel model = model(30L, 20L, "iPhone 13");
        when(iProductModelService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(model));
        assertEquals(1, support.listEnabledModelsByCategoryId(10L).size());
    }

    @Test
    void requireEnabledModelAndResolveCategoryShouldChain() {
        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> support.requireEnabledModel(30L));

        ProductModel disabled = model(30L, 20L, "iPhone");
        disabled.setState(0);
        when(iProductModelService.getById(30L)).thenReturn(disabled);
        assertThrows(BizException.class, () -> support.requireEnabledModel(30L));

        ProductModel enabled = model(30L, 20L, "iPhone");
        when(iProductModelService.getById(30L)).thenReturn(enabled);
        ProductBrand brand = brand(20L, 10L, "Apple");
        when(iProductBrandService.getById(20L)).thenReturn(brand);
        ProductCategory category = category(10L, 1L, "手机");
        when(iProductCategoryService.getById(10L)).thenReturn(category);

        assertEquals("iPhone", support.requireEnabledModel(30L).getName());
        assertEquals("手机", support.resolveCategoryByModel(enabled).getName());
    }

    @Test
    void listSpecsByModelIdShouldQuery() {
        ProductSpec spec = spec(40L, 30L, "颜色", "[]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(spec));
        assertEquals(1, support.listSpecsByModelId(30L).size());
    }

    @Test
    void buildSpecPriceMapShouldSkipInvalidAndNormalizeKeys() {
        ProductSpec blankName = spec(1L, 30L, " ", "[]");
        ProductSpec invalidJson = spec(2L, 30L, "容量版本", "{bad");
        ProductSpec valid = spec(3L, 30L, "颜色", "["
                + "{\"attName\":\"  黑色  \",\"attPrice\":\"10.5\"},"
                + "null,"
                + "{\"attName\":\" \",\"attPrice\":\"1\"},"
                + "{\"attName\":\"白色\",\"attPrice\":\"bad\"}"
                + "]");
        ProductSpec doorType = spec(4L, 30L, "门型", "[{\"attName\":\"双开门\",\"attPrice\":\"20\"}]");

        when(iProductSpecService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(null, blankName, invalidJson, valid, doorType));

        Map<String, Map<String, BigDecimal>> map = support.buildSpecPriceMap(30L);
        assertTrue(map.containsKey("storage"));
        assertTrue(map.containsKey("容量版本"));
        assertTrue(map.containsKey("颜色"));
        assertEquals(new BigDecimal("10.50"), map.get("color").get("黑色"));
        assertEquals(0, BigDecimal.ZERO.compareTo(map.get("color").get("白色")));
        assertEquals(new BigDecimal("20.00"), map.get("doorType").get("双开门"));
    }

    @Test
    void applySpecValuationShouldHandleNullEmptyAndHit() {
        assertFalse(support.applySpecValuation(null, 30L, Collections.emptyMap()));
        assertFalse(support.applySpecValuation(new BigDecimal[0], 30L, Collections.emptyMap()));

        BigDecimal[] holder = new BigDecimal[]{new BigDecimal("100")};
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertFalse(support.applySpecValuation(holder, 30L, Collections.singletonMap("color", "黑")));

        ProductSpec spec = spec(1L, 30L, "颜色", "[{\"attName\":\"黑\",\"attPrice\":\"15\"}]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(spec));

        Map<String, String> params = new HashMap<>();
        params.put("color", "黑");
        params.put("", "x");
        params.put("series", "");
        params.put("storage", "128G");
        assertTrue(support.applySpecValuation(holder, 30L, params));
        assertEquals(0, new BigDecimal("115.00").compareTo(holder[0]));

        holder[0] = null;
        assertTrue(support.applySpecValuation(holder, 30L, Collections.singletonMap("颜色", "黑")));
        assertEquals(new BigDecimal("15.00"), holder[0]);

        ProductSpec multiSpec = spec(2L, 30L, "配件",
                "[{\"attName\":\"遥控\",\"attPrice\":\"10\"},{\"attName\":\"说明书\",\"attPrice\":\"5\"}]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(multiSpec));
        holder[0] = new BigDecimal("100");
        assertTrue(support.applySpecValuation(holder, 30L, Collections.singletonMap("配件", "遥控,说明书")));
        assertEquals(0, new BigDecimal("115.00").compareTo(holder[0]));
    }

    @Test
    void applySpecValuationShouldMatchApplianceTemplateKeysToChineseAttrs() {
        ProductSpec volume = spec(1L, 30L, "容积",
                "[{\"attName\":\"200L以下\",\"attPrice\":\"10\"},{\"attName\":\"400L以上\",\"attPrice\":\"150\"}]");
        ProductSpec door = spec(2L, 30L, "门型",
                "[{\"attName\":\"双门\",\"attPrice\":\"30\"},{\"attName\":\"十字对开门\",\"attPrice\":\"130\"}]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(volume, door));

        BigDecimal[] holder = new BigDecimal[]{new BigDecimal("100")};
        Map<String, String> params = new HashMap<>();
        // 前端历史模板英文 key，应对齐到管理端中文属性估值
        params.put("capacity", "400L以上");
        params.put("doorType", "十字对开门");
        assertTrue(support.applySpecValuation(holder, 30L, params));
        assertEquals(0, new BigDecimal("380.00").compareTo(holder[0]));

        holder[0] = new BigDecimal("100");
        Map<String, String> chineseParams = new HashMap<>();
        chineseParams.put("容积", "200L以下");
        chineseParams.put("门型", "双门");
        assertTrue(support.applySpecValuation(holder, 30L, chineseParams));
        assertEquals(0, new BigDecimal("140.00").compareTo(holder[0]));
    }

    @Test
    void listSpecOptionNamesShouldResolveExactAndNormalizedKeys() {
        ProductSpec spec = spec(1L, 30L, "材质", "[{\"attName\":\"皮革\",\"attPrice\":\"5\"}]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(spec));

        assertTrue(support.listSpecOptionNames(30L, "unknown").isEmpty());
        List<String> options = support.listSpecOptionNames(30L, "material");
        assertEquals(Collections.singletonList("皮革"), options);
        assertEquals(options, support.listSpecOptionNames(30L, "材质"));
    }

    @Test
    void resolveRecycleCodeByBrandShouldReturnCodeOrNull() {
        ProductBrand brand = brand(20L, 10L, "Apple");
        ProductCategory category = category(10L, 1L, "手机");
        when(iProductCategoryService.getById(10L)).thenReturn(category);
        when(iRecycleCategoryService.getById(1L)).thenReturn(recycleCategory(1L, "digital"));

        assertEquals("digital", support.resolveRecycleCodeByBrand(brand));

        when(iRecycleCategoryService.getById(1L)).thenReturn(null);
        assertNull(support.resolveRecycleCodeByBrand(brand));
    }

    @Test
    void normalizeKeyBranchesShouldBeReachableThroughPriceMap() {
        stubSpecForKey("memory", "8G", "1");
        assertNotNull(support.buildSpecPriceMap(30L).get("storage"));

        stubSpecForKey("colour", "红", "2");
        assertNotNull(support.buildSpecPriceMap(30L).get("color"));

        stubSpecForKey("系列", "Pro", "3");
        assertNotNull(support.buildSpecPriceMap(30L).get("series"));

        stubSpecForKey("damage", "轻微", "4");
        assertNotNull(support.buildSpecPriceMap(30L).get("damage"));

        stubSpecForKey("新旧程度", "九成新", "5");
        assertNotNull(support.buildSpecPriceMap(30L).get("condition"));

        stubSpecForKey("使用年限", "3年", "6");
        assertNotNull(support.buildSpecPriceMap(30L).get("age"));

        stubSpecForKey("custom-field", "A", "7");
        assertNotNull(support.buildSpecPriceMap(30L).get("custom-field"));
    }

    @Test
    void resolveOptionMapShouldReturnEmptyForBlankKeyOrEmptyPriceMap() {
        assertTrue(support.listSpecOptionNames(30L, " ").isEmpty());
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(support.listSpecOptionNames(30L, "color").isEmpty());
    }

    @Test
    void parseAttributesAndPriceShouldHandleBlankInputs() {
        ProductSpec blankJson = spec(1L, 30L, "颜色", " ");
        ProductSpec blankPrice = spec(2L, 30L, "颜色", "[{\"attName\":\"黑\",\"attPrice\":\"\"}]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(blankJson, blankPrice));

        Map<String, Map<String, BigDecimal>> map = support.buildSpecPriceMap(30L);
        assertTrue(map.containsKey("颜色"));
        assertEquals(0, BigDecimal.ZERO.compareTo(map.get("颜色").get("黑")));
    }

    @Test
    void normalizeKeyShouldReturnEmptyForBlankKey() throws Exception {
        java.lang.reflect.Method method = ProductRecycleSupport.class
                .getDeclaredMethod("normalizeKey", String.class);
        method.setAccessible(true);

        assertEquals("", method.invoke(support, ""));
        assertEquals("", method.invoke(support, "   "));
        assertEquals("", method.invoke(support, new Object[]{null}));
    }

    private void stubSpecForKey(String attributeName, String optionName, String price) {
        ProductSpec spec = spec(1L, 30L, attributeName,
                "[{\"attName\":\"" + optionName + "\",\"attPrice\":\"" + price + "\"}]");
        when(iProductSpecService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(spec));
    }

    private RecycleCategory recycleCategory(Long id, String code) {
        RecycleCategory entity = new RecycleCategory();
        entity.setId(id);
        entity.setCode(code);
        return entity;
    }

    private ProductCategory category(Long id, Long recycleCategoryId, String name) {
        ProductCategory entity = new ProductCategory();
        entity.setId(id);
        entity.setRecycleCategoryId(recycleCategoryId);
        entity.setName(name);
        entity.setState(ProductRecycleSupport.STATE_ENABLED);
        return entity;
    }

    private ProductBrand brand(Long id, Long categoryId, String name) {
        ProductBrand entity = new ProductBrand();
        entity.setId(id);
        entity.setCategoryId(categoryId);
        entity.setName(name);
        entity.setState(ProductRecycleSupport.STATE_ENABLED);
        return entity;
    }

    private ProductModel model(Long id, Long brandId, String name) {
        ProductModel entity = new ProductModel();
        entity.setId(id);
        entity.setBrandId(brandId);
        entity.setName(name);
        entity.setState(ProductRecycleSupport.STATE_ENABLED);
        return entity;
    }

    private ProductSpec spec(Long id, Long modelId, String attributeName, String json) {
        ProductSpec entity = new ProductSpec();
        entity.setId(id);
        entity.setModelId(modelId);
        entity.setAttributeName(attributeName);
        entity.setAttributesJson(json);
        return entity;
    }
}
