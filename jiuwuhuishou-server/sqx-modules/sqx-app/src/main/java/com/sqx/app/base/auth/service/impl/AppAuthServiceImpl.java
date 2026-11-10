package com.sqx.app.base.auth.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sqx.app.base.auth.domain.bo.*;
import com.sqx.app.base.auth.domain.vo.LoginVO;
import com.sqx.app.base.auth.domain.vo.WxLoginVo;
import com.sqx.app.base.auth.service.AppAuthService;
import com.sqx.app.base.auth.support.AppAuthTokenSupport;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.base.enums.AppError;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.base.user.enums.UserStatus;
import com.sqx.app.base.user.enums.UserType;
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
import com.sqx.common.core.utils.GenUtil;
import com.sqx.common.core.utils.Md5Utils;
import com.sqx.common.shiro.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author chengming
 * @since 2025/4/22
 */
@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Resource
    private IUserService iUserService;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private WxUtil wxUtil;
    @Resource
    private IUserWalletService iUserWalletService;
    @Resource
    private IReferralUserCommissionService iReferralUserCommissionService;
    @Resource
    private AppAuthTokenSupport appAuthTokenSupport;

    @Override
    public Result<LoginVO> phonePasswordLogin(PhonePasswordLoginBO phonePasswordLoginBO) {
        UserEntity userEntity = getUserByPhone(phonePasswordLoginBO.getPhoneNo());
        if (Objects.isNull(userEntity)) {
            throw new BizException(AppError.PHONE_NOT_REGISTER);
        }

        if (1 != userEntity.getStatus()) {
            throw new BizException(AppError.ACCOUNT_ABNORMAL);
        }

        String encryptPassword = Md5Utils.md5s(phonePasswordLoginBO.getPassword());

        if (!Objects.equals(encryptPassword, userEntity.getPassword())) {
            throw new BizException(AppError.PHONE_OR_PASSWORD_ERROR);
        }

        LoginVO vo = buildLoginResult(userEntity);
        return Result.success(vo);
    }

    @Override
    public Result<LoginVO> phoneCodeLogin(PhoneCodeLoginBO phoneCodeLoginBO) {
        // 校验验证码
        if (!verifyCodeService.verify(phoneCodeLoginBO.getPhoneNo(), phoneCodeLoginBO.getVerifyCode())) {
            throw new BizException(AppError.VERIFY_CODE_ERROR);
        }

        UserEntity userEntity = getUserByPhone(phoneCodeLoginBO.getPhoneNo());
        if (Objects.isNull(userEntity)) {
            // 注册流程
            UserEntity userInfo = registerPhone(phoneCodeLoginBO.getPhoneNo(), null ,null,null, null, null);
            LoginVO vo = buildLoginResult(userInfo);
            return Result.success(vo);
        }

        if (1 != userEntity.getStatus()) {
            throw new BizException(AppError.ACCOUNT_ABNORMAL);
        }

        LoginVO vo = buildLoginResult(userEntity);
        return Result.success(vo);
    }

    @Override
    public Result<LoginVO> phoneRegister(PhoneRegisterBO phoneRegisterBO) {
        if (!verifyCodeService.verify(phoneRegisterBO.getPhoneNo(), phoneRegisterBO.getCode())) {
            throw new BizException(AppError.VERIFY_CODE_ERROR);
        }

        UserEntity userInfo = registerPhone(phoneRegisterBO.getPhoneNo(), phoneRegisterBO.getPassword(), phoneRegisterBO.getUsername(), phoneRegisterBO.getSysPhone(), phoneRegisterBO.getPlatform(), phoneRegisterBO.getSuperiorInvitationCode());

        LoginVO vo = buildLoginResult(userInfo);
        return Result.success(vo);
    }

    @Override
    public Result<Void> resetPassword(ResetPasswordBO resetPasswordBO) {
        if (!verifyCodeService.verify(resetPasswordBO.getPhone(), resetPasswordBO.getCode())) {
            throw new BizException(AppError.VERIFY_CODE_ERROR);
        }

        if (!checkPhoneExist(resetPasswordBO.getPhone())) {
            throw new BizException(AppError.PHONE_NOT_REGISTER);
        }

        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(UserEntity::getPhone, resetPasswordBO.getPhone())
                .set(UserEntity::getPassword, Md5Utils.md5s(resetPasswordBO.getPassword()));

        iUserService.update(new UserEntity(), updateWrapper);

        return Result.success();
    }

    private boolean checkPhoneExist(String phone) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getPhone, phone);
        return iUserService.exists(queryWrapper);
    }

    /**
     * 手机号注册
     * @param phoneNo
     * @param sysPhone
     * @param platform
     * @param superiorInvitationCode
     * @return
     */
    private UserEntity registerPhone(String phoneNo, String password, String username, Integer sysPhone, String platform, String superiorInvitationCode) {
        UserEntity userEntity = getUserByPhone(phoneNo);
        if (Objects.nonNull(userEntity)) {
            throw new BizException(AppError.PHONE_REGISTERED);
        }

        UserEntity userInfo = new UserEntity();
        userInfo.setUserName(StringUtils.isNotBlank(username) ? username : phoneNo);
        userInfo.setPhone(phoneNo);
        userInfo.setSysPhone(sysPhone);
        userInfo.setStatus(UserStatus.ENABLE.getCode());
        userInfo.setPlatform(platform);
        userInfo.setUserType(UserType.USER.getCode());
        userInfo.setIsVip(0);
        userInfo.setInvitationCode(GenUtil.genInviteCode(phoneNo));
        userInfo.setSuperiorInvitationCode(superiorInvitationCode);

        if (StringUtils.isNotBlank(password)) {
            String encryptPassword = Md5Utils.md5s(password);
            userInfo.setPassword(encryptPassword);
        }

        iUserService.save(userInfo);

        /* 其他业务 */
        registerBiz(userInfo.getUserId());

        return userInfo;
    }

    private void registerBiz(Long userId) {
        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(userId);
        userWallet.setAmount(BigDecimal.ZERO);
        iUserWalletService.save(userWallet);

        ReferralUserCommission referralUserCommission = new ReferralUserCommission();
        referralUserCommission.setUserId(userId);
        referralUserCommission.setCommission(BigDecimal.ZERO);
        iReferralUserCommissionService.save(referralUserCommission);
    }

    @Override
    public Result<WxLoginVo> wxLogin(String code) {
        String appId = SysConfigUtil.getConfigValue(SysConfigConstant.Sys.SYS_USER_MA_APPID);
        String secret = SysConfigUtil.getConfigValue(SysConfigConstant.Sys.SYS_USER_MA_SECRET);

        WxJsLoginRes wxJsLoginRes = wxUtil.wxJsLogin(appId, secret, code);

        WxLoginVo vo = new WxLoginVo();
        vo.setSessionKey(wxJsLoginRes.getSessionKey());
        vo.setUnionid(wxJsLoginRes.getUnionid());
        vo.setErrmsg(wxJsLoginRes.getErrmsg());
        vo.setOpenid(wxJsLoginRes.getOpenid());
        vo.setErrmsg(wxJsLoginRes.getErrmsg());

        // 是否绑定手机号
        UserEntity userEntity = iUserService.getByOpenId(wxJsLoginRes.getOpenid());
        if (Objects.isNull(userEntity) || StringUtils.isBlank(userEntity.getPhone())) {
            vo.setBindPhone(false);
        } else {
            LoginVO loginVO = buildLoginResult(userEntity);
            vo.setLoginVO(loginVO);
            vo.setUserEntity(userEntity);
            vo.setBindPhone(true);
        }

        return Result.success(vo);
    }

    @Override
    public Result<LoginVO> wxJsRegister(WxJsRegisterBo wxJsRegisterBo) {
        UserEntity userEntity = iUserService.getByOpenId(wxJsRegisterBo.getOpenId());
        if (Objects.nonNull(userEntity)) {
            if (StringUtils.isBlank(userEntity.getPhone())) {
                userEntity.setPhone(wxJsRegisterBo.getPhone());
                iUserService.updateById(userEntity);
            }

            // 已注册过, 直接登录
            LoginVO vo = buildLoginResult(userEntity);
            return Result.success(vo);
        }

        UserEntity userByPhone = getUserByPhone(wxJsRegisterBo.getPhone());
        if (Objects.nonNull(userByPhone)) {

            if (StringUtils.isBlank(userByPhone.getMaOpenId())) {
                userByPhone.setMaOpenId(wxJsRegisterBo.getOpenId());
                iUserService.updateById(userByPhone);
            }

            // 已注册过, 直接登录
            LoginVO vo = buildLoginResult(userByPhone);
            return Result.success(vo);
        }

        UserEntity userInfo = new UserEntity();
        userInfo.setUserName(wxJsRegisterBo.getUsername());
        userInfo.setAvatar(wxJsRegisterBo.getAvatar());
        userInfo.setPhone(wxJsRegisterBo.getPhone());
        userInfo.setStatus(UserStatus.ENABLE.getCode());
        userInfo.setPlatform(StringUtils.isNotBlank(wxJsRegisterBo.getPlatform()) ? wxJsRegisterBo.getPlatform() : "mp");
        userInfo.setUserType(UserType.USER.getCode());
        userInfo.setIsVip(0);
        userInfo.setInvitationCode(GenUtil.genInviteCode(wxJsRegisterBo.getPhone()));
        userInfo.setSuperiorInvitationCode(wxJsRegisterBo.getSuperiorInvitationCode());
        userInfo.setMaOpenId(wxJsRegisterBo.getOpenId());
        userInfo.setCreateTime(LocalDateTime.now());

        iUserService.save(userInfo);

        /* 其他业务 */
        registerBiz(userInfo.getUserId());

        LoginVO vo = buildLoginResult(userInfo);
        return Result.success(vo);
    }

    @Override
    public Result<Object> decryptPhone(DecryptPhoneBo decryptPhoneBo) {
        String decryptS5 = wxUtil.decryptS5(decryptPhoneBo.getDecryptData(), decryptPhoneBo.getKey(), decryptPhoneBo.getIv());
        return Result.success(JSONObject.parseObject(decryptS5));
    }

    @Override
    public WxUserPhone getWxUserPhoneNumber(String code, String openId) {
        WxUserPhone wxUserPhone = wxUtil.getUserPhoneNumber(code, openId, RequestUtil.getClientType());

        return wxUserPhone;
    }

    @Override
    public Result<Void> logout(String token) {
        appAuthTokenSupport.invalidateToken(token);
        return Result.success();
    }

    /**
     * 构建登录返回值
     * @param userEntity 用户信息
     * @return
     */
    private LoginVO buildLoginResult(UserEntity userEntity) {
        LoginVO vo = new LoginVO();

        //生成token
        String token = jwtUtil.generateToken(userEntity.getUserId());
        vo.setToken(token);
        vo.setExpire(jwtUtil.getExpire());
        vo.setUser(userEntity);
        return vo;

    }

    /**
     * 手机号获取用户信息
     * @param phoneNo 手机号
     * @return
     */
    private UserEntity getUserByPhone(String phoneNo) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getPhone, phoneNo);

        return iUserService.getOne(queryWrapper);
    }
}
