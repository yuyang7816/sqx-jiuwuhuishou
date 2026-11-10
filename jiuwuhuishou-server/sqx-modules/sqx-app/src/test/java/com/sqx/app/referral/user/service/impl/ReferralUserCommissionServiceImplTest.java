package com.sqx.app.referral.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.base.user.enums.UserStatus;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
import com.sqx.app.referral.commission.domain.vo.ReferralLeaderboardVo;
import com.sqx.app.referral.commission.domain.vo.ReferralListVo;
import com.sqx.app.referral.constants.ReferralConstant;
import com.sqx.app.referral.user.db.service.IReferralUserCommissionService;
import com.sqx.app.referral.user.domain.bo.ReferralCommissionWithdrawBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionInfoBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionPageBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionUpdateBo;
import com.sqx.app.referral.user.domain.entity.ReferralUserCommission;
import com.sqx.app.referral.user.domain.vo.InvitePosterVo;
import com.sqx.app.referral.user.domain.vo.ReferralCommissionWithdrawResultVo;
import com.sqx.app.referral.user.domain.vo.ReferralDataVo;
import com.sqx.app.referral.user.domain.vo.ReferralLawyerVo;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.welfare.service.WelfareService;
import com.sqx.app.base.utils.wx.WxUtil;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.shiro.utils.UserContextHolder;
import com.sqx.system.db.entity.Banner;
import com.sqx.system.db.service.IBannerService;
import com.sqx.system.db.service.ISysConfigService;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
class ReferralUserCommissionServiceImplTest {

    @Mock
    private IReferralUserCommissionService iReferralUserCommissionService;
    @Mock
    private ISysConfigService iSysConfigService;
    @Mock
    private IUserService iUserService;
    @Mock
    private IReferralCommissionService iReferralCommissionService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private WelfareService welfareService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private IBannerService iBannerService;
    @Mock
    private WxUtil wxUtil;
    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;

    @InjectMocks
    private ReferralUserCommissionServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(ReferralUserCommission.class, ReferralCommission.class,
                RecycleOrder.class, UserEntity.class, Banner.class, UserWallet.class);
    }

    @Test
    void pageInfoInfoUpdateDelete() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        ReferralUserCommissionPageBo pageBo = new ReferralUserCommissionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<ReferralUserCommission> page = new Page<>(1, 10);
        page.setRecords(null);
        when(iReferralUserCommissionService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        ReferralUserCommission row = new ReferralUserCommission();
        row.setUserId(1L);
        page.setRecords(Collections.singletonList(row));
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());

        when(iReferralUserCommissionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        doAnswer(inv -> {
            ReferralUserCommission c = inv.getArgument(0);
            c.setId(5L);
            return true;
        }).when(iReferralUserCommissionService).save(any(ReferralUserCommission.class));
        assertEquals(new BigDecimal("0"), service.info(1L).getCommission());

        ReferralUserCommission existing = new ReferralUserCommission();
        existing.setUserId(1L);
        existing.setCommission(new BigDecimal("3"));
        when(iReferralUserCommissionService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        assertEquals(new BigDecimal("3"), service.info(1L).getCommission());

        ReferralUserCommissionUpdateBo updateBo = new ReferralUserCommissionUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        ReferralUserCommissionInfoBo infoBo = new ReferralUserCommissionInfoBo();
        infoBo.setId(1L);
        service.delete(infoBo);
        verify(iReferralUserCommissionService).removeById(1L);
    }

    @Test
    void saveUserCommissionShouldSkipAllInvalidCasesThenSucceed() {
        when(iReferralCommissionService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
        service.saveUserCommission(1L);
        verify(iRecycleOrderService, never()).getById(any());

        when(iReferralCommissionService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iRecycleOrderService.getById(1L)).thenReturn(null);
        service.saveUserCommission(1L);

        RecycleOrder order = new RecycleOrder();
        order.setId(1L);
        order.setUserId(10L);
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        service.saveUserCommission(1L);

        order.setStatus(RecycleOrderStatus.COMPLETED.getCode());
        when(iUserService.getById(10L)).thenReturn(null);
        service.saveUserCommission(1L);

        UserEntity user = new UserEntity();
        user.setUserId(10L);
        user.setSuperiorInvitationCode(" ");
        when(iUserService.getById(10L)).thenReturn(user);
        service.saveUserCommission(1L);

        user.setSuperiorInvitationCode("INV");
        when(iUserService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        service.saveUserCommission(1L);

        UserEntity invite = new UserEntity();
        invite.setUserId(10L);
        invite.setStatus(1);
        when(iUserService.getOne(any(LambdaQueryWrapper.class))).thenReturn(invite);
        service.saveUserCommission(1L);

        invite.setUserId(20L);
        invite.setStatus(UserStatus.DISABLE.getCode());
        service.saveUserCommission(1L);

        invite.setStatus(1);
        order.setActualAmount(BigDecimal.ZERO);
        order.setEstimateAmount(null);
        service.saveUserCommission(1L);

        order.setEstimateAmount(new BigDecimal("100"));
        when(iSysConfigService.getConfigValue(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_RATE)).thenReturn("0");
        service.saveUserCommission(1L);

        when(iSysConfigService.getConfigValue(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_RATE)).thenReturn("");
        order.setActualAmount(new BigDecimal("50"));
        order.setOrderNo("RE1");
        service.saveUserCommission(1L);

        ArgumentCaptor<ReferralCommission> captor = ArgumentCaptor.forClass(ReferralCommission.class);
        verify(iReferralCommissionService).save(captor.capture());
        assertEquals(0, new BigDecimal("5.00").compareTo(captor.getValue().getAmount()));
        verify(iReferralUserCommissionService).incr(20L, new BigDecimal("5.00"));
        verify(welfareService).incrReferralCoin(20L, new BigDecimal("5.00"));
    }

    @Test
    void referralDataListLawyerLeaderboard() {
        when(iUserService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.referralData(1L));

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        when(iUserService.getById(1L)).thenReturn(user);
        when(iReferralCommissionService.sumUserCommission(1L)).thenReturn(null);
        ReferralDataVo blank = service.referralData(1L);
        assertEquals(0L, blank.getNum());
        assertEquals(BigDecimal.ZERO, blank.getCommission());

        user.setInvitationCode("CODE");
        when(iUserService.count(any(QueryWrapper.class))).thenReturn(3L, 1L);
        when(iReferralCommissionService.sumUserCommission(1L)).thenReturn(new BigDecimal("8"));
        ReferralDataVo data = service.referralData(1L);
        assertEquals(3L, data.getNum());
        assertEquals(1L, data.getMonthNum());
        assertEquals(new BigDecimal("8"), data.getCommission());

        when(iUserService.getById(2L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.referralUserList(2L));
        when(iUserService.getById(1L)).thenReturn(user);
        Page<UserEntity> userPage = new Page<>(1, 10);
        when(iUserService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(userPage);
        assertSame(userPage, service.referralUserList(1L));

        IPage<ReferralListVo> listPage = new Page<>();
        when(iReferralCommissionService.referralList(any(Page.class), eq(1L))).thenReturn(listPage);
        assertSame(listPage, service.referralList(1L));

        try (MockedStatic<UserContextHolder> ctx = mockStatic(UserContextHolder.class)) {
            ctx.when(UserContextHolder::getAppUserId).thenReturn(1L);
            when(iReferralCommissionService.getByUserId(1L)).thenReturn(null);
            assertThrows(BizException.class, () -> service.lawyer());

            ReferralCommission asset = new ReferralCommission();
            asset.setAmount(new BigDecimal("12"));
            when(iReferralCommissionService.getByUserId(1L)).thenReturn(asset);
            when(iReferralCommissionService.sumAmount(eq(1L), any(), any())).thenReturn(new BigDecimal("1"));
            ReferralLawyerVo lawyer = service.lawyer();
            assertEquals(new BigDecimal("12"), lawyer.getAmount());
            assertEquals(new BigDecimal("1"), lawyer.getYesterdayEarn());
        }

        when(iReferralCommissionService.leaderboard(50)).thenReturn(null);
        assertTrue(service.leaderboard().isEmpty());
        ReferralLeaderboardVo item = new ReferralLeaderboardVo();
        when(iReferralCommissionService.leaderboard(50)).thenReturn(Collections.singletonList(item));
        assertEquals(1, service.leaderboard().size());
    }

    @Test
    void invitePosterShouldUseInviteBackgroundBanner() {
        when(iUserService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.invitePoster(1L));

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserName("测试用户");
        user.setAvatar("https://cdn.example.com/a.png");
        user.setInvitationCode("ABCDEF");
        when(iUserService.getById(1L)).thenReturn(user);
        when(iBannerService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BizException.class, () -> service.invitePoster(1L));

        Banner banner = new Banner();
        banner.setImageUrl("https://cdn.example.com/invite-bg.png");
        banner.setName("邀请海报");
        banner.setDescription("一起来回收");
        when(iBannerService.getOne(any(LambdaQueryWrapper.class))).thenReturn(banner);
        when(wxUtil.getWxaCodeBase64(eq("ABCDEF"), eq("pages/index/index"), anyString()))
                .thenReturn("data:image/png;base64,xxx");

        InvitePosterVo vo = service.invitePoster(1L);
        assertEquals("https://cdn.example.com/invite-bg.png", vo.getBackgroundImageUrl());
        assertEquals("测试用户", vo.getUserName());
        assertEquals("ABCDEF", vo.getInvitationCode());
        assertEquals("pages/index/index?invitationCode=ABCDEF", vo.getInvitePath());
        assertEquals("data:image/png;base64,xxx", vo.getQrCodeImage());
        assertEquals("邀请海报", vo.getTitle());
        assertEquals("一起来回收", vo.getDescription());

        user.setInvitationCode(" ");
        when(iUserService.getById(1L)).thenReturn(user);
        assertThrows(BizException.class, () -> service.invitePoster(1L));

        user.setInvitationCode("ABCDEF");
        when(iUserService.getById(1L)).thenReturn(user);
        when(wxUtil.getWxaCodeBase64(eq("ABCDEF"), eq("pages/index/index"), anyString()))
                .thenThrow(new RuntimeException("wx down"));
        InvitePosterVo degraded = service.invitePoster(1L);
        assertNull(degraded.getQrCodeImage());
        assertEquals("ABCDEF", degraded.getInvitationCode());
    }

    @Test
    void withdrawToWalletShouldMoveCommissionIntoWallet() {
        ReferralCommissionWithdrawBo empty = new ReferralCommissionWithdrawBo();
        empty.setAmount(BigDecimal.ZERO);
        assertThrows(BizException.class, () -> service.withdrawToWallet(9L, empty));

        ReferralUserCommission commission = new ReferralUserCommission();
        commission.setUserId(9L);
        commission.setCommission(new BigDecimal("10.00"));
        when(iReferralUserCommissionService.getByUserId(9L)).thenReturn(commission);

        ReferralCommissionWithdrawBo tooMuch = new ReferralCommissionWithdrawBo();
        tooMuch.setAmount(new BigDecimal("20"));
        assertThrows(BizException.class, () -> service.withdrawToWallet(9L, tooMuch));

        UserWallet wallet = new UserWallet();
        wallet.setUserId(9L);
        wallet.setAmount(new BigDecimal("1.00"));
        when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);

        ReferralCommissionWithdrawBo ok = new ReferralCommissionWithdrawBo();
        ok.setAmount(new BigDecimal("3.5"));
        ReferralCommissionWithdrawResultVo vo = service.withdrawToWallet(9L, ok);
        assertEquals(new BigDecimal("3.50"), vo.getWithdrawAmount());
        assertEquals(new BigDecimal("6.50"), vo.getCommission());
        assertEquals(new BigDecimal("4.50"), vo.getWalletAmount());
        verify(iReferralUserCommissionService).decr(9L, new BigDecimal("3.50"));
        verify(iReferralCommissionService).saveWithdraw(9L, new BigDecimal("3.50"));
        verify(welfareService).decrReferralCoin(9L, new BigDecimal("3.50"));
        verify(iUserWalletService).incr(9L, new BigDecimal("3.50"));
        verify(iUserWalletTransactionService).save(any());
    }
}
