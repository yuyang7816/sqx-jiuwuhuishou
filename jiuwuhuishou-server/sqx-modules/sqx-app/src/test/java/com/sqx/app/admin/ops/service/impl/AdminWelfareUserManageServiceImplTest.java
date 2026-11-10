package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareUserPageBo;
import com.sqx.app.admin.ops.domain.vo.AdminWelfareUserVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.domain.entity.UserWelfare;
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
class AdminWelfareUserManageServiceImplTest {

    @Mock
    private IUserWelfareService iUserWelfareService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private AdminWelfareUserManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserWelfare.class);
    }

    @Test
    void pageInfoShouldShortCircuitAndMap() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        AdminWelfareUserPageBo pageBo = new AdminWelfareUserPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
        verify(iUserWelfareService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<UserWelfare> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iUserWelfareService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        UserWelfare uw = new UserWelfare();
        uw.setId(1L);
        uw.setUserId(9L);
        page.setRecords(Collections.singletonList(uw));
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        verify(adminLookupSupport).fillUserFields(eq(9L), any(AdminWelfareUserVo.class));
    }
}
