package com.sqx.app.admin.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.product.domain.bo.*;
import com.sqx.app.admin.product.domain.vo.AdminProductBrandVo;
import com.sqx.app.admin.product.domain.vo.AdminProductCategoryVo;
import com.sqx.app.admin.product.domain.vo.AdminProductModelVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecTemplateVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecVo;
import com.sqx.app.admin.product.domain.vo.ProductSpecAttributeVo;
import com.sqx.app.admin.product.service.AdminProductManageService;
import com.sqx.app.product.db.service.IProductBrandService;
import com.sqx.app.product.db.service.IProductCategoryService;
import com.sqx.app.product.db.service.IProductModelService;
import com.sqx.app.product.db.service.IProductSpecService;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductCategory;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.domain.entity.ProductSpec;
import com.sqx.app.recycle.appliance.support.ApplianceSpecDefaults;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import com.sqx.app.recycle.digital.support.DigitalSpecDefaults;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminProductManageServiceImpl implements AdminProductManageService {

    private static final String CODE_APPLIANCE = "appliance";
    private static final String CODE_DIGITAL = "digital";
    private static final String CODE_LUXURY = "luxury";

    @Resource
    private IProductCategoryService iProductCategoryService;
    @Resource
    private IProductBrandService iProductBrandService;
    @Resource
    private IProductModelService iProductModelService;
    @Resource
    private IProductSpecService iProductSpecService;
    @Resource
    private IRecycleCategoryService iRecycleCategoryService;

    @Override
    public void saveCategory(AdminProductCategorySaveBo saveBo) {
        requireRecycleCategory(saveBo.getRecycleCategoryId());
        ProductCategory entity = new ProductCategory();
        BeanUtils.copyProperties(saveBo, entity);
        if (Objects.isNull(entity.getSort())) {
            entity.setSort(0);
        }
        if (Objects.isNull(entity.getState())) {
            entity.setState(1);
        }
        iProductCategoryService.save(entity);
    }

    @Override
    public IPage<AdminProductCategoryVo> pageCategory(AdminProductCategoryPageBo pageBo) {
        Page<ProductCategory> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(pageBo.getRecycleCategoryId()), ProductCategory::getRecycleCategoryId, pageBo.getRecycleCategoryId())
                .like(StringUtils.isNotBlank(pageBo.getName()), ProductCategory::getName, pageBo.getName())
                .orderByAsc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreateTime);
        IPage<ProductCategory> page = iProductCategoryService.page(initPage, wrapper);
        Map<Long, RecycleCategory> recycleMap = loadRecycleCategoryMap(page.getRecords());
        List<AdminProductCategoryVo> voList = page.getRecords().stream()
                .map(item -> toCategoryVo(item, recycleMap.get(item.getRecycleCategoryId())))
                .collect(Collectors.toList());
        return PageUtil.copyPage(page, voList);
    }

    @Override
    public AdminProductCategoryVo infoCategory(AdminProductIdBo infoBo) {
        ProductCategory entity = requireCategory(infoBo.getId());
        RecycleCategory recycleCategory = iRecycleCategoryService.getById(entity.getRecycleCategoryId());
        return toCategoryVo(entity, recycleCategory);
    }

    @Override
    public void updateCategory(AdminProductCategoryUpdateBo updateBo) {
        requireCategory(updateBo.getId());
        requireRecycleCategory(updateBo.getRecycleCategoryId());
        ProductCategory entity = new ProductCategory();
        BeanUtils.copyProperties(updateBo, entity);
        iProductCategoryService.updateById(entity);
    }

    @Override
    public void deleteCategory(AdminProductIdBo infoBo) {
        requireCategory(infoBo.getId());
        long brandCount = iProductBrandService.count(new LambdaQueryWrapper<ProductBrand>()
                .eq(ProductBrand::getCategoryId, infoBo.getId()));
        if (brandCount > 0) {
            throw new BizException("该分类下存在品牌，请先删除品牌");
        }
        iProductCategoryService.removeById(infoBo.getId());
    }

    @Override
    public void saveBrand(AdminProductBrandSaveBo saveBo) {
        requireCategory(saveBo.getCategoryId());
        ProductBrand entity = new ProductBrand();
        BeanUtils.copyProperties(saveBo, entity);
        if (Objects.isNull(entity.getSort())) {
            entity.setSort(0);
        }
        if (Objects.isNull(entity.getState())) {
            entity.setState(1);
        }
        iProductBrandService.save(entity);
    }

    @Override
    public IPage<AdminProductBrandVo> pageBrand(AdminProductBrandPageBo pageBo) {
        Page<ProductBrand> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ProductBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductBrand::getCategoryId, pageBo.getCategoryId())
                .like(StringUtils.isNotBlank(pageBo.getName()), ProductBrand::getName, pageBo.getName())
                .orderByAsc(ProductBrand::getSort)
                .orderByDesc(ProductBrand::getCreateTime);
        IPage<ProductBrand> page = iProductBrandService.page(initPage, wrapper);
        ProductCategory category = iProductCategoryService.getById(pageBo.getCategoryId());
        String categoryName = category == null ? null : category.getName();
        List<AdminProductBrandVo> voList = page.getRecords().stream()
                .map(item -> toBrandVo(item, categoryName))
                .collect(Collectors.toList());
        return PageUtil.copyPage(page, voList);
    }

    @Override
    public AdminProductBrandVo infoBrand(AdminProductIdBo infoBo) {
        ProductBrand entity = requireBrand(infoBo.getId());
        ProductCategory category = iProductCategoryService.getById(entity.getCategoryId());
        return toBrandVo(entity, category == null ? null : category.getName());
    }

    @Override
    public void updateBrand(AdminProductBrandUpdateBo updateBo) {
        requireBrand(updateBo.getId());
        requireCategory(updateBo.getCategoryId());
        ProductBrand entity = new ProductBrand();
        BeanUtils.copyProperties(updateBo, entity);
        iProductBrandService.updateById(entity);
    }

    @Override
    public void deleteBrand(AdminProductIdBo infoBo) {
        requireBrand(infoBo.getId());
        long modelCount = iProductModelService.count(new LambdaQueryWrapper<ProductModel>()
                .eq(ProductModel::getBrandId, infoBo.getId()));
        if (modelCount > 0) {
            throw new BizException("该品牌下存在型号，请先删除型号");
        }
        iProductBrandService.removeById(infoBo.getId());
    }

    @Override
    public void saveModel(AdminProductModelSaveBo saveBo) {
        requireBrand(saveBo.getBrandId());
        ProductModel entity = new ProductModel();
        BeanUtils.copyProperties(saveBo, entity);
        if (Objects.isNull(entity.getSort())) {
            entity.setSort(0);
        }
        if (Objects.isNull(entity.getState())) {
            entity.setState(1);
        }
        normalizeModelPrices(entity);
        iProductModelService.save(entity);
    }

    @Override
    public IPage<AdminProductModelVo> pageModel(AdminProductModelPageBo pageBo) {
        Page<ProductModel> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductModel::getBrandId, pageBo.getBrandId())
                .like(StringUtils.isNotBlank(pageBo.getName()), ProductModel::getName, pageBo.getName())
                .orderByAsc(ProductModel::getSort)
                .orderByDesc(ProductModel::getCreateTime);
        IPage<ProductModel> page = iProductModelService.page(initPage, wrapper);
        ProductBrand brand = iProductBrandService.getById(pageBo.getBrandId());
        ProductCategory category = brand == null ? null : iProductCategoryService.getById(brand.getCategoryId());
        List<AdminProductModelVo> voList = page.getRecords().stream()
                .map(item -> toModelVo(item, brand, category))
                .collect(Collectors.toList());
        return PageUtil.copyPage(page, voList);
    }

    @Override
    public AdminProductModelVo infoModel(AdminProductIdBo infoBo) {
        ProductModel entity = requireModel(infoBo.getId());
        ProductBrand brand = iProductBrandService.getById(entity.getBrandId());
        ProductCategory category = brand == null ? null : iProductCategoryService.getById(brand.getCategoryId());
        return toModelVo(entity, brand, category);
    }

    @Override
    public void updateModel(AdminProductModelUpdateBo updateBo) {
        requireModel(updateBo.getId());
        requireBrand(updateBo.getBrandId());
        ProductModel entity = new ProductModel();
        BeanUtils.copyProperties(updateBo, entity);
        normalizeModelPrices(entity);
        iProductModelService.updateById(entity);
    }

    @Override
    public void deleteModel(AdminProductIdBo infoBo) {
        requireModel(infoBo.getId());
        long specCount = iProductSpecService.count(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getModelId, infoBo.getId()));
        if (specCount > 0) {
            throw new BizException("该型号下存在规格，请先删除规格");
        }
        iProductModelService.removeById(infoBo.getId());
    }

    @Override
    public void saveSpec(AdminProductSpecSaveBo saveBo) {
        requireModel(saveBo.getModelId());
        ProductSpec entity = buildSpecEntity(saveBo, null);
        iProductSpecService.save(entity);
        refreshAttributeSpecIds(entity);
    }

    @Override
    public IPage<AdminProductSpecVo> pageSpec(AdminProductSpecPageBo pageBo) {
        Page<ProductSpec> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<ProductSpec> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSpec::getModelId, pageBo.getModelId())
                .like(StringUtils.isNotBlank(pageBo.getAttributeName()), ProductSpec::getAttributeName, pageBo.getAttributeName())
                .orderByAsc(ProductSpec::getSort)
                .orderByDesc(ProductSpec::getCreateTime);
        IPage<ProductSpec> page = iProductSpecService.page(initPage, wrapper);
        ProductModel model = iProductModelService.getById(pageBo.getModelId());
        ProductBrand brand = model == null ? null : iProductBrandService.getById(model.getBrandId());
        List<AdminProductSpecVo> voList = page.getRecords().stream()
                .map(item -> toSpecVo(item, model, brand))
                .collect(Collectors.toList());
        return PageUtil.copyPage(page, voList);
    }

    @Override
    public AdminProductSpecVo infoSpec(AdminProductIdBo infoBo) {
        ProductSpec entity = requireSpec(infoBo.getId());
        ProductModel model = iProductModelService.getById(entity.getModelId());
        ProductBrand brand = model == null ? null : iProductBrandService.getById(model.getBrandId());
        return toSpecVo(entity, model, brand);
    }

    @Override
    public void updateSpec(AdminProductSpecUpdateBo updateBo) {
        requireSpec(updateBo.getId());
        requireModel(updateBo.getModelId());
        ProductSpec entity = buildSpecEntity(updateBo, updateBo.getId());
        iProductSpecService.updateById(entity);
        refreshAttributeSpecIds(entity);
    }

    @Override
    public void deleteSpec(AdminProductIdBo infoBo) {
        requireSpec(infoBo.getId());
        iProductSpecService.removeById(infoBo.getId());
    }

    @Override
    public AdminProductSpecTemplateVo loadSpecTemplate(AdminProductSpecTemplateBo templateBo) {
        ProductModel model = requireModel(templateBo.getModelId());
        ProductBrand brand = requireBrand(model.getBrandId());
        ProductCategory category = requireCategory(brand.getCategoryId());
        RecycleCategory recycleCategory = requireRecycleCategory(category.getRecycleCategoryId());
        String code = StringUtils.defaultString(recycleCategory.getCode());

        AdminProductSpecTemplateVo vo = new AdminProductSpecTemplateVo();
        vo.setRecycleCategoryCode(code);
        vo.setCategoryName(category.getName());

        if (CODE_APPLIANCE.equalsIgnoreCase(code)) {
            String configKey = "appliance_spec_" + category.getName();
            JSONObject template = readConfigJson(configKey, ApplianceSpecDefaults.getTemplate(category.getName()));
            vo.setConfigKey(configKey);
            vo.setSections(parseApplianceSections(template));
            return vo;
        }
        if (CODE_DIGITAL.equalsIgnoreCase(code) || CODE_LUXURY.equalsIgnoreCase(code)) {
            String scene = CODE_LUXURY.equalsIgnoreCase(code) ? CODE_LUXURY : CODE_DIGITAL;
            String configKey = "digital_spec_" + scene;
            JSONObject template = readConfigJson(configKey, DigitalSpecDefaults.getTemplate(scene));
            vo.setConfigKey(configKey);
            vo.setSections(parseDigitalSections(template));
            return vo;
        }
        vo.setConfigKey(null);
        vo.setSections(Collections.emptyList());
        return vo;
    }

    private JSONObject readConfigJson(String configKey, JSONObject fallback) {
        try {
            String configValue = ConfigUtil.getConfigValueOrDefault(configKey);
            if (StringUtils.isNotBlank(configValue)) {
                return JSON.parseObject(configValue);
            }
        } catch (Exception ignored) {
            // fallback
        }
        return fallback == null ? new JSONObject() : fallback;
    }

    private List<AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo> parseDigitalSections(JSONObject template) {
        List<AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo> sections = new ArrayList<>();
        JSONArray array = template.getJSONArray("sections");
        if (array == null || array.isEmpty()) {
            return sections;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            if (item == null) {
                continue;
            }
            AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo section =
                    new AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo();
            section.setKey(item.getString("key"));
            section.setLabel(StringUtils.defaultIfBlank(item.getString("label"), item.getString("key")));
            section.setOptions(toStringList(item.getJSONArray("options")));
            sections.add(section);
        }
        return sections;
    }

    private List<AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo> parseApplianceSections(JSONObject template) {
        List<AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo> sections = new ArrayList<>();
        JSONArray fields = template.getJSONArray("fields");
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                JSONObject item = fields.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo section =
                        new AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo();
                section.setKey(item.getString("key"));
                section.setLabel(StringUtils.defaultIfBlank(item.getString("label"), item.getString("key")));
                section.setOptions(toStringList(item.getJSONArray("options")));
                sections.add(section);
            }
        }
        appendSimpleSection(sections, "damage", "外观损伤", template.getJSONArray("damageOptions"));
        appendSimpleSection(sections, "condition", "成色", template.getJSONArray("conditionOptions"));
        appendSimpleSection(sections, "age", "使用年限", template.getJSONArray("ageOptions"));
        return sections;
    }

    private void appendSimpleSection(List<AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo> sections,
                                     String key, String label, JSONArray options) {
        if (options == null || options.isEmpty()) {
            return;
        }
        AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo section =
                new AdminProductSpecTemplateVo.AdminProductSpecTemplateSectionVo();
        section.setKey(key);
        section.setLabel(label);
        section.setOptions(toStringList(options));
        sections.add(section);
    }

    private List<String> toStringList(JSONArray array) {
        if (array == null || array.isEmpty()) {
            return Collections.emptyList();
        }
        return array.toJavaList(String.class).stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private ProductSpec buildSpecEntity(AdminProductSpecSaveBo saveBo, Long id) {
        ProductSpec entity = new ProductSpec();
        entity.setId(id);
        entity.setModelId(saveBo.getModelId());
        entity.setAttributeName(saveBo.getAttributeName());
        entity.setMultiSelect(Objects.isNull(saveBo.getMultiSelect()) ? 0 : saveBo.getMultiSelect());
        entity.setSort(Objects.isNull(saveBo.getSort()) ? 0 : saveBo.getSort());
        entity.setAttributesJson(JSON.toJSONString(normalizeAttributes(saveBo.getAttributes(), id)));
        return entity;
    }

    private void refreshAttributeSpecIds(ProductSpec entity) {
        if (Objects.isNull(entity.getId())) {
            return;
        }
        List<ProductSpecAttributeVo> attributes = parseAttributes(entity.getAttributesJson(), entity.getId());
        entity.setAttributesJson(JSON.toJSONString(attributes));
        iProductSpecService.updateById(entity);
    }

    private List<ProductSpecAttributeVo> normalizeAttributes(List<ProductSpecAttributeVo> attributes, Long specId) {
        if (CollectionUtils.isEmpty(attributes)) {
            return Collections.emptyList();
        }
        List<ProductSpecAttributeVo> result = new ArrayList<>();
        int index = 1;
        for (ProductSpecAttributeVo item : attributes) {
            if (item == null || StringUtils.isBlank(item.getAttName())) {
                continue;
            }
            ProductSpecAttributeVo vo = new ProductSpecAttributeVo();
            vo.setId(StringUtils.defaultIfBlank(item.getId(), String.valueOf(index++)));
            vo.setAttName(item.getAttName().trim());
            vo.setAttPrice(StringUtils.defaultString(item.getAttPrice(), "0"));
            vo.setImg(StringUtils.defaultString(item.getImg(), ""));
            if (specId != null) {
                vo.setSpecId(String.valueOf(specId));
            }
            result.add(vo);
        }
        return result;
    }

    private List<ProductSpecAttributeVo> parseAttributes(String json, Long specId) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        try {
            List<ProductSpecAttributeVo> list = JSON.parseArray(json, ProductSpecAttributeVo.class);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            for (ProductSpecAttributeVo item : list) {
                if (specId != null) {
                    item.setSpecId(String.valueOf(specId));
                }
            }
            return list;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private Map<Long, RecycleCategory> loadRecycleCategoryMap(List<ProductCategory> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyMap();
        }
        List<Long> ids = records.stream().map(ProductCategory::getRecycleCategoryId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return iRecycleCategoryService.listByIds(ids).stream()
                .collect(Collectors.toMap(RecycleCategory::getId, item -> item, (a, b) -> a));
    }

    private AdminProductCategoryVo toCategoryVo(ProductCategory entity, RecycleCategory recycleCategory) {
        AdminProductCategoryVo vo = new AdminProductCategoryVo();
        BeanUtils.copyProperties(entity, vo);
        if (recycleCategory != null) {
            vo.setRecycleCategoryName(recycleCategory.getName());
            vo.setRecycleCategoryCode(recycleCategory.getCode());
        }
        return vo;
    }

    private AdminProductBrandVo toBrandVo(ProductBrand entity, String categoryName) {
        AdminProductBrandVo vo = new AdminProductBrandVo();
        BeanUtils.copyProperties(entity, vo);
        vo.setCategoryName(categoryName);
        return vo;
    }

    private AdminProductModelVo toModelVo(ProductModel entity, ProductBrand brand, ProductCategory category) {
        AdminProductModelVo vo = new AdminProductModelVo();
        BeanUtils.copyProperties(entity, vo);
        if (vo.getMinPrice() == null) {
            vo.setMinPrice(entity.resolveMinPrice());
        }
        if (vo.getMaxPrice() == null) {
            vo.setMaxPrice(entity.resolveMaxPrice());
        }
        if (brand != null) {
            vo.setBrandId(brand.getId());
            vo.setBrandName(brand.getName());
            vo.setCategoryId(brand.getCategoryId());
        }
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        return vo;
    }

    /**
     * 家电：最低/最高估值，并回写 base_price 为中值；
     * 数码/奢侈品等：单个预估价 base_price，min/max 与之对齐。
     */
    private void normalizeModelPrices(ProductModel entity) {
        boolean rangeMode = isApplianceModel(entity.getBrandId());
        if (rangeMode) {
            BigDecimal minPrice = entity.getMinPrice();
            BigDecimal maxPrice = entity.getMaxPrice();
            if (minPrice == null && maxPrice == null && entity.getBasePrice() != null) {
                minPrice = entity.getBasePrice();
                maxPrice = entity.getBasePrice();
            }
            if (minPrice == null) {
                minPrice = BigDecimal.ZERO;
            }
            if (maxPrice == null) {
                maxPrice = minPrice;
            }
            if (maxPrice.compareTo(minPrice) < 0) {
                throw new BizException("最高估值不能低于最低估值");
            }
            entity.setMinPrice(minPrice);
            entity.setMaxPrice(maxPrice);
            entity.setBasePrice(minPrice.add(maxPrice).divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP));
            return;
        }
        BigDecimal basePrice = entity.getBasePrice() == null ? BigDecimal.ZERO : entity.getBasePrice();
        entity.setBasePrice(basePrice);
        entity.setMinPrice(basePrice);
        entity.setMaxPrice(basePrice);
    }

    private boolean isApplianceModel(Long brandId) {
        if (brandId == null) {
            return false;
        }
        ProductBrand brand = iProductBrandService.getById(brandId);
        if (brand == null || brand.getCategoryId() == null) {
            return false;
        }
        ProductCategory category = iProductCategoryService.getById(brand.getCategoryId());
        if (category == null || category.getRecycleCategoryId() == null) {
            return false;
        }
        RecycleCategory recycleCategory = iRecycleCategoryService.getById(category.getRecycleCategoryId());
        return recycleCategory != null && CODE_APPLIANCE.equalsIgnoreCase(recycleCategory.getCode());
    }

    private AdminProductSpecVo toSpecVo(ProductSpec entity, ProductModel model, ProductBrand brand) {
        AdminProductSpecVo vo = new AdminProductSpecVo();
        vo.setId(entity.getId());
        vo.setModelId(entity.getModelId());
        vo.setModelName(model == null ? null : model.getName());
        vo.setBrandId(brand == null ? null : brand.getId());
        vo.setBrandName(brand == null ? null : brand.getName());
        vo.setAttributeName(entity.getAttributeName());
        vo.setAttributes(parseAttributes(entity.getAttributesJson(), entity.getId()));
        vo.setMultiSelect(entity.getMultiSelect());
        vo.setSort(entity.getSort());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private RecycleCategory requireRecycleCategory(Long id) {
        RecycleCategory entity = iRecycleCategoryService.getById(id);
        if (Objects.isNull(entity)) {
            throw new BizException("回收品类不存在");
        }
        return entity;
    }

    private ProductCategory requireCategory(Long id) {
        ProductCategory entity = iProductCategoryService.getById(id);
        if (Objects.isNull(entity)) {
            throw new BizException("商品分类不存在");
        }
        return entity;
    }

    private ProductBrand requireBrand(Long id) {
        ProductBrand entity = iProductBrandService.getById(id);
        if (Objects.isNull(entity)) {
            throw new BizException("品牌不存在");
        }
        return entity;
    }

    private ProductModel requireModel(Long id) {
        ProductModel entity = iProductModelService.getById(id);
        if (Objects.isNull(entity)) {
            throw new BizException("型号不存在");
        }
        return entity;
    }

    private ProductSpec requireSpec(Long id) {
        ProductSpec entity = iProductSpecService.getById(id);
        if (Objects.isNull(entity)) {
            throw new BizException("规格不存在");
        }
        return entity;
    }
}
