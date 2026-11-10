package com.sqx.app.referral.commission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionInfoBo;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionPageBo;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionSaveBo;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionUpdateBo;
import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReferralCommissionServiceImplTest {

    @Mock
    private IReferralCommissionService iReferralCommissionService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private ReferralCommissionServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(ReferralCommission.class);
    }

    @Test
    void pageInfoAndCrud() {
        when(adminLookupSupport.resolveUserIds("u", "p")).thenReturn(Collections.emptyList());
        ReferralCommissionPageBo pageBo = new ReferralCommissionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("u");
        pageBo.setPhone("p");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds("NO")).thenReturn(Collections.emptyList());
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        pageBo.setOrderNo("NO");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        Page<ReferralCommission> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iReferralCommissionService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        pageBo.setOrderNo(null);
        pageBo.setUserId(1L);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        ReferralCommission record = new ReferralCommission();
        record.setUserId(1L);
        record.setOrderId(9L);
        page.setRecords(Collections.singletonList(record));
        when(adminLookupSupport.getUser(1L)).thenReturn(null);
        when(adminLookupSupport.resolveOrderNo(9L)).thenReturn("R9");
        assertEquals("R9", service.pageInfo(pageBo).getRecords().get(0).getOrderNo());

        UserEntity user = new UserEntity();
        user.setUserName("张三");
        user.setPhone("138");
        when(adminLookupSupport.getUser(1L)).thenReturn(user);
        assertEquals("张三", service.pageInfo(pageBo).getRecords().get(0).getUserName());

        service.saveInfo(new ReferralCommissionSaveBo());
        verify(iReferralCommissionService).save(any(ReferralCommission.class));
        ReferralCommissionUpdateBo updateBo = new ReferralCommissionUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        ReferralCommissionInfoBo infoBo = new ReferralCommissionInfoBo();
        infoBo.setId(1L);
        when(iReferralCommissionService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
        when(iReferralCommissionService.getById(1L)).thenReturn(record);
        assertNotNull(service.info(infoBo));
        service.delete(infoBo);
        verify(iReferralCommissionService).removeById(1L);
    }
}
