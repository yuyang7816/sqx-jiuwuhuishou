package com.sqx.app.admin.recycle.appliance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.appliance.domain.bo.*;
import com.sqx.app.admin.recycle.appliance.domain.vo.AdminApplianceModelVo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.appliance.db.service.IApplianceCategoryService;
import com.sqx.app.recycle.appliance.db.service.IApplianceHeroFlowService;
import com.sqx.app.recycle.appliance.db.service.IApplianceModelService;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceCategory;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceModel;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminApplianceManageServiceImplTest {

    @Mock
    private IApplianceCategoryService iApplianceCategoryService;
    @Mock
    private IApplianceModelService iApplianceModelService;
    @Mock
    private IApplianceHeroFlowService iApplianceHeroFlowService;

    @InjectMocks
    private AdminApplianceManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(ApplianceCategory.class, ApplianceModel.class, ApplianceHeroFlow.class);
    }

    @Test
    void saveCategoryShouldApplyDefaultsWhenNull() {
        AdminApplianceCategorySaveBo saveBo = new AdminApplianceCategorySaveBo();
        saveBo.setName("冰箱");
        service.saveCategory(saveBo);

        ArgumentCaptor<ApplianceCategory> captor = ArgumentCaptor.forClass(ApplianceCategory.class);
        verify(iApplianceCategoryService).save(captor.capture());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void saveCategoryShouldKeepNonNullSortAndState() {
        AdminApplianceCategorySaveBo saveBo = new AdminApplianceCategorySaveBo();
        saveBo.setName("洗衣机");
        saveBo.setSort(5);
        saveBo.setState(2);
        service.saveCategory(saveBo);

        ArgumentCaptor<ApplianceCategory> captor = ArgumentCaptor.forClass(ApplianceCategory.class);
        verify(iApplianceCategoryService).save(captor.capture());
        assertEquals(5, captor.getValue().getSort());
        assertEquals(2, captor.getValue().getState());
    }

    @Test
    void pageCategoryShouldQueryWithFilters() {
        AdminApplianceCategoryPageBo pageBo = new AdminApplianceCategoryPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("冰箱");
        pageBo.setState(1);

        Page<ApplianceCategory> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new ApplianceCategory()));
        when(iApplianceCategoryService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, service.pageCategory(pageBo).getRecords().size());
        verify(iApplianceCategoryService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void infoCategoryShouldThrowWhenMissing() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iApplianceCategoryService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoCategory(infoBo));
    }

    @Test
    void infoCategoryShouldReturnEntity() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        ApplianceCategory category = new ApplianceCategory();
        category.setId(1L);
        category.setName("冰箱");
        when(iApplianceCategoryService.getById(1L)).thenReturn(category);
        assertEquals("冰箱", service.infoCategory(infoBo).getName());
    }

    @Test
    void updateCategoryShouldVerifyExistsThenUpdate() {
        AdminApplianceCategoryUpdateBo updateBo = new AdminApplianceCategoryUpdateBo();
        updateBo.setId(1L);
        updateBo.setName("新名称");
        ApplianceCategory existing = new ApplianceCategory();
        existing.setId(1L);
        when(iApplianceCategoryService.getById(1L)).thenReturn(existing);

        service.updateCategory(updateBo);
        verify(iApplianceCategoryService).updateById(any(ApplianceCategory.class));
    }

    @Test
    void deleteCategoryShouldRejectWhenModelsExist() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iApplianceModelService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        assertThrows(BizException.class, () -> service.deleteCategory(infoBo));
        verify(iApplianceCategoryService, never()).removeById(anyLong());
    }

    @Test
    void deleteCategoryShouldSucceedWhenNoModels() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iApplianceModelService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        service.deleteCategory(infoBo);
        verify(iApplianceCategoryService).removeById(1L);
    }

    @Test
    void saveModelShouldRejectMissingCategory() {
        AdminApplianceModelSaveBo saveBo = new AdminApplianceModelSaveBo();
        saveBo.setCategoryId(9L);
        saveBo.setName("型号A");
        when(iApplianceCategoryService.getById(9L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.saveModel(saveBo));
    }

    @Test
    void saveModelShouldApplyStateDefaultWhenNull() {
        AdminApplianceModelSaveBo saveBo = new AdminApplianceModelSaveBo();
        saveBo.setCategoryId(1L);
        saveBo.setName("型号A");
        when(iApplianceCategoryService.getById(1L)).thenReturn(new ApplianceCategory());

        service.saveModel(saveBo);

        ArgumentCaptor<ApplianceModel> captor = ArgumentCaptor.forClass(ApplianceModel.class);
        verify(iApplianceModelService).save(captor.capture());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void saveModelShouldKeepNonNullState() {
        AdminApplianceModelSaveBo saveBo = new AdminApplianceModelSaveBo();
        saveBo.setCategoryId(1L);
        saveBo.setName("型号B");
        saveBo.setState(2);
        when(iApplianceCategoryService.getById(1L)).thenReturn(new ApplianceCategory());

        service.saveModel(saveBo);

        ArgumentCaptor<ApplianceModel> captor = ArgumentCaptor.forClass(ApplianceModel.class);
        verify(iApplianceModelService).save(captor.capture());
        assertEquals(2, captor.getValue().getState());
    }

    @Test
    void pageModelShouldReturnEmptyWhenNoRecords() {
        AdminApplianceModelPageBo pageBo = new AdminApplianceModelPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        Page<ApplianceModel> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iApplianceModelService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertTrue(service.pageModel(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageModelShouldMapVoWithAndWithoutCategory() {
        AdminApplianceModelPageBo pageBo = new AdminApplianceModelPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setCategoryId(1L);
        pageBo.setName("型号");
        pageBo.setState(1);

        ApplianceModel withCategory = new ApplianceModel();
        withCategory.setId(1L);
        withCategory.setCategoryId(10L);
        withCategory.setName("型号A");

        ApplianceModel orphan = new ApplianceModel();
        orphan.setId(2L);
        orphan.setCategoryId(99L);
        orphan.setName("型号B");

        Page<ApplianceModel> page = new Page<>(1, 10);
        page.setRecords(java.util.Arrays.asList(withCategory, orphan));
        when(iApplianceModelService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        ApplianceCategory category = new ApplianceCategory();
        category.setId(10L);
        category.setName("冰箱");
        when(iApplianceCategoryService.getById(10L)).thenReturn(category);
        when(iApplianceCategoryService.getById(99L)).thenReturn(null);

        java.util.List<AdminApplianceModelVo> records = service.pageModel(pageBo).getRecords();
        assertEquals(2, records.size());
        assertEquals("冰箱", records.get(0).getCategoryName());
        assertNull(records.get(1).getCategoryName());
    }

    @Test
    void infoModelShouldThrowWhenMissing() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iApplianceModelService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoModel(infoBo));
    }

    @Test
    void infoModelShouldReturnVoWithCategoryName() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        ApplianceModel model = new ApplianceModel();
        model.setId(1L);
        model.setCategoryId(10L);
        model.setName("型号A");
        when(iApplianceModelService.getById(1L)).thenReturn(model);
        ApplianceCategory category = new ApplianceCategory();
        category.setId(10L);
        category.setName("冰箱");
        when(iApplianceCategoryService.getById(10L)).thenReturn(category);

        AdminApplianceModelVo vo = service.infoModel(infoBo);
        assertEquals("型号A", vo.getName());
        assertEquals("冰箱", vo.getCategoryName());
    }

    @Test
    void updateModelShouldVerifyModelAndCategory() {
        AdminApplianceModelUpdateBo updateBo = new AdminApplianceModelUpdateBo();
        updateBo.setId(1L);
        updateBo.setCategoryId(2L);
        updateBo.setName("新型号");

        ApplianceModel existing = new ApplianceModel();
        existing.setId(1L);
        existing.setCategoryId(1L);
        when(iApplianceModelService.getById(1L)).thenReturn(existing);
        when(iApplianceCategoryService.getById(2L)).thenReturn(new ApplianceCategory());

        service.updateModel(updateBo);
        verify(iApplianceModelService).updateById(any(ApplianceModel.class));
    }

    @Test
    void deleteModelShouldRemoveById() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(3L);
        service.deleteModel(infoBo);
        verify(iApplianceModelService).removeById(3L);
    }

    @Test
    void saveHeroFlowShouldFillNullDefaults() {
        AdminApplianceHeroFlowSaveBo saveBo = new AdminApplianceHeroFlowSaveBo();
        saveBo.setName("线上预约");
        service.saveHeroFlow(saveBo);

        ArgumentCaptor<ApplianceHeroFlow> captor = ArgumentCaptor.forClass(ApplianceHeroFlow.class);
        verify(iApplianceHeroFlowService).save(captor.capture());
        assertEquals("", captor.getValue().getIcon());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void saveHeroFlowShouldKeepNonNullDefaults() {
        AdminApplianceHeroFlowSaveBo saveBo = new AdminApplianceHeroFlowSaveBo();
        saveBo.setName("上门回收");
        saveBo.setIcon("icon.png");
        saveBo.setSort(3);
        saveBo.setState(2);
        service.saveHeroFlow(saveBo);

        ArgumentCaptor<ApplianceHeroFlow> captor = ArgumentCaptor.forClass(ApplianceHeroFlow.class);
        verify(iApplianceHeroFlowService).save(captor.capture());
        assertEquals("icon.png", captor.getValue().getIcon());
        assertEquals(3, captor.getValue().getSort());
        assertEquals(2, captor.getValue().getState());
    }

    @Test
    void pageHeroFlowShouldQueryWithFilters() {
        AdminApplianceHeroFlowPageBo pageBo = new AdminApplianceHeroFlowPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("预约");
        pageBo.setState(1);

        Page<ApplianceHeroFlow> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new ApplianceHeroFlow()));
        when(iApplianceHeroFlowService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, service.pageHeroFlow(pageBo).getRecords().size());
    }

    @Test
    void infoHeroFlowShouldThrowWhenMissing() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iApplianceHeroFlowService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoHeroFlow(infoBo));
    }

    @Test
    void infoHeroFlowShouldReturnEntity() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        ApplianceHeroFlow flow = new ApplianceHeroFlow();
        flow.setId(1L);
        flow.setName("步骤1");
        when(iApplianceHeroFlowService.getById(1L)).thenReturn(flow);
        assertEquals("步骤1", service.infoHeroFlow(infoBo).getName());
    }

    @Test
    void updateHeroFlowShouldVerifyExistsAndFillDefaults() {
        AdminApplianceHeroFlowUpdateBo updateBo = new AdminApplianceHeroFlowUpdateBo();
        updateBo.setId(1L);
        updateBo.setName("新步骤");

        ApplianceHeroFlow existing = new ApplianceHeroFlow();
        existing.setId(1L);
        when(iApplianceHeroFlowService.getById(1L)).thenReturn(existing);

        service.updateHeroFlow(updateBo);

        ArgumentCaptor<ApplianceHeroFlow> captor = ArgumentCaptor.forClass(ApplianceHeroFlow.class);
        verify(iApplianceHeroFlowService).updateById(captor.capture());
        assertEquals("", captor.getValue().getIcon());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());
    }

    @Test
    void deleteHeroFlowShouldRemoveById() {
        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(5L);
        service.deleteHeroFlow(infoBo);
        verify(iApplianceHeroFlowService).removeById(5L);
    }
}
