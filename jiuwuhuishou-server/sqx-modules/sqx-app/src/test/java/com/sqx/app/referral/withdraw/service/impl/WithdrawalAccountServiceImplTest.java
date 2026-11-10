package com.sqx.app.referral.withdraw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.referral.withdraw.db.service.IWithdrawalAccountService;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawalAccountInfoBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawalAccountPageBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawalAccountSaveBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawalAccountUpdateBo;
import com.sqx.app.referral.withdraw.domain.entity.WithdrawalAccount;
import com.sqx.app.referral.withdraw.domain.vo.WithdrawalAccountVo;
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
class WithdrawalAccountServiceImplTest {

    @Mock
    private IWithdrawalAccountService iWithdrawalAccountService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private WithdrawalAccountServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(WithdrawalAccount.class);
    }

    @Test
    void saveShouldRejectDuplicate() {
        WithdrawalAccountSaveBo saveBo = new WithdrawalAccountSaveBo();
        saveBo.setUserId(1L);
        saveBo.setType(1);
        when(iWithdrawalAccountService.exists(any(LambdaQueryWrapper.class))).thenReturn(true);
        assertThrows(BizException.class, () -> service.saveInfo(saveBo));

        when(iWithdrawalAccountService.exists(any(LambdaQueryWrapper.class))).thenReturn(false);
        service.saveInfo(saveBo);
        verify(iWithdrawalAccountService).save(any(WithdrawalAccount.class));
    }

    @Test
    void pageInfoAndCrud() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        WithdrawalAccountPageBo pageBo = new WithdrawalAccountPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<WithdrawalAccount> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iWithdrawalAccountService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        pageBo.setAccountName("张");
        pageBo.setAccountNumber("62");
        pageBo.setType(1);
        pageBo.setSource(1);
        pageBo.setUserId(9L);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        pageBo.setSource(0);
        WithdrawalAccount account = new WithdrawalAccount();
        account.setUserId(9L);
        page.setRecords(Collections.singletonList(account));
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        verify(adminLookupSupport).fillUserFields(eq(9L), any(WithdrawalAccountVo.class));

        WithdrawalAccountInfoBo infoBo = new WithdrawalAccountInfoBo();
        infoBo.setId(1L);
        when(iWithdrawalAccountService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
        when(iWithdrawalAccountService.getById(1L)).thenReturn(account);
        assertNotNull(service.info(infoBo));

        WithdrawalAccountUpdateBo updateBo = new WithdrawalAccountUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        service.delete(infoBo);
        when(iWithdrawalAccountService.getOne(any(LambdaQueryWrapper.class))).thenReturn(account);
        assertSame(account, service.getUserWithdrawalAccount(9L, 1));
    }
}
