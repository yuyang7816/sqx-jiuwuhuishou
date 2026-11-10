package com.sqx.app.vip.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.vip.plan.db.service.IVipPlanService;
import com.sqx.app.vip.plan.domain.bo.VipPlanInfoBo;
import com.sqx.app.vip.plan.domain.bo.VipPlanPageBo;
import com.sqx.app.vip.plan.domain.bo.VipPlanSaveBo;
import com.sqx.app.vip.plan.domain.bo.VipPlanUpdateBo;
import com.sqx.app.vip.plan.domain.entity.VipPlan;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VipPlanServiceImplTest {

    @Mock
    private IVipPlanService iVipPlanService;

    @InjectMocks
    private VipPlanServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(VipPlan.class);
    }

    @Test
    void crudAndInfo() {
        VipPlanSaveBo saveBo = new VipPlanSaveBo();
        saveBo.setName("月卡");
        service.saveInfo(saveBo);
        verify(iVipPlanService).save(any(VipPlan.class));

        VipPlanUpdateBo updateBo = new VipPlanUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iVipPlanService).updateById(any(VipPlan.class));

        VipPlanInfoBo infoBo = new VipPlanInfoBo();
        infoBo.setId(1L);
        when(iVipPlanService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        VipPlan plan = new VipPlan();
        plan.setId(1L);
        plan.setName("月卡");
        when(iVipPlanService.getById(1L)).thenReturn(plan);
        assertEquals("月卡", service.info(infoBo).getName());

        service.delete(infoBo);
        verify(iVipPlanService).removeById(1L);
    }

    @Test
    void pageAndListBranches() {
        VipPlanPageBo pageBo = new VipPlanPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        Page<VipPlan> empty = new Page<>(1, 10);
        empty.setRecords(Collections.emptyList());
        when(iVipPlanService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(empty);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        VipPlan plan = new VipPlan();
        plan.setId(1L);
        plan.setName("年卡");
        empty.setRecords(Collections.singletonList(plan));
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        when(iVipPlanService.list()).thenReturn(Collections.emptyList());
        assertTrue(service.list().isEmpty());

        when(iVipPlanService.list()).thenReturn(Collections.singletonList(plan));
        assertEquals("年卡", service.list().get(0).getName());
    }
}
