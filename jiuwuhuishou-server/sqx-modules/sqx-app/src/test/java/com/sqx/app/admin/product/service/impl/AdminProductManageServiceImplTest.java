package com.sqx.app.admin.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.product.domain.bo.AdminProductBrandPageBo;
import com.sqx.app.admin.product.domain.bo.AdminProductBrandSaveBo;
import com.sqx.app.admin.product.domain.bo.AdminProductBrandUpdateBo;
import com.sqx.app.admin.product.domain.bo.AdminProductCategoryPageBo;
import com.sqx.app.admin.product.domain.bo.AdminProductCategorySaveBo;
import com.sqx.app.admin.product.domain.bo.AdminProductCategoryUpdateBo;
import com.sqx.app.admin.product.domain.bo.AdminProductIdBo;
import com.sqx.app.admin.product.domain.bo.AdminProductModelPageBo;
import com.sqx.app.admin.product.domain.bo.AdminProductModelSaveBo;
import com.sqx.app.admin.product.domain.bo.AdminProductModelUpdateBo;
import com.sqx.app.admin.product.domain.bo.AdminProductSpecPageBo;
import com.sqx.app.admin.product.domain.bo.AdminProductSpecSaveBo;
import com.sqx.app.admin.product.domain.bo.AdminProductSpecTemplateBo;
import com.sqx.app.admin.product.domain.bo.AdminProductSpecUpdateBo;
import com.sqx.app.admin.product.domain.vo.AdminProductBrandVo;
import com.sqx.app.admin.product.domain.vo.AdminProductCategoryVo;
import com.sqx.app.admin.product.domain.vo.AdminProductModelVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecTemplateVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecVo;
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
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminProductManageServiceImplTest {

    @Mock
    private IProductCategoryService iProductCategoryService;
    @Mock
    private IProductBrandService iProductBrandService;
    @Mock
    private IProductModelService iProductModelService;
    @Mock
    private IProductSpecService iProductSpecService;
    @Mock
    private IRecycleCategoryService iRecycleCategoryService;

    @InjectMocks
    private AdminProductManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(ProductCategory.class, ProductBrand.class, ProductModel.class, ProductSpec.class);
    }

    // ── Category ──────────────────────────────────────────────────────────────

    @Test
    void saveCategoryShouldApplyDefaultsAndPersist() {
        AdminProductCategorySaveBo saveBo = new AdminProductCategorySaveBo();
        saveBo.setRecycleCategoryId(100L);
        saveBo.setName("冰箱");
        when(iRecycleCategoryService.getById(100L)).thenReturn(recycleCategory(100L, "appliance", "家电"));

        service.saveCategory(saveBo);

        ArgumentCaptor<ProductCategory> captor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(iProductCategoryService).save(captor.capture());
        ProductCategory saved = captor.getValue();
        assertEquals(0, saved.getSort());
        assertEquals(1, saved.getState());
        assertEquals("冰箱", saved.getName());
    }

    @Test
    void saveCategoryShouldFailWhenRecycleCategoryMissing() {
        AdminProductCategorySaveBo saveBo = new AdminProductCategorySaveBo();
        saveBo.setRecycleCategoryId(999L);
        saveBo.setName("冰箱");
        when(iRecycleCategoryService.getById(999L)).thenReturn(null);

        BizException ex = assertThrows(BizException.class, () -> service.saveCategory(saveBo));
        assertEquals("回收品类不存在", ex.getMessage());
        verify(iProductCategoryService, never()).save(any());
    }

    @Test
    void updateCategoryShouldValidateAndUpdate() {
        AdminProductCategoryUpdateBo updateBo = new AdminProductCategoryUpdateBo();
        updateBo.setId(10L);
        updateBo.setRecycleCategoryId(100L);
        updateBo.setName("洗衣机");
        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        when(iRecycleCategoryService.getById(100L)).thenReturn(recycleCategory(100L, "appliance", "家电"));

        service.updateCategory(updateBo);

        verify(iProductCategoryService).updateById(any(ProductCategory.class));
    }

    @Test
    void updateCategoryShouldFailWhenCategoryOrRecycleMissing() {
        AdminProductCategoryUpdateBo updateBo = new AdminProductCategoryUpdateBo();
        updateBo.setId(10L);
        updateBo.setRecycleCategoryId(100L);
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateCategory(updateBo));

        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        when(iRecycleCategoryService.getById(100L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateCategory(updateBo));
    }

    @Test
    void infoCategoryShouldReturnVoWithRecycleMeta() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(10L);
        ProductCategory entity = category(10L, 100L, "冰箱");
        when(iProductCategoryService.getById(10L)).thenReturn(entity);
        when(iRecycleCategoryService.getById(100L)).thenReturn(recycleCategory(100L, "appliance", "家电"));

        AdminProductCategoryVo vo = service.infoCategory(infoBo);

        assertEquals("冰箱", vo.getName());
        assertEquals("家电", vo.getRecycleCategoryName());
        assertEquals("appliance", vo.getRecycleCategoryCode());

        when(iRecycleCategoryService.getById(100L)).thenReturn(null);
        AdminProductCategoryVo noRecycle = service.infoCategory(infoBo);
        assertNull(noRecycle.getRecycleCategoryName());
        assertNull(noRecycle.getRecycleCategoryCode());
    }

    @Test
    void infoCategoryShouldFailWhenCategoryMissing() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(10L);
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoCategory(infoBo));
    }

    @Test
    void deleteCategoryShouldRejectWhenBrandsExist() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(10L);
        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        when(iProductBrandService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);

        BizException ex = assertThrows(BizException.class, () -> service.deleteCategory(infoBo));
        assertEquals("该分类下存在品牌，请先删除品牌", ex.getMessage());
        verify(iProductCategoryService, never()).removeById(any());

        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.deleteCategory(infoBo));
    }

    @Test
    void deleteCategoryShouldRemoveWhenNoBrands() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(10L);
        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        when(iProductBrandService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        service.deleteCategory(infoBo);

        verify(iProductCategoryService).removeById(10L);
    }

    @Test
    void pageCategoryShouldHandleEmptyRecordsAndNullRecycleIds() {
        AdminProductCategoryPageBo pageBo = new AdminProductCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        Page<ProductCategory> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iProductCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);
        assertEquals(0, service.pageCategory(pageBo).getRecords().size());

        ProductCategory noRecycle = category(1L, null, "无回收类型");
        Page<ProductCategory> nullRecyclePage = new Page<>(1, 10);
        nullRecyclePage.setRecords(Collections.singletonList(noRecycle));
        when(iProductCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(nullRecyclePage);

        AdminProductCategoryVo nullRecycleVo = service.pageCategory(pageBo).getRecords().get(0);
        assertNull(nullRecycleVo.getRecycleCategoryName());
        assertNull(nullRecycleVo.getRecycleCategoryCode());
        verify(iRecycleCategoryService, never()).listByIds(any());
    }

    @Test
    void pageCategoryShouldMapRecycleCategoriesAndHandleMissingRecycle() {
        AdminProductCategoryPageBo pageBo = new AdminProductCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setRecycleCategoryId(100L);
        pageBo.setName("冰");

        ProductCategory mapped = category(1L, 100L, "冰箱");
        ProductCategory orphan = category(2L, 200L, "未知");
        Page<ProductCategory> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(mapped, orphan));
        when(iProductCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(iRecycleCategoryService.listByIds(Arrays.asList(100L, 200L)))
                .thenReturn(Collections.singletonList(recycleCategory(100L, "appliance", "家电")));

        IPage<AdminProductCategoryVo> result = service.pageCategory(pageBo);

        assertEquals(2, result.getRecords().size());
        assertEquals("家电", result.getRecords().get(0).getRecycleCategoryName());
        assertNull(result.getRecords().get(1).getRecycleCategoryName());
    }

    // ── Brand ─────────────────────────────────────────────────────────────────

    @Test
    void saveBrandShouldApplyDefaultsAndPersist() {
        AdminProductBrandSaveBo saveBo = new AdminProductBrandSaveBo();
        saveBo.setCategoryId(10L);
        saveBo.setName("海尔");
        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));

        service.saveBrand(saveBo);

        ArgumentCaptor<ProductBrand> captor = ArgumentCaptor.forClass(ProductBrand.class);
        verify(iProductBrandService).save(captor.capture());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void saveBrandShouldFailWhenCategoryMissing() {
        AdminProductBrandSaveBo saveBo = new AdminProductBrandSaveBo();
        saveBo.setCategoryId(10L);
        saveBo.setName("海尔");
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.saveBrand(saveBo));
    }

    @Test
    void pageBrandShouldHandleCategoryNullAndFound() {
        AdminProductBrandPageBo pageBo = new AdminProductBrandPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setCategoryId(10L);
        pageBo.setName("海");

        ProductBrand brand = brand(20L, 10L, "海尔");
        Page<ProductBrand> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(brand));
        when(iProductBrandService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertNull(service.pageBrand(pageBo).getRecords().get(0).getCategoryName());

        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        assertEquals("冰箱", service.pageBrand(pageBo).getRecords().get(0).getCategoryName());
    }

    @Test
    void infoBrandShouldResolveCategoryName() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(20L);
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductCategoryService.getById(10L)).thenReturn(null);

        assertNull(service.infoBrand(infoBo).getCategoryName());

        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        assertEquals("冰箱", service.infoBrand(infoBo).getCategoryName());

        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoBrand(infoBo));
    }

    @Test
    void updateBrandShouldValidateAndUpdate() {
        AdminProductBrandUpdateBo updateBo = new AdminProductBrandUpdateBo();
        updateBo.setId(20L);
        updateBo.setCategoryId(10L);
        updateBo.setName("美的");
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));

        service.updateBrand(updateBo);
        verify(iProductBrandService).updateById(any(ProductBrand.class));
    }

    @Test
    void updateBrandShouldFailWhenBrandOrCategoryMissing() {
        AdminProductBrandUpdateBo updateBo = new AdminProductBrandUpdateBo();
        updateBo.setId(20L);
        updateBo.setCategoryId(10L);
        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateBrand(updateBo));

        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateBrand(updateBo));
    }

    @Test
    void deleteBrandShouldRejectWhenModelsExist() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(20L);
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductModelService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThrows(BizException.class, () -> service.deleteBrand(infoBo));
        verify(iProductBrandService, never()).removeById(any());

        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.deleteBrand(infoBo));
    }

    @Test
    void deleteBrandShouldRemoveWhenNoModels() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(20L);
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductModelService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        service.deleteBrand(infoBo);
        verify(iProductBrandService).removeById(20L);
    }

    // ── Model ─────────────────────────────────────────────────────────────────

    @Test
    void saveModelShouldDefaultBasePriceSortAndState() {
        AdminProductModelSaveBo saveBo = new AdminProductModelSaveBo();
        saveBo.setBrandId(20L);
        saveBo.setName("BCD-500");
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));

        service.saveModel(saveBo);

        ArgumentCaptor<ProductModel> captor = ArgumentCaptor.forClass(ProductModel.class);
        verify(iProductModelService).save(captor.capture());
        ProductModel saved = captor.getValue();
        assertEquals(BigDecimal.ZERO, saved.getBasePrice());
        assertEquals(BigDecimal.ZERO, saved.getMinPrice());
        assertEquals(BigDecimal.ZERO, saved.getMaxPrice());
        assertEquals(0, saved.getSort());
        assertEquals(1, saved.getState());
    }

    @Test
    void saveModelShouldFailWhenBrandMissing() {
        AdminProductModelSaveBo saveBo = new AdminProductModelSaveBo();
        saveBo.setBrandId(20L);
        saveBo.setName("BCD-500");
        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.saveModel(saveBo));
    }

    @Test
    void pageModelShouldHandleBrandAndCategoryNullChains() {
        AdminProductModelPageBo pageBo = new AdminProductModelPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setBrandId(20L);
        pageBo.setName("BCD");

        ProductModel model = model(30L, 20L, "BCD-500");
        Page<ProductModel> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(model));
        when(iProductModelService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        when(iProductBrandService.getById(20L)).thenReturn(null);
        AdminProductModelVo noBrand = service.pageModel(pageBo).getRecords().get(0);
        assertNull(noBrand.getBrandName());
        assertNull(noBrand.getCategoryName());

        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        AdminProductModelVo noCategory = service.pageModel(pageBo).getRecords().get(0);
        assertEquals("海尔", noCategory.getBrandName());
        assertNull(noCategory.getCategoryName());

        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        AdminProductModelVo full = service.pageModel(pageBo).getRecords().get(0);
        assertEquals("冰箱", full.getCategoryName());
    }

    @Test
    void infoModelShouldResolveBrandAndCategory() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(30L);
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));

        AdminProductModelVo vo = service.infoModel(infoBo);
        assertEquals("海尔", vo.getBrandName());
        assertEquals("冰箱", vo.getCategoryName());

        when(iProductBrandService.getById(20L)).thenReturn(null);
        AdminProductModelVo noBrand = service.infoModel(infoBo);
        assertNull(noBrand.getBrandName());
        assertNull(noBrand.getCategoryName());

        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoModel(infoBo));
    }

    @Test
    void updateModelShouldValidateAndUpdate() {
        AdminProductModelUpdateBo updateBo = new AdminProductModelUpdateBo();
        updateBo.setId(30L);
        updateBo.setBrandId(20L);
        updateBo.setName("BCD-600");
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));

        service.updateModel(updateBo);
        verify(iProductModelService).updateById(any(ProductModel.class));

        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateModel(updateBo));

        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateModel(updateBo));
    }

    @Test
    void deleteModelShouldRejectWhenSpecsExist() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(30L);
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductSpecService.count(any(LambdaQueryWrapper.class))).thenReturn(3L);

        assertThrows(BizException.class, () -> service.deleteModel(infoBo));
        verify(iProductModelService, never()).removeById(any());

        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.deleteModel(infoBo));
    }

    @Test
    void deleteModelShouldRemoveWhenNoSpecs() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(30L);
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductSpecService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        service.deleteModel(infoBo);
        verify(iProductModelService).removeById(30L);
    }

    // ── Spec ──────────────────────────────────────────────────────────────────

    @Test
    void saveSpecShouldNormalizeAttributesAndRefreshSpecIds() {
        AdminProductSpecSaveBo saveBo = new AdminProductSpecSaveBo();
        saveBo.setModelId(30L);
        saveBo.setAttributeName("容量");
        saveBo.setAttributes(buildMixedAttributes());
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        doAnswer(invocation -> {
            ProductSpec entity = invocation.getArgument(0);
            entity.setId(40L);
            return true;
        }).when(iProductSpecService).save(any(ProductSpec.class));

        service.saveSpec(saveBo);

        verify(iProductSpecService).save(any(ProductSpec.class));
        ArgumentCaptor<ProductSpec> updateCaptor = ArgumentCaptor.forClass(ProductSpec.class);
        verify(iProductSpecService).updateById(updateCaptor.capture());
        List<ProductSpecAttributeVo> refreshed = JSON.parseArray(updateCaptor.getValue().getAttributesJson(), ProductSpecAttributeVo.class);
        assertEquals(2, refreshed.size());
        assertEquals("40", refreshed.get(0).getSpecId());
        assertEquals("1", refreshed.get(0).getId());
        assertEquals("已有ID", refreshed.get(1).getAttName());
    }

    @Test
    void saveSpecShouldSkipRefreshWhenIdNotAssigned() {
        AdminProductSpecSaveBo saveBo = new AdminProductSpecSaveBo();
        saveBo.setModelId(30L);
        saveBo.setAttributeName("容量");
        saveBo.setAttributes(null);
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductSpecService.save(any(ProductSpec.class))).thenReturn(true);

        service.saveSpec(saveBo);

        ArgumentCaptor<ProductSpec> captor = ArgumentCaptor.forClass(ProductSpec.class);
        verify(iProductSpecService).save(captor.capture());
        assertEquals(0, captor.getValue().getMultiSelect());
        assertEquals(0, captor.getValue().getSort());
        verify(iProductSpecService, never()).updateById(any(ProductSpec.class));
    }

    @Test
    void updateSpecShouldNormalizeWithSpecIdAndRefresh() {
        AdminProductSpecUpdateBo updateBo = new AdminProductSpecUpdateBo();
        updateBo.setId(40L);
        updateBo.setModelId(30L);
        updateBo.setAttributeName("颜色");
        updateBo.setMultiSelect(1);
        updateBo.setSort(5);
        ProductSpecAttributeVo attr = new ProductSpecAttributeVo();
        attr.setAttName("  红色  ");
        updateBo.setAttributes(Collections.singletonList(attr));

        when(iProductSpecService.getById(40L)).thenReturn(spec(40L, 30L, "容量", "[]"));
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));

        service.updateSpec(updateBo);

        ArgumentCaptor<ProductSpec> captor = ArgumentCaptor.forClass(ProductSpec.class);
        verify(iProductSpecService, times(2)).updateById(captor.capture());
        ProductSpec refreshed = captor.getAllValues().get(1);
        List<ProductSpecAttributeVo> attrs = JSON.parseArray(refreshed.getAttributesJson(), ProductSpecAttributeVo.class);
        assertEquals("40", attrs.get(0).getSpecId());
        assertEquals("红色", attrs.get(0).getAttName());
        assertEquals("0", attrs.get(0).getAttPrice());
        assertEquals("", attrs.get(0).getImg());
    }

    @Test
    void deleteSpecShouldRemoveAfterValidation() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(40L);
        when(iProductSpecService.getById(40L)).thenReturn(spec(40L, 30L, "容量", "[]"));

        service.deleteSpec(infoBo);

        verify(iProductSpecService).removeById(40L);

        when(iProductSpecService.getById(40L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.deleteSpec(infoBo));
    }

    @Test
    void infoSpecShouldParseAttributesAndHandleNullModelBrand() {
        AdminProductIdBo infoBo = new AdminProductIdBo();
        infoBo.setId(40L);

        ProductSpec invalidJson = spec(40L, 30L, "容量", "{bad");
        when(iProductSpecService.getById(40L)).thenReturn(invalidJson);
        when(iProductModelService.getById(30L)).thenReturn(null);
        AdminProductSpecVo invalidVo = service.infoSpec(infoBo);
        assertTrue(invalidVo.getAttributes().isEmpty());
        assertNull(invalidVo.getModelName());
        assertNull(invalidVo.getBrandName());

        ProductSpec blankJson = spec(42L, 30L, "容量", "");
        infoBo.setId(42L);
        when(iProductSpecService.getById(42L)).thenReturn(blankJson);
        assertTrue(service.infoSpec(infoBo).getAttributes().isEmpty());

        ProductSpec emptyArray = spec(43L, 30L, "容量", "[]");
        infoBo.setId(43L);
        when(iProductSpecService.getById(43L)).thenReturn(emptyArray);
        assertTrue(service.infoSpec(infoBo).getAttributes().isEmpty());

        ProductSpec validJson = spec(41L, 30L, "颜色", "[{\"id\":\"x1\",\"attName\":\"黑\"}]");
        infoBo.setId(41L);
        when(iProductSpecService.getById(41L)).thenReturn(validJson);
        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));

        AdminProductSpecVo validVo = service.infoSpec(infoBo);
        assertEquals(1, validVo.getAttributes().size());
        assertEquals("41", validVo.getAttributes().get(0).getSpecId());
        assertEquals("BCD-500", validVo.getModelName());
        assertEquals("海尔", validVo.getBrandName());

        when(iProductSpecService.getById(41L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoSpec(infoBo));
    }

    @Test
    void updateSpecShouldFailWhenSpecOrModelMissing() {
        AdminProductSpecUpdateBo updateBo = new AdminProductSpecUpdateBo();
        updateBo.setId(40L);
        updateBo.setModelId(30L);
        updateBo.setAttributeName("颜色");
        when(iProductSpecService.getById(40L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateSpec(updateBo));

        when(iProductSpecService.getById(40L)).thenReturn(spec(40L, 30L, "容量", "[]"));
        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateSpec(updateBo));
    }

    @Test
    void saveSpecShouldFailWhenModelMissing() {
        AdminProductSpecSaveBo saveBo = new AdminProductSpecSaveBo();
        saveBo.setModelId(30L);
        saveBo.setAttributeName("容量");
        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.saveSpec(saveBo));
    }

    @Test
    void pageSpecShouldHandleModelBrandNullChains() {
        AdminProductSpecPageBo pageBo = new AdminProductSpecPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setModelId(30L);
        pageBo.setAttributeName("容");

        ProductSpec spec = spec(40L, 30L, "容量", "[]");
        Page<ProductSpec> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(spec));
        when(iProductSpecService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        when(iProductModelService.getById(30L)).thenReturn(null);
        assertNull(service.pageSpec(pageBo).getRecords().get(0).getModelName());

        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertEquals("BCD-500", service.pageSpec(pageBo).getRecords().get(0).getModelName());
        assertNull(service.pageSpec(pageBo).getRecords().get(0).getBrandName());
    }

    // ── loadSpecTemplate ──────────────────────────────────────────────────────

    @Test
    void loadSpecTemplateApplianceShouldParseSectionsWithoutFields() {
        stubTemplateChain(30L, "appliance", "冰箱");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        String configJson = "{"
                + "\"damageOptions\":[\"无损伤\"],"
                + "\"conditionOptions\":[\"良好\"],"
                + "\"ageOptions\":[\"1年内\"]"
                + "}";

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("appliance_spec_冰箱")).thenReturn(configJson);
            AdminProductSpecTemplateVo vo = service.loadSpecTemplate(templateBo);
            assertEquals(3, vo.getSections().size());
            assertEquals("damage", vo.getSections().get(0).getKey());
            assertEquals("condition", vo.getSections().get(1).getKey());
            assertEquals("age", vo.getSections().get(2).getKey());
        }
    }

    @Test
    void loadSpecTemplateApplianceShouldParseEmptyFieldsArray() {
        stubTemplateChain(30L, "appliance", "冰箱");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("appliance_spec_冰箱"))
                    .thenReturn("{\"fields\":[],\"damageOptions\":[\"无损伤\"]}");
            AdminProductSpecTemplateVo vo = service.loadSpecTemplate(templateBo);
            assertEquals(1, vo.getSections().size());
            assertEquals("damage", vo.getSections().get(0).getKey());
        }
    }

    @Test
    void loadSpecTemplateApplianceShouldUseConfigValueAndParseSections() {
        stubTemplateChain(30L, "appliance", "冰箱");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        String configJson = "{"
                + "\"fields\":["
                + "null,"
                + "{\"key\":\"type\",\"label\":\"\",\"options\":[\"挂机\",\" \",\"柜机\"]},"
                + "{\"key\":\"cap\",\"options\":[\"1匹\"]}"
                + "],"
                + "\"damageOptions\":[\"无损伤\"],"
                + "\"conditionOptions\":[],"
                + "\"ageOptions\":null"
                + "}";

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("appliance_spec_冰箱")).thenReturn(configJson);
            AdminProductSpecTemplateVo vo = service.loadSpecTemplate(templateBo);

            assertEquals("appliance", vo.getRecycleCategoryCode());
            assertEquals("冰箱", vo.getCategoryName());
            assertEquals("appliance_spec_冰箱", vo.getConfigKey());
            assertEquals(3, vo.getSections().size());
            assertEquals("type", vo.getSections().get(0).getLabel());
            assertEquals(Arrays.asList("挂机", "柜机"), vo.getSections().get(0).getOptions());
            assertEquals("cap", vo.getSections().get(1).getKey());
            assertEquals("damage", vo.getSections().get(2).getKey());
            assertEquals("外观损伤", vo.getSections().get(2).getLabel());
        }
    }

    @Test
    void loadSpecTemplateApplianceShouldFallbackOnBlankOrError() {
        stubTemplateChain(30L, "APPLIANCE", "空调");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("appliance_spec_空调")).thenReturn("  ");
            AdminProductSpecTemplateVo blank = service.loadSpecTemplate(templateBo);
            assertFalse(blank.getSections().isEmpty());
            assertEquals("appliance_spec_空调", blank.getConfigKey());

            config.when(() -> ConfigUtil.getConfigValueOrDefault("appliance_spec_空调"))
                    .thenThrow(new RuntimeException("redis down"));
            AdminProductSpecTemplateVo fallback = service.loadSpecTemplate(templateBo);
            assertFalse(fallback.getSections().isEmpty());
        }
    }

    @Test
    void loadSpecTemplateDigitalShouldUseConfigAndParseSections() {
        stubTemplateChain(30L, "digital", "手机");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        String configJson = "{"
                + "\"sections\":["
                + "null,"
                + "{\"key\":\"storage\",\"label\":\"\",\"options\":[\"128G\",\"\"]},"
                + "{\"key\":\"empty\",\"options\":[]},"
                + "{\"key\":\"noopt\",\"label\":\"无选项\"}"
                + "]"
                + "}";

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("digital_spec_digital")).thenReturn(configJson);
            AdminProductSpecTemplateVo vo = service.loadSpecTemplate(templateBo);

            assertEquals("digital_spec_digital", vo.getConfigKey());
            assertEquals(3, vo.getSections().size());
            assertEquals("storage", vo.getSections().get(0).getLabel());
            assertEquals(Collections.singletonList("128G"), vo.getSections().get(0).getOptions());
            assertTrue(vo.getSections().get(1).getOptions().isEmpty());
            assertTrue(vo.getSections().get(2).getOptions().isEmpty());
        }
    }

    @Test
    void loadSpecTemplateDigitalShouldHandleNullSectionsAndFallback() {
        stubTemplateChain(30L, "DIGITAL", "平板");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("digital_spec_digital")).thenReturn("{}");
            assertTrue(service.loadSpecTemplate(templateBo).getSections().isEmpty());

            config.when(() -> ConfigUtil.getConfigValueOrDefault("digital_spec_digital")).thenReturn(null);
            assertFalse(service.loadSpecTemplate(templateBo).getSections().isEmpty());

            config.when(() -> ConfigUtil.getConfigValueOrDefault("digital_spec_digital"))
                    .thenThrow(new RuntimeException("config error"));
            assertFalse(service.loadSpecTemplate(templateBo).getSections().isEmpty());
        }
    }

    @Test
    void loadSpecTemplateLuxuryShouldUseLuxuryConfigKey() {
        stubTemplateChain(30L, "luxury", "箱包");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("digital_spec_luxury")).thenReturn("{\"sections\":[]}");
            AdminProductSpecTemplateVo vo = service.loadSpecTemplate(templateBo);

            assertEquals("luxury", vo.getRecycleCategoryCode());
            assertEquals("digital_spec_luxury", vo.getConfigKey());
            assertTrue(vo.getSections().isEmpty());
        }
    }

    @Test
    void loadSpecTemplateUnknownRecycleCodeShouldReturnEmptySections() {
        stubTemplateChain(30L, null, "书籍");
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);

        AdminProductSpecTemplateVo vo = service.loadSpecTemplate(templateBo);

        assertNull(vo.getConfigKey());
        assertTrue(vo.getSections().isEmpty());
        assertEquals("书籍", vo.getCategoryName());
    }

    @Test
    void loadSpecTemplateShouldFailWhenChainEntityMissing() {
        AdminProductSpecTemplateBo templateBo = new AdminProductSpecTemplateBo();
        templateBo.setModelId(30L);
        when(iProductModelService.getById(30L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.loadSpecTemplate(templateBo));

        when(iProductModelService.getById(30L)).thenReturn(model(30L, 20L, "BCD-500"));
        when(iProductBrandService.getById(20L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.loadSpecTemplate(templateBo));

        when(iProductBrandService.getById(20L)).thenReturn(brand(20L, 10L, "海尔"));
        when(iProductCategoryService.getById(10L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.loadSpecTemplate(templateBo));

        when(iProductCategoryService.getById(10L)).thenReturn(category(10L, 100L, "冰箱"));
        when(iRecycleCategoryService.getById(100L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.loadSpecTemplate(templateBo));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void stubTemplateChain(Long modelId, String recycleCode, String categoryName) {
        RecycleCategory recycle = recycleCategory(100L, recycleCode, "回收品类");
        ProductCategory category = category(10L, 100L, categoryName);
        ProductBrand brand = brand(20L, 10L, "海尔");
        ProductModel model = model(modelId, 20L, "BCD-500");
        when(iProductModelService.getById(modelId)).thenReturn(model);
        when(iProductBrandService.getById(20L)).thenReturn(brand);
        when(iProductCategoryService.getById(10L)).thenReturn(category);
        when(iRecycleCategoryService.getById(100L)).thenReturn(recycle);
    }

    private List<ProductSpecAttributeVo> buildMixedAttributes() {
        List<ProductSpecAttributeVo> list = new ArrayList<>();
        list.add(null);
        ProductSpecAttributeVo blankName = new ProductSpecAttributeVo();
        blankName.setAttName("  ");
        list.add(blankName);
        ProductSpecAttributeVo first = new ProductSpecAttributeVo();
        first.setAttName("  大容量  ");
        list.add(first);
        ProductSpecAttributeVo second = new ProductSpecAttributeVo();
        second.setId("custom");
        second.setAttName("已有ID");
        second.setAttPrice("99");
        second.setImg("img.png");
        list.add(second);
        return list;
    }

    private RecycleCategory recycleCategory(Long id, String code, String name) {
        RecycleCategory entity = new RecycleCategory();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);
        return entity;
    }

    private ProductCategory category(Long id, Long recycleCategoryId, String name) {
        ProductCategory entity = new ProductCategory();
        entity.setId(id);
        entity.setRecycleCategoryId(recycleCategoryId);
        entity.setName(name);
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    private ProductBrand brand(Long id, Long categoryId, String name) {
        ProductBrand entity = new ProductBrand();
        entity.setId(id);
        entity.setCategoryId(categoryId);
        entity.setName(name);
        return entity;
    }

    private ProductModel model(Long id, Long brandId, String name) {
        ProductModel entity = new ProductModel();
        entity.setId(id);
        entity.setBrandId(brandId);
        entity.setName(name);
        entity.setBasePrice(BigDecimal.TEN);
        entity.setMinPrice(BigDecimal.TEN);
        entity.setMaxPrice(BigDecimal.TEN);
        return entity;
    }

    private ProductSpec spec(Long id, Long modelId, String attributeName, String attributesJson) {
        ProductSpec entity = new ProductSpec();
        entity.setId(id);
        entity.setModelId(modelId);
        entity.setAttributeName(attributeName);
        entity.setAttributesJson(attributesJson);
        entity.setMultiSelect(0);
        entity.setSort(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }
}
