package com.sqx.app.recycle.digital.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.domain.entity.ProductSpec;
import com.sqx.app.product.support.ProductRecycleSupport;
import com.sqx.app.recycle.digital.db.service.IDigitalPricingRuleService;
import com.sqx.app.recycle.digital.domain.bo.DigitalEstimateBo;
import com.sqx.app.recycle.digital.domain.entity.DigitalPricingRule;
import com.sqx.app.recycle.digital.domain.vo.DigitalBrandVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalEstimateVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalModelVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSearchResultVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSpecPageVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSpecSectionVo;
import com.sqx.app.recycle.digital.service.DigitalRecycleService;
import com.sqx.app.recycle.digital.support.DigitalSpecDefaults;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数码/奢侈品回收业务服务实现（主数据读取 product_*）
 */
@Service
public class DigitalRecycleServiceImpl implements DigitalRecycleService {

    private static final String RULE_KEY_CONDITION = "condition";
    private static final String SCENE_LUXURY = "luxury";
    private static final String SCENE_DIGITAL = "digital";
    private static final int DEFAULT_SEARCH_LIMIT = 20;
    private static final int DEFAULT_HOT_LIMIT = 8;
    private static final String[] STORAGE_KEYS = {"storage", "memory", "capacity"};
    private static final String[] COLOR_KEYS = {"color", "colour"};
    private static final String[] SERIES_KEYS = {"series", "style"};
    private static final String[] MATERIAL_KEYS = {"material"};

    @Resource
    private ProductRecycleSupport productRecycleSupport;

    @Resource
    private IDigitalPricingRuleService iDigitalPricingRuleService;

    @Override
    public List<DigitalBrandVo> listBrands(String scene) {
        String normalizedScene = normalizeScene(scene);
        List<ProductBrand> brandList = productRecycleSupport.listEnabledBrandsByRecycleCode(normalizedScene);
        if (CollectionUtils.isEmpty(brandList)) {
            return Collections.emptyList();
        }
        return brandList.stream().map(brand -> toBrandVo(brand, normalizedScene)).collect(Collectors.toList());
    }

    @Override
    public List<DigitalModelVo> listModels(Long brandId, String keyword) {
        productRecycleSupport.requireEnabledBrand(brandId);
        List<ProductModel> modelList = productRecycleSupport.listEnabledModelsByBrandId(brandId, keyword);
        if (CollectionUtils.isEmpty(modelList)) {
            return Collections.emptyList();
        }
        return modelList.stream().map(this::toModelVo).collect(Collectors.toList());
    }

    @Override
    public List<DigitalSearchResultVo> searchModels(String keyword, String scene, int limit) {
        String normalizedScene = normalizeScene(scene);
        int pageSize = limit > 0 ? Math.min(limit, 50) : DEFAULT_SEARCH_LIMIT;
        List<ProductBrand> brandList = productRecycleSupport.listEnabledBrandsByRecycleCode(normalizedScene);
        if (CollectionUtils.isEmpty(brandList)) {
            return Collections.emptyList();
        }

        Map<Long, ProductBrand> brandMap = brandList.stream()
                .collect(Collectors.toMap(ProductBrand::getId, brand -> brand, (left, right) -> left, LinkedHashMap::new));
        List<Long> brandIds = new ArrayList<>(brandMap.keySet());

        List<ProductModel> allModels = new ArrayList<>();
        for (Long brandId : brandIds) {
            allModels.addAll(productRecycleSupport.listEnabledModelsByBrandId(brandId, null));
        }
        if (CollectionUtils.isEmpty(allModels)) {
            return Collections.emptyList();
        }

        String trimmedKeyword = StringUtils.trimToEmpty(keyword);
        List<ProductModel> filtered = allModels.stream()
                .filter(model -> {
                    if (StringUtils.isBlank(trimmedKeyword)) {
                        return true;
                    }
                    ProductBrand brand = brandMap.get(model.getBrandId());
                    boolean brandMatched = brand != null && StringUtils.containsIgnoreCase(brand.getName(), trimmedKeyword);
                    return brandMatched || StringUtils.containsIgnoreCase(model.getName(), trimmedKeyword);
                })
                .sorted((left, right) -> {
                    BigDecimal leftPrice = left.getBasePrice() == null ? BigDecimal.ZERO : left.getBasePrice();
                    BigDecimal rightPrice = right.getBasePrice() == null ? BigDecimal.ZERO : right.getBasePrice();
                    int cmp = rightPrice.compareTo(leftPrice);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return Long.compare(
                            right.getCreateTime() == null ? 0L : right.getCreateTime().toEpochSecond(java.time.ZoneOffset.UTC),
                            left.getCreateTime() == null ? 0L : left.getCreateTime().toEpochSecond(java.time.ZoneOffset.UTC)
                    );
                })
                .limit(pageSize)
                .collect(Collectors.toList());

        return filtered.stream().map(model -> {
            DigitalSearchResultVo vo = new DigitalSearchResultVo();
            vo.setModelId(model.getId());
            vo.setBrandId(model.getBrandId());
            vo.setModelName(model.getName());
            vo.setBasePrice(model.getBasePrice());
            vo.setSpecJson(model.getSpecJson());
            vo.setScene(normalizedScene);
            ProductBrand brand = brandMap.get(model.getBrandId());
            if (Objects.nonNull(brand)) {
                vo.setBrandName(brand.getName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> hotKeywords(String scene) {
        String normalizedScene = normalizeScene(scene);
        String configKey = "digital_hot_keywords_" + normalizedScene;
        try {
            String configValue = ConfigUtil.getConfigValueOrDefault(configKey);
            if (StringUtils.isNotBlank(configValue)) {
                List<String> keywords = JSON.parseArray(configValue, String.class);
                if (CollectionUtils.isNotEmpty(keywords)) {
                    return keywords.stream()
                            .filter(StringUtils::isNotBlank)
                            .map(String::trim)
                            .limit(DEFAULT_HOT_LIMIT)
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception ignored) {
            // fallback to model names
        }

        return searchModels(null, normalizedScene, DEFAULT_HOT_LIMIT).stream()
                .map(DigitalSearchResultVo::getModelName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    public DigitalEstimateVo estimate(Long modelId, String condition) {
        DigitalEstimateBo estimateBo = new DigitalEstimateBo();
        estimateBo.setModelId(modelId);
        estimateBo.setCondition(condition);
        return estimate(estimateBo);
    }

    @Override
    public DigitalSpecPageVo getSpecPage(Long modelId, String scene, String brandName, String modelName) {
        String normalizedScene = SCENE_LUXURY.equalsIgnoreCase(scene) ? SCENE_LUXURY : SCENE_DIGITAL;

        DigitalSpecPageVo pageVo = new DigitalSpecPageVo();
        pageVo.setScene(normalizedScene);
        pageVo.setSceneLabel(DigitalSpecDefaults.resolveSceneLabel(normalizedScene));
        pageVo.setBrandName(brandName);
        pageVo.setModelName(modelName);
        // 不向用户端返回场景公用模板；规格仅来自型号 product_spec
        pageVo.setSections(Collections.emptyList());

        if (Objects.isNull(modelId)) {
            return pageVo;
        }

        ProductModel model = productRecycleSupport.requireEnabledModel(modelId);
        pageVo.setModelId(model.getId());
        pageVo.setModelName(model.getName());
        pageVo.setBasePrice(model.getBasePrice());

        ProductBrand brand = productRecycleSupport.requireEnabledBrand(model.getBrandId());
        pageVo.setBrandId(brand.getId());
        pageVo.setBrandName(brand.getName());
        pageVo.setSections(buildModelBoundSections(model.getId(), normalizedScene, parseSpecJson(model.getSpecJson())));
        return pageVo;
    }

    @Override
    public DigitalEstimateVo estimate(DigitalEstimateBo estimateBo) {
        ProductModel model = productRecycleSupport.requireEnabledModel(estimateBo.getModelId());
        String condition = estimateBo.getCondition();
        Map<String, String> params = collectSpecParams(estimateBo);

        BigDecimal[] priceHolder = {model.getBasePrice() == null ? BigDecimal.ZERO : model.getBasePrice()};
        boolean usedSpecValuation = productRecycleSupport.applySpecValuation(priceHolder, model.getId(), params);
        boolean hasModelSpecs = CollectionUtils.isNotEmpty(productRecycleSupport.listSpecsByModelId(model.getId()));
        // 型号已配置 product_spec 时，一律按「基础价 + 所选规格估值」计算；未配置才走旧定价规则
        BigDecimal estimatePrice = (usedSpecValuation || hasModelSpecs)
                ? priceHolder[0].setScale(2, RoundingMode.HALF_UP)
                : calculateEstimatePriceByRule(model.getBasePrice(), model.getId(), condition);

        DigitalEstimateVo vo = new DigitalEstimateVo();
        vo.setModelId(model.getId());
        vo.setCondition(condition);
        vo.setEstimatePrice(estimatePrice);
        return vo;
    }

    private Map<String, String> collectSpecParams(DigitalEstimateBo estimateBo) {
        Map<String, String> params = new LinkedHashMap<>();
        if (MapUtils.isNotEmpty(estimateBo.getParams())) {
            estimateBo.getParams().forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    params.put(key, value.trim());
                }
            });
        }
        if (StringUtils.isNotBlank(estimateBo.getCondition()) && !params.containsKey(RULE_KEY_CONDITION)) {
            params.put(RULE_KEY_CONDITION, estimateBo.getCondition().trim());
        }
        return params;
    }

    private JSONObject resolveSpecTemplate(String scene) {
        String configKey = "digital_spec_" + scene;
        try {
            String configValue = ConfigUtil.getConfigValueOrDefault(configKey);
            if (StringUtils.isNotBlank(configValue)) {
                return JSON.parseObject(configValue);
            }
        } catch (Exception ignored) {
            // 使用内置默认模板
        }
        return DigitalSpecDefaults.getTemplate(scene);
    }

    /**
     * 仅返回当前型号在 product_spec 中绑定的规格分组，绝不回填场景公用模板 options。
     */
    private List<DigitalSpecSectionVo> buildModelBoundSections(Long modelId, String scene, JSONObject specJson) {
        List<DigitalSpecSectionVo> templateSections = DigitalSpecDefaults.parseSections(resolveSpecTemplate(scene));
        List<DigitalSpecSectionVo> result = new ArrayList<>();
        Set<String> boundKeys = new LinkedHashSet<>();

        for (ProductSpec spec : productRecycleSupport.listSpecsByModelId(modelId)) {
            if (spec == null || StringUtils.isBlank(spec.getAttributeName())) {
                continue;
            }
            String attrName = spec.getAttributeName().trim();
            List<String> options = productRecycleSupport.listSpecOptionNames(modelId, attrName);
            if (CollectionUtils.isEmpty(options)) {
                continue;
            }
            String sectionKey = resolveSectionKey(attrName, templateSections);
            String normalizedKey = sectionKey.trim().toLowerCase();
            if (!boundKeys.add(normalizedKey)) {
                continue;
            }
            DigitalSpecSectionVo template = findTemplateSection(sectionKey, templateSections);
            DigitalSpecSectionVo item = new DigitalSpecSectionVo();
            item.setKey(template != null ? template.getKey() : sectionKey);
            item.setLabel(template != null
                    ? StringUtils.defaultIfBlank(template.getLabel(), attrName)
                    : attrName);
            item.setLayout(resolveSectionLayout(item.getKey(), template));
            item.setOptions(orderOptions(options, resolvePreferredValue(item.getKey(), specJson)));
            result.add(item);
        }
        return result;
    }

    private DigitalSpecSectionVo findTemplateSection(String keyOrLabel, List<DigitalSpecSectionVo> templateSections) {
        if (CollectionUtils.isEmpty(templateSections) || StringUtils.isBlank(keyOrLabel)) {
            return null;
        }
        for (DigitalSpecSectionVo section : templateSections) {
            if (section == null) {
                continue;
            }
            if (keyOrLabel.equalsIgnoreCase(StringUtils.defaultString(section.getKey()))
                    || keyOrLabel.equalsIgnoreCase(StringUtils.defaultString(section.getLabel()))) {
                return section;
            }
        }
        return null;
    }

    private String resolveSectionKey(String attributeName, List<DigitalSpecSectionVo> templateSections) {
        DigitalSpecSectionVo matched = findTemplateSection(attributeName, templateSections);
        if (matched != null && StringUtils.isNotBlank(matched.getKey())) {
            return matched.getKey();
        }
        String lower = attributeName.trim().toLowerCase();
        if (lower.contains("容量版本") || "storage".equals(lower) || "memory".equals(lower) || "capacity".equals(lower)) {
            return "storage";
        }
        if (lower.contains("颜色") || "color".equals(lower) || "colour".equals(lower)) {
            return "color";
        }
        if (lower.contains("系列") || "series".equals(lower) || "style".equals(lower)) {
            return "series";
        }
        if (lower.contains("材质") || "material".equals(lower)) {
            return "material";
        }
        if (lower.contains("损伤") || "damage".equals(lower)) {
            return "damage";
        }
        if (lower.contains("成色") || lower.contains("新旧") || "condition".equals(lower)) {
            return "condition";
        }
        if (lower.contains("年限") || "age".equals(lower)) {
            return "age";
        }
        return attributeName.trim();
    }

    private String resolveSectionLayout(String key, DigitalSpecSectionVo template) {
        if (template != null && StringUtils.isNotBlank(template.getLayout())) {
            return template.getLayout();
        }
        return "condition".equalsIgnoreCase(key) || "age".equalsIgnoreCase(key) ? "column" : "grid";
    }

    private String resolvePreferredValue(String key, JSONObject specJson) {
        if ("storage".equals(key)) {
            return firstSpecValue(specJson, STORAGE_KEYS);
        }
        if ("color".equals(key)) {
            return firstSpecValue(specJson, COLOR_KEYS);
        }
        if ("series".equals(key)) {
            return firstSpecValue(specJson, SERIES_KEYS);
        }
        if ("material".equals(key)) {
            return firstSpecValue(specJson, MATERIAL_KEYS);
        }
        return null;
    }

    /**
     * 仅调整已绑定选项顺序，不会把未绑定的公共值塞进 options。
     */
    private List<String> orderOptions(List<String> options, String preferredValue) {
        if (CollectionUtils.isEmpty(options)) {
            return Collections.emptyList();
        }
        Set<String> ordered = new LinkedHashSet<>();
        if (StringUtils.isNotBlank(preferredValue) && options.contains(preferredValue.trim())) {
            ordered.add(preferredValue.trim());
        }
        options.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .forEach(ordered::add);
        return new ArrayList<>(ordered);
    }

    private String firstSpecValue(JSONObject specJson, String[] keys) {
        if (specJson == null || specJson.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            String value = specJson.getString(key);
            if (StringUtils.isNotBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private JSONObject parseSpecJson(String specJson) {
        if (StringUtils.isBlank(specJson)) {
            return new JSONObject();
        }
        try {
            return JSON.parseObject(specJson);
        } catch (Exception error) {
            return new JSONObject();
        }
    }

    private BigDecimal calculateEstimatePriceByRule(BigDecimal basePrice, Long modelId, String condition) {
        BigDecimal price = basePrice == null ? BigDecimal.ZERO : basePrice;
        if (StringUtils.isBlank(condition)) {
            return price.setScale(2, RoundingMode.HALF_UP);
        }
        DigitalPricingRule pricingRule = iDigitalPricingRuleService.getOne(new LambdaQueryWrapper<DigitalPricingRule>()
                .eq(DigitalPricingRule::getModelId, modelId)
                .eq(DigitalPricingRule::getRuleKey, RULE_KEY_CONDITION)
                .eq(DigitalPricingRule::getRuleValue, condition)
                .last("LIMIT 1"));
        if (Objects.nonNull(pricingRule) && Objects.nonNull(pricingRule.getAdjustRate())) {
            price = price.multiply(BigDecimal.ONE.add(pricingRule.getAdjustRate()));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeScene(String scene) {
        return SCENE_LUXURY.equalsIgnoreCase(scene) ? SCENE_LUXURY : SCENE_DIGITAL;
    }

    private DigitalBrandVo toBrandVo(ProductBrand brand, String scene) {
        DigitalBrandVo vo = new DigitalBrandVo();
        vo.setId(brand.getId());
        vo.setName(brand.getName());
        vo.setLogo(brand.getImage());
        vo.setSort(brand.getSort());
        vo.setState(brand.getState());
        vo.setScene(scene);
        vo.setCreateTime(brand.getCreateTime());
        vo.setUpdateTime(brand.getUpdateTime());
        return vo;
    }

    private DigitalModelVo toModelVo(ProductModel model) {
        DigitalModelVo vo = new DigitalModelVo();
        vo.setId(model.getId());
        vo.setBrandId(model.getBrandId());
        vo.setName(model.getName());
        vo.setSpecJson(model.getSpecJson());
        vo.setBasePrice(model.getBasePrice());
        vo.setState(model.getState());
        vo.setCreateTime(model.getCreateTime());
        vo.setUpdateTime(model.getUpdateTime());
        return vo;
    }
}
