package com.sqx.app.referral.withdraw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.referral.constants.ReferralConstant;
import com.sqx.app.referral.withdraw.db.service.IWithdrawRecordService;
import com.sqx.app.referral.withdraw.db.service.IWithdrawalAccountService;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordConfirmBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordInfoBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordPageBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordSaveBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordUpdateBo;
import com.sqx.app.referral.withdraw.domain.entity.WithdrawRecord;
import com.sqx.app.referral.withdraw.domain.entity.WithdrawalAccount;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.common.core.exception.BizException;
import com.sqx.system.db.service.ISysConfigService;
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

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WithdrawRecordServiceImplTest {

    @Mock
    private IWithdrawRecordService iWithdrawRecordService;
    @Mock
    private ISysConfigService iSysConfigService;
    @Mock
    private IUserService iUserService;
    @Mock
    private IWithdrawalAccountService iWithdrawalAccountService;
    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private WithdrawRecordServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(WithdrawRecord.class, WithdrawalAccount.class, UserWallet.class, UserWalletTransaction.class);
    }

    @Test
    void saveInfoShouldValidateAndSucceed() {
        WithdrawRecordSaveBo bo = new WithdrawRecordSaveBo();
        assertThrows(BizException.class, () -> service.saveInfo(bo));
        bo.setUserId(1L);
        bo.setAmount(BigDecimal.ZERO);
        assertThrows(BizException.class, () -> service.saveInfo(bo));

        bo.setAmount(new BigDecimal("50"));
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        doAnswer(inv -> {
            UserWallet w = inv.getArgument(0);
            w.setId(10L);
            return true;
        }).when(iUserWalletService).save(any(UserWallet.class));
        assertThrows(BizException.class, () -> service.saveInfo(bo)); // balance 0

        UserWallet wallet = new UserWallet();
        wallet.setUserId(1L);
        wallet.setAmount(null);
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);
        assertThrows(BizException.class, () -> service.saveInfo(bo));

        wallet.setAmount(new BigDecimal("100"));
        bo.setAmount(new BigDecimal("90"));
        when(iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MAX)).thenReturn("80");
        when(iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MIN)).thenReturn("10");
        when(iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_RATE)).thenReturn("0.1");
        assertThrows(BizException.class, () -> service.saveInfo(bo));

        when(iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MAX)).thenReturn("200");
        when(iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MIN)).thenReturn("95");
        bo.setAmount(new BigDecimal("50"));
        assertThrows(BizException.class, () -> service.saveInfo(bo));

        when(iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MIN)).thenReturn("10");
        doAnswer(inv -> {
            WithdrawRecord r = inv.getArgument(0);
            r.setId(99L);
            return true;
        }).when(iWithdrawRecordService).save(any(WithdrawRecord.class));
        doThrow(new RuntimeException("tx")).when(iUserWalletTransactionService).save(any(UserWalletTransaction.class));

        service.saveInfo(bo);
        verify(iUserWalletService).decr(1L, new BigDecimal("50"));
        ArgumentCaptor<WithdrawRecord> captor = ArgumentCaptor.forClass(WithdrawRecord.class);
        verify(iWithdrawRecordService).save(captor.capture());
        assertEquals(0, new BigDecimal("45").compareTo(captor.getValue().getActualAmount()));
        assertEquals(Integer.valueOf(1), captor.getValue().getStatus());
    }

    @Test
    void pageInfoAndInfoAndCrud() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        WithdrawRecordPageBo pageBo = new WithdrawRecordPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<WithdrawRecord> page = new Page<>(1, 10);
        WithdrawRecord record = new WithdrawRecord();
        record.setUserId(1L);
        record.setAccountType(2);
        page.setRecords(Collections.singletonList(record));
        when(iWithdrawRecordService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        when(iUserService.getById(1L)).thenReturn(null);
        when(iWithdrawalAccountService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        pageBo.setUserId(1L);
        pageBo.setStatus(1);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        when(iUserService.getById(1L)).thenReturn(user);
        WithdrawalAccount account = new WithdrawalAccount();
        when(iWithdrawalAccountService.getOne(any(LambdaQueryWrapper.class))).thenReturn(account);
        assertNotNull(service.pageInfo(pageBo).getRecords().get(0).getWithdrawalAccount());

        page.setRecords(Collections.emptyList());
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        WithdrawRecordInfoBo infoBo = new WithdrawRecordInfoBo();
        infoBo.setId(1L);
        when(iWithdrawRecordService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
        when(iWithdrawRecordService.getById(1L)).thenReturn(record);
        when(iUserService.getById(1L)).thenReturn(null);
        assertNull(service.info(infoBo).getUserEntity());
        when(iUserService.getById(1L)).thenReturn(user);
        assertNotNull(service.info(infoBo).getUserEntity());

        service.updateInfo(new WithdrawRecordUpdateBo());
        service.delete(infoBo);
        verify(iWithdrawRecordService).removeById(1L);
    }

    @Test
    void confirmShouldCoverSuccessRejectAndIllegal() {
        WithdrawRecordConfirmBo bo = new WithdrawRecordConfirmBo();
        bo.setId(1L);
        when(iWithdrawRecordService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.confirm(bo));

        WithdrawRecord record = new WithdrawRecord();
        record.setId(1L);
        record.setUserId(9L);
        record.setAmount(new BigDecimal("20"));
        when(iWithdrawRecordService.getById(1L)).thenReturn(record);

        bo.setStatus(2);
        service.confirm(bo);
        assertEquals(Integer.valueOf(2), record.getStatus());
        assertNotNull(record.getWithdrawTime());

        bo.setStatus(3);
        bo.setReason("资料不全");
        service.confirm(bo);
        assertEquals(Integer.valueOf(3), record.getStatus());
        verify(iUserWalletService).incr(9L, new BigDecimal("20"));
        verify(iUserWalletTransactionService).save(any(UserWalletTransaction.class));

        bo.setStatus(9);
        assertThrows(BizException.class, () -> service.confirm(bo));
    }
}
