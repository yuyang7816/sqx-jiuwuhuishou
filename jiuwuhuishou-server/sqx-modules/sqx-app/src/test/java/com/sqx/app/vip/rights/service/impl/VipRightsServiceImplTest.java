package com.sqx.app.vip.rights.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.vip.rights.db.service.IVipRightsService;
import com.sqx.app.vip.rights.domain.bo.VipRightsInfoBo;
import com.sqx.app.vip.rights.domain.bo.VipRightsPageBo;
import com.sqx.app.vip.rights.domain.bo.VipRightsSaveBo;
import com.sqx.app.vip.rights.domain.bo.VipRightsUpdateBo;
import com.sqx.app.vip.rights.domain.entity.VipRights;
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
class VipRightsServiceImplTest {

    @Mock
    private IVipRightsService iVipRightsService;

    @InjectMocks
    private VipRightsServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(VipRights.class);
    }

    @Test
    void crudAndInfo() {
        VipRightsSaveBo saveBo = new VipRightsSaveBo();
        service.saveInfo(saveBo);
        verify(iVipRightsService).save(any(VipRights.class));

        VipRightsUpdateBo updateBo = new VipRightsUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iVipRightsService).updateById(any(VipRights.class));

        VipRightsInfoBo infoBo = new VipRightsInfoBo();
        infoBo.setId(1L);
        when(iVipRightsService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        VipRights rights = new VipRights();
        rights.setId(1L);
        when(iVipRightsService.getById(1L)).thenReturn(rights);
        assertEquals(1L, service.info(infoBo).getId());

        service.delete(infoBo);
        verify(iVipRightsService).removeById(1L);
    }

    @Test
    void pageAndListBranches() {
        VipRightsPageBo pageBo = new VipRightsPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        Page<VipRights> empty = new Page<>(1, 10);
        empty.setRecords(Collections.emptyList());
        when(iVipRightsService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(empty);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        VipRights rights = new VipRights();
        rights.setId(2L);
        empty.setRecords(Collections.singletonList(rights));
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        when(iVipRightsService.list()).thenReturn(Collections.emptyList());
        assertTrue(service.list().isEmpty());

        when(iVipRightsService.list()).thenReturn(Collections.singletonList(rights));
        assertEquals(2L, service.list().get(0).getId());
    }
}
