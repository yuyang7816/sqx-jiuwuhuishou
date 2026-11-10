package com.sqx.app.recycle.appliance.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductCategory;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.domain.entity.ProductSpec;
import com.sqx.app.product.support.ProductRecycleSupport;
import com.sqx.app.recycle.appliance.db.service.IApplianceHeroFlowService;
import com.sqx.app.recycle.appliance.domain.bo.ApplianceEstimateBo;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryOverviewVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceEstimateVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceHeroFlowVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceModelVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecBrandVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecConfigVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecFieldVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecModelVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecPageVo;
import com.sqx.app.recycle.appliance.service.ApplianceRecycleService;
import com.sqx.app.recycle.appliance.support.ApplianceEstimateRuleSupport;
import com.sqx.app.recycle.appliance.support.ApplianceHeroFlowDefaults;
import com.sqx.app.recycle.appliance.support.ApplianceSpecDefaults;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 家电回收业务服务实现（主数据读取 product_*）
 */
@Service
public class ApplianceRecycleServiceImpl implements ApplianceRecycleService {

    private static final String RULE_KEY_CONDITION = "condition";
    private static final String RECYCLE_CODE = "appliance";
    private static final Integer STATE_ENABLED = 1;

    @Resource
    private ProductRecycleSupport productRecycleSupport;
    @Resource
    private IApplianceHeroFlowService iApplianceHeroFlowService;

    @Override
    public ApplianceSpecPageVo getSpecPage(String categoryName) {
        List<ApplianceCategoryVo> categories = listCategories();
        ApplianceSpecPageVo pageVo = new ApplianceSpecPageVo();
        pageVo.setCategories(categories);
        pageVo.setConfig(buildSpecConfig(resolveCategory(categoryName, categories)));
        return pageVo;
    }

    @Override
    public List<ApplianceCategoryOverviewVo> listCategoryOverview() {
        List<ProductCategory> categoryList = productRecycleSupport.listEnabledCategories(RECYCLE_CODE);
        if (CollectionUtils.isEmpty(categoryList)) {
            return Collections.emptyList();
        }
        return categoryList.stream().map(category -> {
            List<ApplianceModelVo> models = listModels(category.getId());
            ApplianceCategoryOverviewVo vo = new ApplianceCategoryOverviewVo();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setIcon(category.getImage());
            vo.setSort(category.getSort());
            vo.setPriceTitle(resolvePriceTitle(category, models));
            vo.setModels(models);
            fillPriceRange(vo, models);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ApplianceCategoryVo> listCategories() {
        List<ProductCategory> categoryList = productRecycleSupport.listEnabledCategories(RECYCLE_CODE);
        if (CollectionUtils.isEmpty(categoryList)) {
            return Collections.emptyList();
        }
        return categoryList.stream().map(this::toCategoryVo).collect(Collectors.toList());
    }

    @Override
    public List<ApplianceModelVo> listModels(Long categoryId) {
        List<ProductModel> modelList = productRecycleSupport.listEnabledModelsByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(modelList)) {
            return Collections.emptyList();
        }
        return modelList.stream().map(model -> toModelVo(model, categoryId)).collect(Collectors.toList());
    }

    @Override
    public List<ApplianceHeroFlowVo> listHeroFlows() {
        List<ApplianceHeroFlow> flowList = iApplianceHeroFlowService.list(new LambdaQueryWrapper<ApplianceHeroFlow>()
                .eq(ApplianceHeroFlow::getState, STATE_ENABLED)
                .orderByAsc(ApplianceHeroFlow::getSort)
                .orderByDesc(ApplianceHeroFlow::getCreateTime));
        if (CollectionUtils.isEmpty(flowList)) {
            return ApplianceHeroFlowDefaults.defaultFlows();
        }
        return flowList.stream().map(flow -> {
            ApplianceHeroFlowVo vo = new ApplianceHeroFlowVo();
            vo.setName(flow.getName());
            vo.setIcon(flow.getIcon());
            vo.setSort(flow.getSort());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public ApplianceEstimateVo estimate(Long modelId, String condition) {
        ApplianceEstimateBo estimateBo = new ApplianceEstimateBo();
        estimateBo.setModelId(modelId);
        estimateBo.setCondition(condition);
        return estimate(estimateBo);
    }

    @Override
    public ApplianceEstimateVo estimate(ApplianceEstimateBo estimateBo) {
        ProductModel model = productRecycleSupport.requireEnabledModel(estimateBo.getModelId());
        ProductCategory category = productRecycleSupport.resolveCategoryByModel(model);
        String categoryName = category.getName();
        JSONObject categoryRules = ApplianceEstimateRuleSupport.resolveCategoryRules(categoryName);
        Map<String, String> specParams = collectSpecParams(estimateBo);

        BigDecimal minBase = model.resolveMinPrice();
        BigDecimal maxBase = model.resolveMaxPrice();
        if (maxBase.compareTo(minBase) < 0) {
            maxBase = minBase;
        }

        // 仅累加管理端「规格估值」；未配置规格时区间 = 最低/最高估值本身（不再用品类内置加减规则，避免默认 condition 抬价）
        BigDecimal[] adjustHolder = {BigDecimal.ZERO};
        productRecycleSupport.applySpecValuation(adjustHolder, model.getId(), specParams);
        BigDecimal adjust = adjustHolder[0] == null ? BigDecimal.ZERO : adjustHolder[0];

        BigDecimal floor = ApplianceEstimateRuleSupport.resolveMinPrice(categoryRules);
        BigDecimal lowPrice = minBase.add(adjust);
        BigDecimal highPrice = maxBase.add(adjust);
        if (lowPrice.compareTo(floor) < 0) {
            lowPrice = floor;
        }
        if (highPrice.compareTo(lowPrice) < 0) {
            highPrice = lowPrice;
        }
        lowPrice = lowPrice.setScale(2, RoundingMode.HALF_UP);
        highPrice = highPrice.setScale(2, RoundingMode.HALF_UP);
        BigDecimal estimatePrice = lowPrice.add(highPrice)
                .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

        ApplianceEstimateVo vo = new ApplianceEstimateVo();
        vo.setModelId(model.getId());
        vo.setCondition(resolveCondition(estimateBo.getCondition(), estimateBo.getParams()));
        vo.setEstimatePrice(estimatePrice);
        vo.setLowPrice(lowPrice);
        vo.setHighPrice(highPrice);
        vo.setReward(0);
        return vo;
    }

    private Map<String, String> collectSpecParams(ApplianceEstimateBo estimateBo) {
        Map<String, String> params = new LinkedHashMap<>();
        if (MapUtils.isNotEmpty(estimateBo.getParams())) {
            estimateBo.getParams().forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    params.put(key, value.trim());
                }
            });
        }
        appendConditionParam(params, resolveCondition(estimateBo.getCondition(), estimateBo.getParams()));
        if (StringUtils.isNotBlank(estimateBo.getDamage()) && !params.containsKey("damage")) {
            params.put("damage", estimateBo.getDamage().trim());
        }
        if (StringUtils.isNotBlank(estimateBo.getAge()) && !params.containsKey("age")) {
            params.put("age", estimateBo.getAge().trim());
        }
        return params;
    }

    private void appendConditionParam(Map<String, String> params, String condition) {
        if (StringUtils.isBlank(condition) || params.containsKey("condition")) {
            return;
        }
        if ("good".equalsIgnoreCase(condition)) {
            params.put("condition", "良好");
            return;
        }
        if ("fair".equalsIgnoreCase(condition)) {
            params.put("condition", "正常老化");
            return;
        }
        if ("poor".equalsIgnoreCase(condition)) {
            params.put("condition", "成色一般");
            return;
        }
        params.put("condition", condition);
    }

    private String resolveCondition(String condition, Map<String, String> params) {
        if (StringUtils.isNotBlank(condition)) {
            return condition;
        }
        if (MapUtils.isNotEmpty(params) && StringUtils.isNotBlank(params.get(RULE_KEY_CONDITION))) {
            return params.get(RULE_KEY_CONDITION);
        }
        return null;
    }

    private String resolvePriceTitle(ProductCategory category, List<ApplianceModelVo> models) {
        if (CollectionUtils.isNotEmpty(models)) {
            return models.get(0).getName();
        }
        return category.getName() + "回收";
    }

    private void fillPriceRange(ApplianceCategoryOverviewVo vo, List<ApplianceModelVo> models) {
        if (CollectionUtils.isEmpty(models)) {
            vo.setPriceMin(BigDecimal.ZERO);
            vo.setPriceMax(BigDecimal.ZERO);
            vo.setPrice("￥0");
            return;
        }
        List<BigDecimal> prices = models.stream()
                .map(ApplianceModelVo::getBasePrice)
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(prices)) {
            vo.setPriceMin(BigDecimal.ZERO);
            vo.setPriceMax(BigDecimal.ZERO);
            vo.setPrice("￥0");
            return;
        }
        BigDecimal min = prices.get(0);
        BigDecimal max = prices.get(prices.size() - 1);
        vo.setPriceMin(min);
        vo.setPriceMax(max);
        vo.setPrice(formatPriceRange(min, max));
    }

    private String formatPriceRange(BigDecimal min, BigDecimal max) {
        String minText = formatPrice(min);
        String maxText = formatPrice(max);
        if (min.compareTo(max) == 0) {
            return "￥" + minText;
        }
        return "￥" + minText + " - " + maxText;
    }

    private String formatPrice(BigDecimal price) {
        if (Objects.isNull(price)) {
            return "0";
        }
        return price.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    private ProductCategory resolveCategory(String categoryName, List<ApplianceCategoryVo> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            throw new BizException("暂无可用品类");
        }
        if (StringUtils.isNotBlank(categoryName)) {
            for (ApplianceCategoryVo categoryVo : categories) {
                if (Objects.equals(categoryName, categoryVo.getName())) {
                    return productRecycleSupport.requireEnabledCategory(categoryVo.getId());
                }
            }
        }
        return productRecycleSupport.requireEnabledCategory(categories.get(0).getId());
    }

    private ApplianceSpecConfigVo buildSpecConfig(ProductCategory category) {
        JSONObject template = resolveSpecTemplate(category.getName());
        ApplianceSpecConfigVo configVo = new ApplianceSpecConfigVo();
        configVo.setCategoryId(category.getId());
        configVo.setCategoryName(category.getName());
        configVo.setTitle(template.getString("title"));
        configVo.setSubtitle(template.getString("subtitle"));
        configVo.setIcon(StringUtils.defaultIfBlank(template.getString("icon"), category.getImage()));
        configVo.setCardLabel(template.getString("cardLabel"));
        configVo.setReward(template.getInteger("reward"));
        // 不向用户端返回品类公用规格；规格/成色选项只挂在型号 product_spec 上
        List<ApplianceSpecFieldVo> templateFields = template.getList("fields", ApplianceSpecFieldVo.class);
        configVo.setFields(Collections.emptyList());
        configVo.setDamageOptions(Collections.emptyList());
        configVo.setConditionOptions(Collections.emptyList());
        configVo.setAgeOptions(Collections.emptyList());
        configVo.setBrands(buildBrandGroups(category.getId(), templateFields));
        return configVo;
    }

    private JSONObject resolveSpecTemplate(String categoryName) {
        String configKey = "appliance_spec_" + categoryName;
        try {
            String configValue = ConfigUtil.getConfigValueOrDefault(configKey);
            if (StringUtils.isNotBlank(configValue)) {
                return JSON.parseObject(configValue);
            }
        } catch (Exception ignored) {
            // 使用内置默认模板
        }
        return ApplianceSpecDefaults.getTemplate(categoryName);
    }

    private List<ApplianceSpecBrandVo> buildBrandGroups(Long categoryId, List<ApplianceSpecFieldVo> templateFields) {
        List<ProductBrand> brands = productRecycleSupport.listEnabledBrandsByCategoryIds(Collections.singletonList(categoryId));
        if (CollectionUtils.isEmpty(brands)) {
            return Collections.emptyList();
        }
        List<ApplianceSpecBrandVo> result = new ArrayList<>();
        for (ProductBrand brand : brands) {
            List<ProductModel> models = productRecycleSupport.listEnabledModelsByBrandId(brand.getId(), null);
            if (CollectionUtils.isEmpty(models)) {
                continue;
            }
            ApplianceSpecBrandVo brandVo = new ApplianceSpecBrandVo();
            brandVo.setName(brand.getName());
            List<ApplianceSpecModelVo> modelVos = new ArrayList<>();
            for (ProductModel model : models) {
                ApplianceSpecModelVo modelVo = new ApplianceSpecModelVo();
                modelVo.setId(model.getId());
                modelVo.setName(model.getName());
                modelVo.setBasePrice(model.getBasePrice());
                modelVo.setMinPrice(model.resolveMinPrice());
                modelVo.setMaxPrice(model.resolveMaxPrice());
                JSONObject specs = parseSpecJson(model.getSpecJson());
                specs.remove("brand");
                modelVo.setSpecs(toStringSpecMap(specs));
                modelVo.setFields(buildModelBoundFields(model.getId(), templateFields));
                modelVo.setDamageOptions(productRecycleSupport.listSpecOptionNames(model.getId(), "damage"));
                modelVo.setConditionOptions(productRecycleSupport.listSpecOptionNames(model.getId(), "condition"));
                modelVo.setAgeOptions(productRecycleSupport.listSpecOptionNames(model.getId(), "age"));
                modelVos.add(modelVo);
            }
            brandVo.setModels(modelVos);
            result.add(brandVo);
        }
        return result;
    }

    /**
     * 仅返回当前型号在 product_spec 中实际绑定的规格选项，绝不回填品类公用模板 options。
     */
    private List<ApplianceSpecFieldVo> buildModelBoundFields(Long modelId, List<ApplianceSpecFieldVo> templateFields) {
        List<ApplianceSpecFieldVo> result = new ArrayList<>();
        Set<String> boundNormalizedKeys = new HashSet<>();

        for (ProductSpec spec : productRecycleSupport.listSpecsByModelId(modelId)) {
            if (spec == null || StringUtils.isBlank(spec.getAttributeName())) {
                continue;
            }
            String attrName = spec.getAttributeName().trim();
            String attrLower = attrName.toLowerCase();
            if (isSharedAssessAttribute(attrLower) || boundNormalizedKeys.contains(attrLower)) {
                continue;
            }
            List<String> options = productRecycleSupport.listSpecOptionNames(modelId, attrName);
            if (CollectionUtils.isEmpty(options)) {
                continue;
            }
            ApplianceSpecFieldVo item = new ApplianceSpecFieldVo();
            // key 必须与 product_spec.attributeName 一致，估价才能按 attPrice 命中
            item.setKey(attrName);
            ApplianceSpecFieldVo templateField = findTemplateField(attrName, templateFields);
            item.setLabel(templateField != null
                    ? StringUtils.defaultIfBlank(templateField.getLabel(), attrName)
                    : attrName);
            item.setOptions(new ArrayList<>(new LinkedHashSet<>(options)));
            item.setMultiSelect(spec.getMultiSelect() != null && spec.getMultiSelect() == 1 ? 1 : 0);
            result.add(item);
            boundNormalizedKeys.add(attrLower);
            if (templateField != null && StringUtils.isNotBlank(templateField.getKey())) {
                boundNormalizedKeys.add(templateField.getKey().trim().toLowerCase());
            }
            if (templateField != null && StringUtils.isNotBlank(templateField.getLabel())) {
                boundNormalizedKeys.add(templateField.getLabel().trim().toLowerCase());
            }
        }
        return result;
    }

    private ApplianceSpecFieldVo findTemplateField(String keyOrLabel, List<ApplianceSpecFieldVo> templateFields) {
        if (CollectionUtils.isEmpty(templateFields) || StringUtils.isBlank(keyOrLabel)) {
            return null;
        }
        for (ApplianceSpecFieldVo field : templateFields) {
            if (field == null) {
                continue;
            }
            if (keyOrLabel.equalsIgnoreCase(StringUtils.defaultString(field.getKey()))
                    || keyOrLabel.equalsIgnoreCase(StringUtils.defaultString(field.getLabel()))) {
                return field;
            }
        }
        return null;
    }

    private boolean isSharedAssessAttribute(String attrLower) {
        return attrLower.contains("损伤") || "damage".equals(attrLower)
                || attrLower.contains("成色") || attrLower.contains("新旧") || "condition".equals(attrLower)
                || attrLower.contains("年限") || "age".equals(attrLower);
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

    private Map<String, String> toStringSpecMap(JSONObject specs) {
        if (specs == null || specs.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : specs.entrySet()) {
            if (StringUtils.isBlank(entry.getKey()) || entry.getValue() == null) {
                continue;
            }
            result.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return result;
    }

    private ApplianceCategoryVo toCategoryVo(ProductCategory category) {
        ApplianceCategoryVo vo = new ApplianceCategoryVo();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setIcon(category.getImage());
        vo.setDisplayTitle(category.getName());
        vo.setSort(category.getSort());
        vo.setState(category.getState());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }

    private ApplianceModelVo toModelVo(ProductModel model, Long categoryId) {
        ApplianceModelVo vo = new ApplianceModelVo();
        vo.setId(model.getId());
        vo.setCategoryId(categoryId);
        vo.setName(model.getName());
        vo.setSpecJson(model.getSpecJson());
        vo.setBasePrice(model.getBasePrice());
        vo.setState(model.getState());
        vo.setCreateTime(model.getCreateTime());
        vo.setUpdateTime(model.getUpdateTime());
        return vo;
    }
}
