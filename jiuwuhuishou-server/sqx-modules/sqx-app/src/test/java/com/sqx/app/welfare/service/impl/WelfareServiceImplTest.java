package com.sqx.app.welfare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.appConfig.domain.bo.SysConfigInfoBO;
import com.sqx.app.base.appConfig.domain.vo.SysConfigVO;
import com.sqx.app.base.appConfig.service.AppConfigService;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.constants.ReferralConstant;
import com.sqx.app.referral.user.db.service.IReferralUserCommissionService;
import com.sqx.app.referral.user.domain.entity.ReferralUserCommission;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.welfare.db.service.IUserSignRecordService;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.db.service.IWelfareTaskService;
import com.sqx.app.welfare.domain.bo.WelfareCoinLogPageBo;
import com.sqx.app.welfare.domain.bo.WelfareContinuousSignBo;
import com.sqx.app.welfare.domain.bo.WelfareWithdrawBo;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.entity.UserWelfare;
import com.sqx.app.welfare.domain.entity.WelfareTask;
import com.sqx.app.welfare.domain.vo.WelfareCoinLogVo;
import com.sqx.app.welfare.domain.vo.WelfareSignResultVo;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
import com.sqx.app.welfare.domain.vo.WelfareWithdrawResultVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WelfareServiceImplTest {

    @Mock
    private IUserWelfareService iUserWelfareService;
    @Mock
    private IUserSignRecordService iUserSignRecordService;
    @Mock
    private IWelfareTaskService iWelfareTaskService;
    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Mock
    private AppConfigService appConfigService;
    @Mock
    private IReferralUserCommissionService iReferralUserCommissionService;
    @Mock
    private IReferralCommissionService iReferralCommissionService;

    @InjectMocks
    private WelfareServiceImpl welfareService;

    private final Map<String, String> configValues = new HashMap<>();

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserWelfare.class, WelfareTask.class, UserWallet.class, UserSignRecord.class);
    }

    @BeforeEach
    void setUp() {
        configValues.clear();
        configValues.put("welfare_sign_daily_coin", "5");
        configValues.put("welfare_sign_total_days", "7");
        configValues.put("welfare_withdraw_min_amount", "10");
        configValues.put("welfare_coin_wallet_rate", "1");
        configValues.put(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_COIN_RATE, "1");
        when(appConfigService.info(any(SysConfigInfoBO.class))).thenAnswer(inv -> {
            SysConfigInfoBO bo = inv.getArgument(0);
            SysConfigVO vo = new SysConfigVO();
            vo.setValue(configValues.get(bo.getKey()));
            return vo;
        });
    }

    @Test
    void summaryShouldCreateWelfareWhenMissing() {
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iUserWelfareService.save(any(UserWelfare.class))).thenAnswer(inv -> {
            UserWelfare uw = inv.getArgument(0);
            uw.setId(1L);
            return true;
        });

        WelfareSummaryVo vo = welfareService.summary(9L);
        assertEquals(0, vo.getCoinBalance());
        assertEquals(0, vo.getContinuousSignDays());
        assertTrue(vo.getContinuousSignEnabled());
        assertFalse(vo.getSignedToday());
        verify(iUserWelfareService).save(any(UserWelfare.class));
    }

    @Test
    void summaryShouldReflectSignedTodayAndDisabledContinuous() {
        UserWelfare uw = baseWelfare(20, 3);
        uw.setContinuousSignEnabled(0);
        uw.setLastSignDate(LocalDate.now());
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);

        WelfareSummaryVo vo = welfareService.summary(9L);
        assertEquals(20, vo.getCoinBalance());
        assertEquals(3, vo.getContinuousSignDays());
        assertFalse(vo.getContinuousSignEnabled());
        assertTrue(vo.getSignedToday());
    }

    @Test
    void listTasksShouldHandleEmptyAndMapped() {
        when(iWelfareTaskService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(welfareService.listTasks().isEmpty());

        WelfareTask task = new WelfareTask();
        task.setId(1L);
        task.setTitle("invite");
        task.setState(1);
        when(iWelfareTaskService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(task));
        assertEquals(1, welfareService.listTasks().size());
        assertEquals("invite", welfareService.listTasks().get(0).getTitle());
    }

    @Test
    void signShouldRejectDuplicate() {
        UserWelfare uw = baseWelfare(0, 1);
        uw.setLastSignDate(LocalDate.now());
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);
        assertThrows(BizException.class, () -> welfareService.sign(9L));
    }

    @Test
    void signShouldStartContinuousFromScratch() {
        UserWelfare uw = baseWelfare(null, null);
        uw.setContinuousSignEnabled(1);
        uw.setLastSignDate(null);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);

        WelfareSignResultVo result = welfareService.sign(9L);
        assertEquals(5, result.getCoinReward());
        assertEquals(1, result.getContinuousSignDays());
        assertEquals(5, result.getCoinBalance());
        verify(iUserSignRecordService).save(any(UserSignRecord.class));
        verify(iUserWelfareService).updateById(uw);
    }

    @Test
    void signShouldContinueStreakAndResetWhenExceedTotalDays() {
        UserWelfare uw = baseWelfare(10, 7);
        uw.setContinuousSignEnabled(1);
        uw.setLastSignDate(LocalDate.now().minusDays(1));
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);

        WelfareSignResultVo result = welfareService.sign(9L);
        assertEquals(1, result.getContinuousSignDays());
        assertEquals(15, result.getCoinBalance());
    }

    @Test
    void signShouldBreakStreakAndSkipWhenContinuousDisabled() {
        UserWelfare broken = baseWelfare(0, 4);
        broken.setContinuousSignEnabled(1);
        broken.setLastSignDate(LocalDate.now().minusDays(3));
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(broken);
        assertEquals(1, welfareService.sign(9L).getContinuousSignDays());

        UserWelfare disabled = baseWelfare(0, 4);
        disabled.setContinuousSignEnabled(0);
        disabled.setLastSignDate(LocalDate.now().minusDays(1));
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(disabled);
        assertEquals(0, welfareService.sign(9L).getContinuousSignDays());
    }

    @Test
    void signShouldUseConfigDefaultsWhenBlankOrInvalid() {
        configValues.put("welfare_sign_daily_coin", " ");
        configValues.put("welfare_sign_total_days", "abc");
        UserWelfare uw = baseWelfare(0, 0);
        uw.setLastSignDate(null);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);

        WelfareSignResultVo result = welfareService.sign(9L);
        assertEquals(5, result.getCoinReward());
        assertEquals(1, result.getContinuousSignDays());
    }

    @Test
    void updateContinuousSignEnabledShouldToggle() {
        UserWelfare uw = baseWelfare(0, 5);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);

        WelfareContinuousSignBo disableBo = new WelfareContinuousSignBo();
        disableBo.setEnabled(false);
        WelfareSummaryVo disabled = welfareService.updateContinuousSignEnabled(9L, disableBo);
        assertEquals(0, uw.getContinuousSignDays());
        assertFalse(disabled.getContinuousSignEnabled());

        WelfareContinuousSignBo enableBo = new WelfareContinuousSignBo();
        enableBo.setEnabled(true);
        WelfareSummaryVo enabled = welfareService.updateContinuousSignEnabled(9L, enableBo);
        assertTrue(enabled.getContinuousSignEnabled());
        verify(iUserWelfareService, times(2)).updateById(uw);
    }

    @Test
    void withdrawShouldValidateAmount() {
        WelfareWithdrawBo bo = new WelfareWithdrawBo();
        assertThrows(BizException.class, () -> welfareService.withdraw(9L, bo));

        bo.setAmount(BigDecimal.ZERO);
        assertThrows(BizException.class, () -> welfareService.withdraw(9L, bo));

        bo.setAmount(new BigDecimal("10.5"));
        assertThrows(BizException.class, () -> welfareService.withdraw(9L, bo));

        bo.setAmount(new BigDecimal("5"));
        assertThrows(BizException.class, () -> welfareService.withdraw(9L, bo));
    }

    @Test
    void withdrawShouldRejectInsufficientBalance() {
        UserWelfare uw = baseWelfare(8, 0);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);
        WelfareWithdrawBo bo = new WelfareWithdrawBo();
        bo.setAmount(new BigDecimal("10"));
        assertThrows(BizException.class, () -> welfareService.withdraw(9L, bo));
    }

    @Test
    void withdrawShouldCreditWalletAndSyncReferral() {
        UserWelfare uw = baseWelfare(100, 0);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);

        ReferralUserCommission commission = new ReferralUserCommission();
        commission.setUserId(9L);
        commission.setCommission(new BigDecimal("30"));
        when(iReferralUserCommissionService.getByUserId(9L)).thenReturn(commission);

        UserWallet wallet = new UserWallet();
        wallet.setUserId(9L);
        wallet.setAmount(new BigDecimal("1"));
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);

        WelfareWithdrawBo bo = new WelfareWithdrawBo();
        bo.setAmount(new BigDecimal("20"));
        WelfareWithdrawResultVo result = welfareService.withdraw(9L, bo);

        assertEquals(20, result.getWithdrawAmount());
        assertEquals(80, result.getCoinBalance());
        assertEquals(0, new BigDecimal("21").compareTo(result.getWalletAmount()));
        verify(iReferralUserCommissionService).decr(eq(9L), argThat(amount ->
                amount != null && amount.compareTo(new BigDecimal("20")) == 0));
        verify(iReferralCommissionService).saveWithdraw(eq(9L), argThat(amount ->
                amount != null && amount.compareTo(new BigDecimal("20")) == 0));
        verify(iUserWalletService).incr(eq(9L), argThat(amount ->
                amount != null && amount.compareTo(new BigDecimal("20")) == 0));
        verify(iUserWalletTransactionService).save(argThat(tx ->
                Objects.equals(tx.getUserId(), 9L)
                        && tx.getAmount().compareTo(new BigDecimal("20")) == 0
                        && Objects.equals(tx.getDirection(), 1)
                        && Objects.equals(tx.getStatus(), 2)
                        && "环保积分提现到账".equals(tx.getTitle())
        ));
    }

    @Test
    void withdrawShouldApplyCoinWalletRate() {
        configValues.put("welfare_coin_wallet_rate", "0.5");
        UserWelfare uw = baseWelfare(100, 0);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);
        when(iReferralUserCommissionService.getByUserId(9L)).thenReturn(null);

        UserWallet wallet = new UserWallet();
        wallet.setUserId(9L);
        wallet.setAmount(new BigDecimal("10"));
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);

        WelfareWithdrawBo bo = new WelfareWithdrawBo();
        bo.setAmount(new BigDecimal("20"));
        WelfareWithdrawResultVo result = welfareService.withdraw(9L, bo);

        assertEquals(20, result.getWithdrawAmount());
        assertEquals(80, result.getCoinBalance());
        assertEquals(0, new BigDecimal("20").compareTo(result.getWalletAmount()));
        verify(iUserWalletService).incr(eq(9L), eq(new BigDecimal("10.00")));
        verify(iUserWalletTransactionService).save(argThat(tx ->
                tx.getAmount().compareTo(new BigDecimal("10.00")) == 0
        ));
    }

    @Test
    void withdrawShouldSkipReferralWhenMissingOrZeroBalanceAndCreateWallet() {
        UserWelfare uw = baseWelfare(50, 0);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);
        when(iReferralUserCommissionService.getByUserId(9L)).thenReturn(null);
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iUserWalletService.save(any(UserWallet.class))).thenReturn(true);

        WelfareWithdrawBo bo = new WelfareWithdrawBo();
        bo.setAmount(new BigDecimal("10"));
        WelfareWithdrawResultVo result = welfareService.withdraw(9L, bo);
        assertEquals(10, result.getWithdrawAmount());
        assertEquals(0, BigDecimal.TEN.compareTo(result.getWalletAmount()));
        verify(iReferralUserCommissionService, never()).decr(anyLong(), any());
        verify(iUserWalletService).save(any(UserWallet.class));

        ReferralUserCommission zero = new ReferralUserCommission();
        zero.setCommission(BigDecimal.ZERO);
        when(iReferralUserCommissionService.getByUserId(9L)).thenReturn(zero);
        UserWallet wallet = new UserWallet();
        wallet.setAmount(null);
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);
        welfareService.withdraw(9L, bo);
        verify(iReferralUserCommissionService, never()).decr(anyLong(), any());
    }

    @Test
    void incrAndDecrReferralCoinShouldCoverRateBranches() {
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(iUserWelfareService.save(any(UserWelfare.class))).thenAnswer(inv -> {
            UserWelfare uw = inv.getArgument(0);
            uw.setId(1L);
            return true;
        });

        welfareService.incrReferralCoin(9L, null);
        welfareService.incrReferralCoin(9L, BigDecimal.ZERO);
        verify(iUserWelfareService, never()).updateById(any());

        UserWelfare uw = baseWelfare(10, 0);
        when(iUserWelfareService.getOne(any(LambdaQueryWrapper.class))).thenReturn(uw);
        welfareService.incrReferralCoin(9L, new BigDecimal("2.4"));
        assertEquals(12, uw.getCoinBalance());

        configValues.put(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_COIN_RATE, "0");
        welfareService.incrReferralCoin(9L, new BigDecimal("1"));
        assertEquals(13, uw.getCoinBalance());

        configValues.put(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_COIN_RATE, "bad");
        welfareService.incrReferralCoin(9L, new BigDecimal("1"));
        assertEquals(14, uw.getCoinBalance());

        configValues.put(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_COIN_RATE, "2");
        welfareService.decrReferralCoin(9L, null);
        welfareService.decrReferralCoin(9L, BigDecimal.ZERO);
        welfareService.decrReferralCoin(9L, new BigDecimal("100"));
        assertEquals(0, uw.getCoinBalance());
    }

    @Test
    void coinLogPageShouldMapSignRecords() {
        UserSignRecord record = new UserSignRecord();
        record.setId(3L);
        record.setUserId(9L);
        record.setSignDate(LocalDate.of(2026, 9, 29));
        record.setCoinReward(5);
        Page<UserSignRecord> page = new Page<>(1, 20);
        page.setRecords(Collections.singletonList(record));
        page.setTotal(1);
        when(iUserSignRecordService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        WelfareCoinLogPageBo pageBo = new WelfareCoinLogPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(20L);
        IPage<WelfareCoinLogVo> result = welfareService.coinLogPage(9L, pageBo);

        assertEquals(1, result.getRecords().size());
        WelfareCoinLogVo vo = result.getRecords().get(0);
        assertEquals(3L, vo.getId());
        assertEquals("每日签到", vo.getTitle());
        assertEquals(5, vo.getCoinAmount());
        assertEquals(1, vo.getType());
    }

    private static UserWelfare baseWelfare(Integer coins, Integer days) {
        UserWelfare uw = new UserWelfare();
        uw.setId(1L);
        uw.setUserId(9L);
        uw.setCoinBalance(coins);
        uw.setContinuousSignDays(days);
        uw.setContinuousSignEnabled(1);
        return uw;
    }
}
