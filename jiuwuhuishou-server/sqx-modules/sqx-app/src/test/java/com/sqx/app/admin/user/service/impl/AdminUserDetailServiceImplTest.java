package com.sqx.app.admin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.address.domain.vo.UserAddressVo;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressListBo;
import com.sqx.app.admin.user.domain.bo.AdminUserSignRecordPageBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareInfoBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareUpdateCoinBo;
import com.sqx.app.welfare.db.service.IUserSignRecordService;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.entity.UserWelfare;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminUserDetailServiceImplTest {

    @Mock
    private IUserAddressService iUserAddressService;
    @Mock
    private IUserWelfareService iUserWelfareService;
    @Mock
    private IUserSignRecordService iUserSignRecordService;

    @InjectMocks
    private AdminUserDetailServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserAddress.class, UserWelfare.class, UserSignRecord.class);
    }

    @Test
    void listAddressesShouldReturnEmptyWhenNoData() {
        AdminUserAddressListBo listBo = new AdminUserAddressListBo();
        listBo.setUserId(1L);
        when(iUserAddressService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<UserAddressVo> result = service.listAddresses(listBo);

        assertTrue(result.isEmpty());
    }

    @Test
    void listAddressesShouldMapRecords() {
        AdminUserAddressListBo listBo = new AdminUserAddressListBo();
        listBo.setUserId(2L);

        UserAddress address = new UserAddress();
        address.setId(5L);
        address.setUserId(2L);
        address.setContactName("李四");
        when(iUserAddressService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(address));

        List<UserAddressVo> result = service.listAddresses(listBo);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
        assertEquals("李四", result.get(0).getContactName());
    }

    @Test
    void welfareInfoShouldUseExistingRecordWithContinuousSignEnabled() {
        AdminUserWelfareInfoBo infoBo = new AdminUserWelfareInfoBo();
        infoBo.setUserId(3L);

        UserWelfare existing = new UserWelfare();
        existing.setUserId(3L);
        existing.setCoinBalance(null);
        existing.setContinuousSignDays(null);
        existing.setContinuousSignEnabled(1);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        WelfareSummaryVo vo = service.welfareInfo(infoBo);

        assertEquals(0, vo.getCoinBalance());
        assertEquals(0, vo.getContinuousSignDays());
        assertTrue(vo.getContinuousSignEnabled());
        assertFalse(vo.getSignedToday());
        verify(iUserWelfareService, never()).save(any(UserWelfare.class));
    }

    @Test
    void welfareInfoShouldCreateWhenMissing() {
        AdminUserWelfareInfoBo infoBo = new AdminUserWelfareInfoBo();
        infoBo.setUserId(4L);

        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iUserWelfareService.save(any(UserWelfare.class))).thenAnswer(invocation -> {
            UserWelfare saved = invocation.getArgument(0);
            saved.setId(99L);
            return true;
        });

        WelfareSummaryVo vo = service.welfareInfo(infoBo);

        assertEquals(0, vo.getCoinBalance());
        assertFalse(vo.getContinuousSignEnabled());
        verify(iUserWelfareService).save(any(UserWelfare.class));
    }

    @Test
    void welfareInfoShouldReportContinuousSignDisabled() {
        AdminUserWelfareInfoBo infoBo = new AdminUserWelfareInfoBo();
        infoBo.setUserId(8L);

        UserWelfare existing = new UserWelfare();
        existing.setUserId(8L);
        existing.setCoinBalance(10);
        existing.setContinuousSignDays(2);
        existing.setContinuousSignEnabled(0);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        WelfareSummaryVo vo = service.welfareInfo(infoBo);

        assertFalse(vo.getContinuousSignEnabled());
        assertEquals(10, vo.getCoinBalance());
    }

    @Test
    void updateWelfareCoinShouldRejectInvalidAmount() {
        AdminUserWelfareUpdateCoinBo nullAmount = new AdminUserWelfareUpdateCoinBo();
        nullAmount.setUserId(1L);
        nullAmount.setAmount(null);
        nullAmount.setOperator("incr");
        assertThrows(BizException.class, () -> service.updateWelfareCoin(nullAmount));

        AdminUserWelfareUpdateCoinBo zeroAmount = new AdminUserWelfareUpdateCoinBo();
        zeroAmount.setUserId(1L);
        zeroAmount.setAmount(0);
        zeroAmount.setOperator("incr");
        assertThrows(BizException.class, () -> service.updateWelfareCoin(zeroAmount));
    }

    @Test
    void updateWelfareCoinShouldDecreaseWithoutGoingBelowZero() {
        AdminUserWelfareUpdateCoinBo updateBo = new AdminUserWelfareUpdateCoinBo();
        updateBo.setUserId(5L);
        updateBo.setAmount(30);
        updateBo.setOperator("decr");

        UserWelfare existing = new UserWelfare();
        existing.setId(1L);
        existing.setUserId(5L);
        existing.setCoinBalance(20);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        service.updateWelfareCoin(updateBo);

        ArgumentCaptor<UserWelfare> captor = ArgumentCaptor.forClass(UserWelfare.class);
        verify(iUserWelfareService).updateById(captor.capture());
        assertEquals(0, captor.getValue().getCoinBalance());
    }

    @Test
    void updateWelfareCoinShouldIncreaseBalance() {
        AdminUserWelfareUpdateCoinBo updateBo = new AdminUserWelfareUpdateCoinBo();
        updateBo.setUserId(6L);
        updateBo.setAmount(15);
        updateBo.setOperator("INCR");

        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iUserWelfareService.save(any(UserWelfare.class))).thenReturn(true);

        service.updateWelfareCoin(updateBo);

        ArgumentCaptor<UserWelfare> captor = ArgumentCaptor.forClass(UserWelfare.class);
        verify(iUserWelfareService).updateById(captor.capture());
        assertEquals(15, captor.getValue().getCoinBalance());
    }

    @Test
    void signRecordPageShouldDelegateToService() {
        AdminUserSignRecordPageBo pageBo = new AdminUserSignRecordPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(20L);
        pageBo.setUserId(7L);

        Page<UserSignRecord> page = new Page<>(1, 20);
        page.setRecords(Collections.singletonList(new UserSignRecord()));
        when(iUserSignRecordService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        IPage<UserSignRecord> result = service.signRecordPage(pageBo);

        assertEquals(1, result.getRecords().size());
    }
}
