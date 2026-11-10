package com.sqx.app.product.support;

import com.alibaba.fastjson2.JSON;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 家电/数码/奢侈品统一主数据查询（product_*）
 */
@Component
public class ProductRecycleSupport {

    public static final Integer STATE_ENABLED = 1;

    @Resource
    private IRecycleCategoryService iRecycleCategoryService;
    @Resource
    private IProductCategoryService iProductCategoryService;
    @Resource
    private IProductBrandService iProductBrandService;
    @Resource
    private IProductModelService iProductModelService;
    @Resource
    private IProductSpecService iProductSpecService;

    public RecycleCategory requireRecycleCategoryByCode(String code) {
        RecycleCategory category = iRecycleCategoryService.getOne(new LambdaQueryWrapper<RecycleCategory>()
                .eq(RecycleCategory::getCode, code)
                .last("LIMIT 1"));
        if (Objects.isNull(category)) {
            throw new BizException("回收类型不存在: " + code);
        }
        return category;
    }

    public List<ProductCategory> listEnabledCategories(String recycleCode) {
        RecycleCategory recycleCategory = requireRecycleCategoryByCode(recycleCode);
        return iProductCategoryService.list(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getRecycleCategoryId, recycleCategory.getId())
                .eq(ProductCategory::getState, STATE_ENABLED)
                .orderByAsc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreateTime));
    }

    public ProductCategory requireEnabledCategory(Long categoryId) {
        ProductCategory category = iProductCategoryService.getById(categoryId);
        if (Objects.isNull(category) || !Objects.equals(category.getState(), STATE_ENABLED)) {
            throw new BizException("品类不存在或已下架");
        }
        return category;
    }

    public List<ProductBrand> listEnabledBrandsByCategoryIds(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        return iProductBrandService.list(new LambdaQueryWrapper<ProductBrand>()
                .in(ProductBrand::getCategoryId, categoryIds)
                .eq(ProductBrand::getState, STATE_ENABLED)
                .orderByAsc(ProductBrand::getSort)
                .orderByDesc(ProductBrand::getCreateTime));
    }

    public List<ProductBrand> listEnabledBrandsByRecycleCode(String recycleCode) {
        List<ProductCategory> categories = listEnabledCategories(recycleCode);
        List<Long> categoryIds = categories.stream().map(ProductCategory::getId).collect(Collectors.toList());
        return listEnabledBrandsByCategoryIds(categoryIds);
    }

    public ProductBrand requireEnabledBrand(Long brandId) {
        ProductBrand brand = iProductBrandService.getById(brandId);
        if (Objects.isNull(brand) || !Objects.equals(brand.getState(), STATE_ENABLED)) {
            throw new BizException("品牌不存在或已下架");
        }
        return brand;
    }

    public List<ProductModel> listEnabledModelsByBrandId(Long brandId, String keyword) {
        LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<ProductModel>()
                .eq(ProductModel::getBrandId, brandId)
                .eq(ProductModel::getState, STATE_ENABLED);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(ProductModel::getName, keyword.trim());
        }
        return iProductModelService.list(wrapper.orderByAsc(ProductModel::getSort)
                .orderByDesc(ProductModel::getCreateTime));
    }

    public List<ProductModel> listEnabledModelsByCategoryId(Long categoryId) {
        List<ProductBrand> brands = listEnabledBrandsByCategoryIds(Collections.singletonList(categoryId));
        if (CollectionUtils.isEmpty(brands)) {
            return Collections.emptyList();
        }
        List<Long> brandIds = brands.stream().map(ProductBrand::getId).collect(Collectors.toList());
        return iProductModelService.list(new LambdaQueryWrapper<ProductModel>()
                .in(ProductModel::getBrandId, brandIds)
                .eq(ProductModel::getState, STATE_ENABLED)
                .orderByAsc(ProductModel::getSort)
                .orderByDesc(ProductModel::getCreateTime));
    }

    public ProductModel requireEnabledModel(Long modelId) {
        ProductModel model = iProductModelService.getById(modelId);
        if (Objects.isNull(model) || !Objects.equals(model.getState(), STATE_ENABLED)) {
            throw new BizException("型号不存在或已下架");
        }
        return model;
    }

    public ProductCategory resolveCategoryByModel(ProductModel model) {
        ProductBrand brand = requireEnabledBrand(model.getBrandId());
        return requireEnabledCategory(brand.getCategoryId());
    }

    public List<ProductSpec> listSpecsByModelId(Long modelId) {
        return iProductSpecService.list(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getModelId, modelId)
                .orderByAsc(ProductSpec::getSort)
                .orderByDesc(ProductSpec::getCreateTime));
    }

    public Map<String, Map<String, BigDecimal>> buildSpecPriceMap(Long modelId) {
        List<ProductSpec> specs = listSpecsByModelId(modelId);
        Map<String, Map<String, BigDecimal>> result = new HashMap<>();
        for (ProductSpec spec : specs) {
            if (spec == null || StringUtils.isBlank(spec.getAttributeName())) {
                continue;
            }
            String attrKey = normalizeKey(spec.getAttributeName());
            Map<String, BigDecimal> optionMap = result.computeIfAbsent(attrKey, key -> new HashMap<>());
            for (ProductSpecAttributeVo attribute : parseAttributes(spec.getAttributesJson())) {
                if (attribute == null || StringUtils.isBlank(attribute.getAttName())) {
                    continue;
                }
                optionMap.put(attribute.getAttName().trim(), parsePrice(attribute.getAttPrice()));
            }
            // 同时保留原始 attributeName，便于直接匹配文案 key
            result.put(spec.getAttributeName().trim(), optionMap);
        }
        return result;
    }

    /**
     * 按所选规格累加估值；命中任一规格配置时返回 true。
     */
    public boolean applySpecValuation(BigDecimal[] priceHolder, Long modelId, Map<String, String> params) {
        if (priceHolder == null || priceHolder.length == 0) {
            return false;
        }
        Map<String, Map<String, BigDecimal>> priceMap = buildSpecPriceMap(modelId);
        if (MapUtils.isEmpty(priceMap) || MapUtils.isEmpty(params)) {
            return false;
        }
        boolean hit = false;
        BigDecimal price = priceHolder[0] == null ? BigDecimal.ZERO : priceHolder[0];
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.isBlank(entry.getKey()) || StringUtils.isBlank(entry.getValue())) {
                continue;
            }
            BigDecimal adjust = resolveOptionPrice(priceMap, entry.getKey(), entry.getValue());
            if (adjust != null) {
                price = price.add(adjust);
                hit = true;
            }
        }
        priceHolder[0] = price;
        return hit;
    }

    public List<String> listSpecOptionNames(Long modelId, String attributeKey) {
        Map<String, Map<String, BigDecimal>> priceMap = buildSpecPriceMap(modelId);
        Map<String, BigDecimal> optionMap = resolveOptionMap(priceMap, attributeKey);
        if (MapUtils.isEmpty(optionMap)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(optionMap.keySet());
    }

    public String resolveRecycleCodeByBrand(ProductBrand brand) {
        ProductCategory category = requireEnabledCategory(brand.getCategoryId());
        RecycleCategory recycleCategory = iRecycleCategoryService.getById(category.getRecycleCategoryId());
        return recycleCategory == null ? null : recycleCategory.getCode();
    }

    private BigDecimal resolveOptionPrice(Map<String, Map<String, BigDecimal>> priceMap, String key, String value) {
        Map<String, BigDecimal> optionMap = resolveOptionMap(priceMap, key);
        if (MapUtils.isEmpty(optionMap)) {
            return null;
        }
        String trimmed = value.trim();
        BigDecimal exact = optionMap.get(trimmed);
        if (exact != null) {
            return exact;
        }
        // 多选：前端以逗号拼接多个属性值，分别累加估值
        if (!trimmed.contains(",")) {
            return null;
        }
        BigDecimal sum = null;
        for (String part : trimmed.split(",")) {
            if (StringUtils.isBlank(part)) {
                continue;
            }
            BigDecimal partPrice = optionMap.get(part.trim());
            if (partPrice == null) {
                continue;
            }
            sum = sum == null ? partPrice : sum.add(partPrice);
        }
        return sum;
    }

    private Map<String, BigDecimal> resolveOptionMap(Map<String, Map<String, BigDecimal>> priceMap, String key) {
        if (MapUtils.isEmpty(priceMap) || StringUtils.isBlank(key)) {
            return Collections.emptyMap();
        }
        String trimmedKey = key.trim();
        Map<String, BigDecimal> exact = priceMap.get(trimmedKey);
        if (MapUtils.isNotEmpty(exact)) {
            return exact;
        }
        String normalized = normalizeKey(trimmedKey);
        Map<String, BigDecimal> byNormalized = priceMap.get(normalized);
        if (MapUtils.isNotEmpty(byNormalized)) {
            return byNormalized;
        }
        // 兼容：前端模板英文 key（capacity）与管理端中文属性名（容积）归一化后一致
        for (Map.Entry<String, Map<String, BigDecimal>> entry : priceMap.entrySet()) {
            if (entry.getKey() == null || MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            if (normalized.equals(normalizeKey(entry.getKey()))) {
                return entry.getValue();
            }
        }
        return Collections.emptyMap();
    }

    private String normalizeKey(String key) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        String trimmed = key.trim().toLowerCase();
        // 数码容量版本（勿把家电 capacity/容积 并入）
        if (trimmed.contains("容量版本") || "storage".equals(trimmed) || "memory".equals(trimmed)) {
            return "storage";
        }
        if (trimmed.contains("颜色") || "color".equals(trimmed) || "colour".equals(trimmed)) {
            return "color";
        }
        if (trimmed.contains("系列") || "series".equals(trimmed) || "style".equals(trimmed)) {
            return "series";
        }
        if (trimmed.contains("材质") || "material".equals(trimmed)) {
            return "material";
        }
        if (trimmed.contains("损伤") || "damage".equals(trimmed)) {
            return "damage";
        }
        if (trimmed.contains("成色") || trimmed.contains("新旧") || "condition".equals(trimmed)) {
            return "condition";
        }
        if (trimmed.contains("年限") || "age".equals(trimmed)) {
            return "age";
        }
        if (trimmed.contains("门型") || "doortype".equals(trimmed)) {
            return "doorType";
        }
        // 家电：容积/匹数/容量 ↔ capacity（模板英文 key）
        if (trimmed.contains("容积") || trimmed.contains("匹数")
                || "容量".equals(trimmed) || trimmed.endsWith("容量")
                || "capacity".equals(trimmed)) {
            return "capacity";
        }
        // 家电：屏幕类型须先于「类型」匹配
        if (trimmed.contains("屏幕") || "screentype".equals(trimmed)) {
            return "screenType";
        }
        // 家电：类型/机型 ↔ type
        if (trimmed.contains("类型") || trimmed.contains("机型") || "type".equals(trimmed)) {
            return "type";
        }
        // 家电：尺寸 ↔ size
        if (trimmed.contains("尺寸") || "size".equals(trimmed)) {
            return "size";
        }
        return trimmed;
    }

    private List<ProductSpecAttributeVo> parseAttributes(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        try {
            List<ProductSpecAttributeVo> list = JSON.parseArray(json, ProductSpecAttributeVo.class);
            return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private BigDecimal parsePrice(String price) {
        if (StringUtils.isBlank(price)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(price.trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }
}
