package com.sqx.app.admin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressPageBo;
import com.sqx.app.admin.user.domain.vo.AdminUserAddressVo;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminUserAddressManageServiceImplTest {

    @Mock
    private IUserAddressService iUserAddressService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private AdminUserAddressManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserAddress.class);
    }

    @Test
    void pageInfoShouldShortCircuitWhenUserIdsEmpty() {
        AdminUserAddressPageBo pageBo = new AdminUserAddressPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("missing");
        pageBo.setPhone("13800000000");

        when(adminLookupSupport.resolveUserIds("missing", "13800000000")).thenReturn(Collections.emptyList());

        IPage<AdminUserAddressVo> result = service.pageInfo(pageBo);

        assertTrue(result.getRecords().isEmpty());
        verify(iUserAddressService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageInfoShouldApplyFiltersAndMapRecords() {
        AdminUserAddressPageBo pageBo = new AdminUserAddressPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setContactName("张三");
        pageBo.setContactMobile("139");
        pageBo.setIsDefault(1);

        List<Long> userIds = Arrays.asList(1L, 2L);
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(userIds);

        UserAddress full = new UserAddress();
        full.setId(10L);
        full.setUserId(1L);
        full.setContactName("张三");
        full.setProvince("广东省");
        full.setCity("深圳市");
        full.setDistrict("南山区");
        full.setStreet("科技园");
        full.setDetailAddress("1号");

        UserAddress partial = new UserAddress();
        partial.setId(11L);
        partial.setUserId(2L);
        partial.setProvince("北京市");
        partial.setCity("");
        partial.setDistrict(null);
        partial.setStreet("  ");
        partial.setDetailAddress("");

        UserAddress blank = new UserAddress();
        blank.setId(12L);
        blank.setUserId(3L);

        Page<UserAddress> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(full, partial, blank));
        when(iUserAddressService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        IPage<AdminUserAddressVo> result = service.pageInfo(pageBo);

        assertEquals(3, result.getRecords().size());
        assertEquals("广东省深圳市南山区科技园1号", result.getRecords().get(0).getAddressText());
        assertEquals("北京市", result.getRecords().get(1).getAddressText());
        assertEquals("", result.getRecords().get(2).getAddressText());
        verify(adminLookupSupport).fillUserFields(eq(1L), any(AdminUserAddressVo.class));
        verify(adminLookupSupport).fillUserFields(eq(2L), any(AdminUserAddressVo.class));
        verify(adminLookupSupport).fillUserFields(eq(3L), any(AdminUserAddressVo.class));
    }

    @Test
    void pageInfoShouldReturnEmptyWhenNoRecords() {
        AdminUserAddressPageBo pageBo = new AdminUserAddressPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        Page<UserAddress> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iUserAddressService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }
}
