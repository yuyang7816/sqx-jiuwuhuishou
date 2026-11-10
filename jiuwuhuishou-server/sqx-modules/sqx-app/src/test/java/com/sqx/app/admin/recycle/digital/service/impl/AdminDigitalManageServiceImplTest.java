package com.sqx.app.admin.recycle.digital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.digital.domain.bo.*;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalModelVo;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalPricingVo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.digital.db.service.IDigitalBrandService;
import com.sqx.app.recycle.digital.db.service.IDigitalModelService;
import com.sqx.app.recycle.digital.db.service.IDigitalPricingRuleService;
import com.sqx.app.recycle.digital.domain.entity.DigitalBrand;
import com.sqx.app.recycle.digital.domain.entity.DigitalModel;
import com.sqx.app.recycle.digital.domain.entity.DigitalPricingRule;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminDigitalManageServiceImplTest {

    @Mock
    private IDigitalBrandService iDigitalBrandService;
    @Mock
    private IDigitalModelService iDigitalModelService;
    @Mock
    private IDigitalPricingRuleService iDigitalPricingRuleService;

    @InjectMocks
    private AdminDigitalManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(DigitalBrand.class, DigitalModel.class, DigitalPricingRule.class);
    }

    @Test
    void saveBrandShouldApplyDefaultsWhenBlankSceneAndNullSortState() {
        AdminDigitalBrandSaveBo saveBo = new AdminDigitalBrandSaveBo();
        saveBo.setName("Apple");
        service.saveBrand(saveBo);

        ArgumentCaptor<DigitalBrand> captor = ArgumentCaptor.forClass(DigitalBrand.class);
        verify(iDigitalBrandService).save(captor.capture());
        assertEquals("digital", captor.getValue().getScene());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void saveBrandShouldKeepNonNullSceneSortAndState() {
        AdminDigitalBrandSaveBo saveBo = new AdminDigitalBrandSaveBo();
        saveBo.setScene("luxury");
        saveBo.setName("Rolex");
        saveBo.setSort(8);
        saveBo.setState(2);
        service.saveBrand(saveBo);

        ArgumentCaptor<DigitalBrand> captor = ArgumentCaptor.forClass(DigitalBrand.class);
        verify(iDigitalBrandService).save(captor.capture());
        assertEquals("luxury", captor.getValue().getScene());
        assertEquals(8, captor.getValue().getSort());
        assertEquals(2, captor.getValue().getState());
    }

    @Test
    void pageBrandShouldQueryWithFilters() {
        AdminDigitalBrandPageBo pageBo = new AdminDigitalBrandPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setScene("digital");
        pageBo.setName("Apple");
        pageBo.setState(1);

        Page<DigitalBrand> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new DigitalBrand()));
        when(iDigitalBrandService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, service.pageBrand(pageBo).getRecords().size());
    }

    @Test
    void infoBrandShouldThrowWhenMissing() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iDigitalBrandService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoBrand(infoBo));
    }

    @Test
    void infoBrandShouldReturnEntity() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        DigitalBrand brand = new DigitalBrand();
        brand.setId(1L);
        brand.setName("Apple");
        when(iDigitalBrandService.getById(1L)).thenReturn(brand);
        assertEquals("Apple", service.infoBrand(infoBo).getName());
    }

    @Test
    void updateBrandShouldVerifyExistsThenUpdate() {
        AdminDigitalBrandUpdateBo updateBo = new AdminDigitalBrandUpdateBo();
        updateBo.setId(1L);
        updateBo.setName("新品牌");
        when(iDigitalBrandService.getById(1L)).thenReturn(new DigitalBrand());

        service.updateBrand(updateBo);
        verify(iDigitalBrandService).updateById(any(DigitalBrand.class));
    }

    @Test
    void deleteBrandShouldRejectWhenModelsExist() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iDigitalModelService.count(any(LambdaQueryWrapper.class))).thenReturn(3L);
        assertThrows(BizException.class, () -> service.deleteBrand(infoBo));
        verify(iDigitalBrandService, never()).removeById(anyLong());
    }

    @Test
    void deleteBrandShouldSucceedWhenNoModels() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iDigitalModelService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        service.deleteBrand(infoBo);
        verify(iDigitalBrandService).removeById(1L);
    }

    @Test
    void saveModelShouldRejectMissingBrand() {
        AdminDigitalModelSaveBo saveBo = new AdminDigitalModelSaveBo();
        saveBo.setBrandId(9L);
        saveBo.setName("iPhone");
        when(iDigitalBrandService.getById(9L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.saveModel(saveBo));
    }

    @Test
    void saveModelShouldApplyStateDefaultWhenNull() {
        AdminDigitalModelSaveBo saveBo = new AdminDigitalModelSaveBo();
        saveBo.setBrandId(1L);
        saveBo.setName("iPhone 15");
        when(iDigitalBrandService.getById(1L)).thenReturn(new DigitalBrand());

        service.saveModel(saveBo);

        ArgumentCaptor<DigitalModel> captor = ArgumentCaptor.forClass(DigitalModel.class);
        verify(iDigitalModelService).save(captor.capture());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void saveModelShouldKeepNonNullState() {
        AdminDigitalModelSaveBo saveBo = new AdminDigitalModelSaveBo();
        saveBo.setBrandId(1L);
        saveBo.setName("iPhone 14");
        saveBo.setState(2);
        when(iDigitalBrandService.getById(1L)).thenReturn(new DigitalBrand());

        service.saveModel(saveBo);

        ArgumentCaptor<DigitalModel> captor = ArgumentCaptor.forClass(DigitalModel.class);
        verify(iDigitalModelService).save(captor.capture());
        assertEquals(2, captor.getValue().getState());
    }

    @Test
    void pageModelShouldReturnEmptyWhenNoRecords() {
        AdminDigitalModelPageBo pageBo = new AdminDigitalModelPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        Page<DigitalModel> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iDigitalModelService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertTrue(service.pageModel(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageModelShouldMapVoWithAndWithoutBrand() {
        AdminDigitalModelPageBo pageBo = new AdminDigitalModelPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setBrandId(1L);
        pageBo.setName("iPhone");
        pageBo.setState(1);

        DigitalModel withBrand = new DigitalModel();
        withBrand.setId(1L);
        withBrand.setBrandId(10L);
        withBrand.setName("iPhone 15");

        DigitalModel orphan = new DigitalModel();
        orphan.setId(2L);
        orphan.setBrandId(99L);
        orphan.setName("Orphan");

        Page<DigitalModel> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(withBrand, orphan));
        when(iDigitalModelService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        DigitalBrand brand = new DigitalBrand();
        brand.setId(10L);
        brand.setName("Apple");
        when(iDigitalBrandService.getById(10L)).thenReturn(brand);
        when(iDigitalBrandService.getById(99L)).thenReturn(null);

        java.util.List<AdminDigitalModelVo> records = service.pageModel(pageBo).getRecords();
        assertEquals(2, records.size());
        assertEquals("Apple", records.get(0).getBrandName());
        assertNull(records.get(1).getBrandName());
    }

    @Test
    void infoModelShouldThrowWhenMissing() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iDigitalModelService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoModel(infoBo));
    }

    @Test
    void infoModelShouldReturnVoWithBrandName() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        DigitalModel model = new DigitalModel();
        model.setId(1L);
        model.setBrandId(10L);
        model.setName("iPhone 15");
        when(iDigitalModelService.getById(1L)).thenReturn(model);
        DigitalBrand brand = new DigitalBrand();
        brand.setId(10L);
        brand.setName("Apple");
        when(iDigitalBrandService.getById(10L)).thenReturn(brand);

        AdminDigitalModelVo vo = service.infoModel(infoBo);
        assertEquals("iPhone 15", vo.getName());
        assertEquals("Apple", vo.getBrandName());
    }

    @Test
    void updateModelShouldVerifyModelAndBrand() {
        AdminDigitalModelUpdateBo updateBo = new AdminDigitalModelUpdateBo();
        updateBo.setId(1L);
        updateBo.setBrandId(2L);
        updateBo.setName("新型号");

        DigitalModel existing = new DigitalModel();
        existing.setId(1L);
        when(iDigitalModelService.getById(1L)).thenReturn(existing);
        when(iDigitalBrandService.getById(2L)).thenReturn(new DigitalBrand());

        service.updateModel(updateBo);
        verify(iDigitalModelService).updateById(any(DigitalModel.class));
    }

    @Test
    void deleteModelShouldRemovePricingThenModel() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(7L);

        service.deleteModel(infoBo);

        verify(iDigitalPricingRuleService).remove(any(LambdaQueryWrapper.class));
        verify(iDigitalModelService).removeById(7L);
    }

    @Test
    void savePricingShouldRejectMissingModel() {
        AdminDigitalPricingSaveBo saveBo = new AdminDigitalPricingSaveBo();
        saveBo.setModelId(9L);
        saveBo.setRuleKey("condition");
        when(iDigitalModelService.getById(9L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.savePricing(saveBo));
    }

    @Test
    void savePricingShouldSucceedWhenModelExists() {
        AdminDigitalPricingSaveBo saveBo = new AdminDigitalPricingSaveBo();
        saveBo.setModelId(1L);
        saveBo.setRuleKey("condition");
        saveBo.setRuleValue("good");
        saveBo.setAdjustRate(new BigDecimal("0.10"));
        when(iDigitalModelService.getById(1L)).thenReturn(new DigitalModel());

        service.savePricing(saveBo);
        verify(iDigitalPricingRuleService).save(any(DigitalPricingRule.class));
    }

    @Test
    void pagePricingShouldReturnEmptyWhenNoRecords() {
        AdminDigitalPricingPageBo pageBo = new AdminDigitalPricingPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        Page<DigitalPricingRule> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iDigitalPricingRuleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertTrue(service.pagePricing(pageBo).getRecords().isEmpty());
    }

    @Test
    void pagePricingShouldMapVoWithAndWithoutModel() {
        AdminDigitalPricingPageBo pageBo = new AdminDigitalPricingPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setModelId(1L);
        pageBo.setRuleKey("condition");

        DigitalPricingRule withModel = new DigitalPricingRule();
        withModel.setId(1L);
        withModel.setModelId(10L);
        withModel.setRuleKey("condition");

        DigitalPricingRule orphan = new DigitalPricingRule();
        orphan.setId(2L);
        orphan.setModelId(99L);
        orphan.setRuleKey("storage");

        Page<DigitalPricingRule> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(withModel, orphan));
        when(iDigitalPricingRuleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        DigitalModel model = new DigitalModel();
        model.setId(10L);
        model.setName("iPhone 15");
        when(iDigitalModelService.getById(10L)).thenReturn(model);
        when(iDigitalModelService.getById(99L)).thenReturn(null);

        java.util.List<AdminDigitalPricingVo> records = service.pagePricing(pageBo).getRecords();
        assertEquals(2, records.size());
        assertEquals("iPhone 15", records.get(0).getModelName());
        assertNull(records.get(1).getModelName());
    }

    @Test
    void infoPricingShouldThrowWhenMissing() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iDigitalPricingRuleService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoPricing(infoBo));
    }

    @Test
    void infoPricingShouldReturnVoWithModelName() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        DigitalPricingRule rule = new DigitalPricingRule();
        rule.setId(1L);
        rule.setModelId(10L);
        rule.setRuleKey("condition");
        when(iDigitalPricingRuleService.getById(1L)).thenReturn(rule);
        DigitalModel model = new DigitalModel();
        model.setId(10L);
        model.setName("iPhone 15");
        when(iDigitalModelService.getById(10L)).thenReturn(model);

        AdminDigitalPricingVo vo = service.infoPricing(infoBo);
        assertEquals("condition", vo.getRuleKey());
        assertEquals("iPhone 15", vo.getModelName());
    }

    @Test
    void updatePricingShouldVerifyRuleAndModel() {
        AdminDigitalPricingUpdateBo updateBo = new AdminDigitalPricingUpdateBo();
        updateBo.setId(1L);
        updateBo.setModelId(2L);
        updateBo.setRuleKey("condition");
        updateBo.setAdjustRate(new BigDecimal("-0.05"));

        DigitalPricingRule existing = new DigitalPricingRule();
        existing.setId(1L);
        when(iDigitalPricingRuleService.getById(1L)).thenReturn(existing);
        when(iDigitalModelService.getById(2L)).thenReturn(new DigitalModel());

        service.updatePricing(updateBo);
        verify(iDigitalPricingRuleService).updateById(any(DigitalPricingRule.class));
    }

    @Test
    void deletePricingShouldRemoveById() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(4L);
        service.deletePricing(infoBo);
        verify(iDigitalPricingRuleService).removeById(4L);
    }
}
