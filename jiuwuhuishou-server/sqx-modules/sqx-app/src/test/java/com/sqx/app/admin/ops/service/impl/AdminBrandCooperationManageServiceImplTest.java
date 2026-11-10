package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.AdminBrandCooperationInfoBo;
import com.sqx.app.admin.ops.domain.bo.AdminBrandCooperationPageBo;
import com.sqx.app.admin.ops.domain.bo.AdminBrandCooperationUpdateStatusBo;
import com.sqx.app.brand.db.service.IBrandCooperationService;
import com.sqx.app.brand.domain.entity.BrandCooperation;
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
class AdminBrandCooperationManageServiceImplTest {

    @Mock
    private IBrandCooperationService iBrandCooperationService;

    @InjectMocks
    private AdminBrandCooperationManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(BrandCooperation.class);
    }

    @Test
    void pageAndInfo() {
        AdminBrandCooperationPageBo pageBo = new AdminBrandCooperationPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setBrandName("A");
        pageBo.setContactName("B");
        pageBo.setMobile("138");
        pageBo.setStatus(1);
        Page<BrandCooperation> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new BrandCooperation()));
        when(iBrandCooperationService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        AdminBrandCooperationInfoBo infoBo = new AdminBrandCooperationInfoBo();
        infoBo.setId(1L);
        when(iBrandCooperationService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        BrandCooperation entity = new BrandCooperation();
        entity.setId(1L);
        entity.setBrandName("品牌");
        when(iBrandCooperationService.getById(1L)).thenReturn(entity);
        assertEquals("品牌", service.info(infoBo).getBrandName());
    }

    @Test
    void updateStatusShouldValidateAndSetRemark() {
        AdminBrandCooperationUpdateStatusBo updateBo = new AdminBrandCooperationUpdateStatusBo();
        updateBo.setId(1L);
        updateBo.setStatus(2);
        when(iBrandCooperationService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateStatus(updateBo));

        BrandCooperation entity = new BrandCooperation();
        entity.setId(1L);
        when(iBrandCooperationService.getById(1L)).thenReturn(entity);
        updateBo.setRemark("  ");
        service.updateStatus(updateBo);
        assertEquals(2, entity.getStatus());
        assertNull(entity.getRemark());
        verify(iBrandCooperationService).updateById(entity);

        updateBo.setRemark("已处理");
        service.updateStatus(updateBo);
        assertEquals("已处理", entity.getRemark());
    }
}
