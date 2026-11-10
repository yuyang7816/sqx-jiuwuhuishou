package com.sqx.app.base.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sqx.app.base.auth.domain.bo.*;
import com.sqx.app.base.auth.domain.vo.LoginVO;
import com.sqx.app.base.auth.domain.vo.WxLoginVo;
import com.sqx.app.base.auth.support.AppAuthTokenSupport;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.base.enums.AppError;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.base.utils.RequestUtil;
import com.sqx.app.base.utils.SysConfigUtil;
import com.sqx.app.base.utils.wx.WxJsLoginRes;
import com.sqx.app.base.utils.wx.WxUserPhone;
import com.sqx.app.base.utils.wx.WxUtil;
import com.sqx.app.base.verifyCode.service.VerifyCodeService;
import com.sqx.app.referral.user.db.service.IReferralUserCommissionService;
import com.sqx.app.referral.user.domain.entity.ReferralUserCommission;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.common.core.domain.Result;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.Md5Utils;
import com.sqx.common.shiro.utils.JwtUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppAuthServiceImplTest {

    private static final String PHONE = "13800138000";
    private static final String PASSWORD = "secret123";
    private static final String OPEN_ID = "wx_openid_test";
    private static final String TOKEN = "jwt-token";
    private static final long EXPIRE = 7200L;

    @Mock
    private IUserService iUserService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private VerifyCodeService verifyCodeService;
    @Mock
    private WxUtil wxUtil;
    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IReferralUserCommissionService iReferralUserCommissionService;
    @Mock
    private AppAuthTokenSupport appAuthTokenSupport;

    @InjectMocks
    private AppAuthServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserEntity.class);
    }

    @BeforeEach
    void setUp() {
        when(jwtUtil.generateToken(anyLong())).thenReturn(TOKEN);
        when(jwtUtil.getExpire()).thenReturn(EXPIRE);
        doAnswer(inv -> {
            UserEntity user = inv.getArgument(0);
            if (user.getUserId() == null) {
                user.setUserId(100L);
            }
            return true;
        }).when(iUserService).save(any(UserEntity.class));
    }

    private UserEntity activeUser() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setPhone(PHONE);
        user.setStatus(1);
        user.setPassword(Md5Utils.md5s(PASSWORD));
        return user;
    }

    @Test
    void phonePasswordLoginShouldCoverAllBranches() {
        PhonePasswordLoginBO bo = new PhonePasswordLoginBO();
        bo.setPhoneNo(PHONE);
        bo.setPassword(PASSWORD);

        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(null);
        BizException notFound = assertThrows(BizException.class, () -> service.phonePasswordLogin(bo));
        assertEquals(AppError.PHONE_NOT_REGISTER, notFound.getErrorCode());

        UserEntity disabled = activeUser();
        disabled.setStatus(2);
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(disabled);
        BizException abnormal = assertThrows(BizException.class, () -> service.phonePasswordLogin(bo));
        assertEquals(AppError.ACCOUNT_ABNORMAL, abnormal.getErrorCode());

        UserEntity badPwd = activeUser();
        badPwd.setPassword(Md5Utils.md5s("wrong"));
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(badPwd);
        BizException pwdError = assertThrows(BizException.class, () -> service.phonePasswordLogin(bo));
        assertEquals(AppError.PHONE_OR_PASSWORD_ERROR, pwdError.getErrorCode());

        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(activeUser());
        Result<LoginVO> result = service.phonePasswordLogin(bo);
        assertEquals(TOKEN, result.getData().getToken());
        assertEquals(EXPIRE, result.getData().getExpire());
        assertEquals(PHONE, result.getData().getUser().getPhone());
    }

    @Test
    void phoneCodeLoginShouldCoverAllBranches() {
        PhoneCodeLoginBO bo = new PhoneCodeLoginBO();
        bo.setPhoneNo(PHONE);
        bo.setVerifyCode("123456");

        when(verifyCodeService.verify(PHONE, "123456")).thenReturn(false);
        BizException codeError = assertThrows(BizException.class, () -> service.phoneCodeLogin(bo));
        assertEquals(AppError.VERIFY_CODE_ERROR, codeError.getErrorCode());

        when(verifyCodeService.verify(PHONE, "123456")).thenReturn(true);
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(null);
        Result<LoginVO> registerResult = service.phoneCodeLogin(bo);
        assertEquals(TOKEN, registerResult.getData().getToken());
        verify(iUserWalletService).save(any(UserWallet.class));
        verify(iReferralUserCommissionService).save(any(ReferralUserCommission.class));

        UserEntity disabled = activeUser();
        disabled.setStatus(2);
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(disabled);
        BizException abnormal = assertThrows(BizException.class, () -> service.phoneCodeLogin(bo));
        assertEquals(AppError.ACCOUNT_ABNORMAL, abnormal.getErrorCode());

        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(activeUser());
        Result<LoginVO> loginResult = service.phoneCodeLogin(bo);
        assertEquals(PHONE, loginResult.getData().getUser().getPhone());
    }

    @Test
    void phoneRegisterShouldCoverSuccessAndDuplicatePhone() {
        PhoneRegisterBO bo = new PhoneRegisterBO();
        bo.setPhoneNo(PHONE);
        bo.setCode("654321");
        bo.setPassword(PASSWORD);
        bo.setUsername("tester");
        bo.setSysPhone(1);
        bo.setPlatform("APP");
        bo.setSuperiorInvitationCode("INVITE1");

        when(verifyCodeService.verify(PHONE, "654321")).thenReturn(false);
        BizException codeError = assertThrows(BizException.class, () -> service.phoneRegister(bo));
        assertEquals(AppError.VERIFY_CODE_ERROR, codeError.getErrorCode());

        when(verifyCodeService.verify(PHONE, "654321")).thenReturn(true);
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(activeUser());
        BizException registered = assertThrows(BizException.class, () -> service.phoneRegister(bo));
        assertEquals(AppError.PHONE_REGISTERED, registered.getErrorCode());

        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(null);
        Result<LoginVO> result = service.phoneRegister(bo);
        assertEquals(TOKEN, result.getData().getToken());
        assertEquals("tester", result.getData().getUser().getUserName());
        verify(iUserService).save(argThat(u -> Md5Utils.md5s(PASSWORD).equals(u.getPassword())));
    }

    @Test
    void resetPasswordShouldCoverAllBranches() {
        ResetPasswordBO bo = new ResetPasswordBO();
        bo.setPhone(PHONE);
        bo.setCode("111111");
        bo.setPassword("newPass");

        when(verifyCodeService.verify(PHONE, "111111")).thenReturn(false);
        BizException codeError = assertThrows(BizException.class, () -> service.resetPassword(bo));
        assertEquals(AppError.VERIFY_CODE_ERROR, codeError.getErrorCode());

        when(verifyCodeService.verify(PHONE, "111111")).thenReturn(true);
        when(iUserService.exists(any(QueryWrapper.class))).thenReturn(false);
        BizException notFound = assertThrows(BizException.class, () -> service.resetPassword(bo));
        assertEquals(AppError.PHONE_NOT_REGISTER, notFound.getErrorCode());

        when(iUserService.exists(any(QueryWrapper.class))).thenReturn(true);
        when(iUserService.update(any(UserEntity.class), any(UpdateWrapper.class))).thenReturn(true);
        Result<Void> result = service.resetPassword(bo);
        assertNotNull(result);
        verify(iUserService).update(eq(new UserEntity()), any(UpdateWrapper.class));
    }

    @Test
    void wxLoginShouldCoverUnbindAndBind() {
        WxJsLoginRes wxRes = new WxJsLoginRes();
        wxRes.setOpenid(OPEN_ID);
        wxRes.setSessionKey("session-key");
        wxRes.setUnionid("union-id");
        wxRes.setErrmsg("ok");
        when(wxUtil.wxJsLogin("app-id", "app-secret", "wx-code")).thenReturn(wxRes);

        try (MockedStatic<SysConfigUtil> config = mockStatic(SysConfigUtil.class)) {
            config.when(() -> SysConfigUtil.getConfigValue(SysConfigConstant.Sys.SYS_USER_MA_APPID)).thenReturn("app-id");
            config.when(() -> SysConfigUtil.getConfigValue(SysConfigConstant.Sys.SYS_USER_MA_SECRET)).thenReturn("app-secret");

            when(iUserService.getByOpenId(OPEN_ID)).thenReturn(null);
            Result<WxLoginVo> unbind = service.wxLogin("wx-code");
            assertFalse(unbind.getData().getBindPhone());
            assertNull(unbind.getData().getLoginVO());

            UserEntity noPhone = activeUser();
            noPhone.setPhone("");
            when(iUserService.getByOpenId(OPEN_ID)).thenReturn(noPhone);
            Result<WxLoginVo> blankPhone = service.wxLogin("wx-code");
            assertFalse(blankPhone.getData().getBindPhone());

            when(iUserService.getByOpenId(OPEN_ID)).thenReturn(activeUser());
            Result<WxLoginVo> bind = service.wxLogin("wx-code");
            assertTrue(bind.getData().getBindPhone());
            assertEquals(TOKEN, bind.getData().getLoginVO().getToken());
            assertEquals(PHONE, bind.getData().getUserEntity().getPhone());
        }
    }

    @Test
    void wxJsRegisterShouldCoverAllBranches() {
        WxJsRegisterBo bo = new WxJsRegisterBo();
        bo.setOpenId(OPEN_ID);
        bo.setPhone(PHONE);
        bo.setUsername("wx-user");
        bo.setAvatar("avatar.png");
        bo.setSuperiorInvitationCode("SUPER1");

        UserEntity existingOpenIdNoPhone = new UserEntity();
        existingOpenIdNoPhone.setUserId(2L);
        existingOpenIdNoPhone.setMaOpenId(OPEN_ID);
        existingOpenIdNoPhone.setPhone(null);
        when(iUserService.getByOpenId(OPEN_ID)).thenReturn(existingOpenIdNoPhone);
        when(iUserService.updateById(any(UserEntity.class))).thenReturn(true);
        Result<LoginVO> bindPhone = service.wxJsRegister(bo);
        assertEquals(PHONE, bindPhone.getData().getUser().getPhone());
        verify(iUserService).updateById(argThat(u -> PHONE.equals(u.getPhone())));

        UserEntity existingOpenIdWithPhone = activeUser();
        existingOpenIdWithPhone.setMaOpenId(OPEN_ID);
        when(iUserService.getByOpenId(OPEN_ID)).thenReturn(existingOpenIdWithPhone);
        Result<LoginVO> existingOpenIdLogin = service.wxJsRegister(bo);
        assertEquals(1L, existingOpenIdLogin.getData().getUser().getUserId());

        clearInvocations(iUserService);
        when(iUserService.getByOpenId(OPEN_ID)).thenReturn(null);
        UserEntity existingPhoneNoMaOpenId = activeUser();
        existingPhoneNoMaOpenId.setMaOpenId(null);
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(existingPhoneNoMaOpenId);
        Result<LoginVO> bindOpenId = service.wxJsRegister(bo);
        assertEquals(PHONE, bindOpenId.getData().getUser().getPhone());
        verify(iUserService).updateById(argThat(u -> OPEN_ID.equals(u.getMaOpenId())));

        UserEntity existingPhoneWithMaOpenId = activeUser();
        existingPhoneWithMaOpenId.setMaOpenId("already-bound");
        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(existingPhoneWithMaOpenId);
        Result<LoginVO> existingPhoneLogin = service.wxJsRegister(bo);
        assertEquals(1L, existingPhoneLogin.getData().getUser().getUserId());

        when(iUserService.getOne(any(QueryWrapper.class))).thenReturn(null);
        Result<LoginVO> newUser = service.wxJsRegister(bo);
        assertEquals(TOKEN, newUser.getData().getToken());
        verify(iUserService).save(argThat(u -> OPEN_ID.equals(u.getMaOpenId())
                && "mp".equals(u.getPlatform())
                && "wx-user".equals(u.getUserName())));
        verify(iUserWalletService, atLeastOnce()).save(any(UserWallet.class));
        verify(iReferralUserCommissionService, atLeastOnce()).save(any(ReferralUserCommission.class));
    }

    @Test
    void decryptPhoneShouldReturnParsedJson() {
        DecryptPhoneBo bo = new DecryptPhoneBo();
        bo.setDecryptData("cipher");
        bo.setKey("key");
        bo.setIv("iv");
        when(wxUtil.decryptS5("cipher", "key", "iv")).thenReturn("{\"phoneNumber\":\"13800138000\"}");

        Result<Object> result = service.decryptPhone(bo);
        assertNotNull(result.getData());
    }

    @Test
    void getWxUserPhoneNumberShouldDelegateToWxUtil() {
        WxUserPhone phone = new WxUserPhone();
        phone.setPhoneNumber(PHONE);
        when(wxUtil.getUserPhoneNumber("phone-code", OPEN_ID, "MA")).thenReturn(phone);

        try (MockedStatic<RequestUtil> request = mockStatic(RequestUtil.class)) {
            request.when(RequestUtil::getClientType).thenReturn("MA");
            WxUserPhone result = service.getWxUserPhoneNumber("phone-code", OPEN_ID);
            assertEquals(PHONE, result.getPhoneNumber());
        }
    }

    @Test
    void logoutShouldInvalidateToken() {
        Result<Void> result = service.logout(TOKEN);
        assertNotNull(result);
        verify(appAuthTokenSupport).invalidateToken(TOKEN);
    }
}
