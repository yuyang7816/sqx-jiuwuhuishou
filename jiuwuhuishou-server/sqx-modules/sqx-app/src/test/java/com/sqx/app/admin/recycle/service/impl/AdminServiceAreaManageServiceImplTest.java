package com.sqx.app.admin.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.domain.bo.AdminServiceAreaInfoBo;
import com.sqx.app.admin.recycle.domain.bo.AdminServiceAreaPageBo;
import com.sqx.app.admin.recycle.domain.bo.AdminServiceAreaSaveBo;
import com.sqx.app.admin.recycle.domain.bo.AdminServiceAreaUpdateBo;
import com.sqx.app.recycle.config.db.service.IServiceAreaService;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;
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
class AdminServiceAreaManageServiceImplTest {

    @Mock
    private IServiceAreaService iServiceAreaService;

    @InjectMocks
    private AdminServiceAreaManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(ServiceArea.class);
    }

    @Test
    void crudAndPage() {
        service.saveInfo(new AdminServiceAreaSaveBo());
        verify(iServiceAreaService).save(any(ServiceArea.class));

        AdminServiceAreaUpdateBo updateBo = new AdminServiceAreaUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iServiceAreaService).updateById(any(ServiceArea.class));

        AdminServiceAreaInfoBo infoBo = new AdminServiceAreaInfoBo();
        infoBo.setId(1L);
        when(iServiceAreaService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        ServiceArea entity = new ServiceArea();
        entity.setId(1L);
        entity.setCity("西安");
        when(iServiceAreaService.getById(1L)).thenReturn(entity);
        assertEquals("西安", service.info(infoBo).getCity());

        service.delete(infoBo);
        verify(iServiceAreaService).removeById(1L);

        AdminServiceAreaPageBo pageBo = new AdminServiceAreaPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setProvince("陕");
        pageBo.setCity("西");
        pageBo.setDistrict("雁");
        pageBo.setState(1);
        Page<ServiceArea> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(entity));
        when(iServiceAreaService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }
}
