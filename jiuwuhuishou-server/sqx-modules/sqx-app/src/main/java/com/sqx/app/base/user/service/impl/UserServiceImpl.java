package com.sqx.app.base.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.enums.AppError;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.bo.ChangePasswordBo;
import com.sqx.app.base.user.domain.bo.DeleteUserBO;
import com.sqx.app.base.user.domain.bo.ForgetPasswordBO;
import com.sqx.app.base.user.domain.bo.ListUserBO;
import com.sqx.app.base.user.domain.bo.UpdateUserBO;
import com.sqx.app.base.user.domain.bo.UserMessageNotifyBo;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.base.user.domain.vo.UserMessageNotifyVo;
import com.sqx.app.base.user.domain.vo.UserVo;
import com.sqx.app.base.user.service.UserService;
import com.sqx.app.base.verifyCode.service.VerifyCodeService;
import com.sqx.common.core.domain.Result;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.Md5Utils;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author chengming
 * @since 2025/4/24
 */
@Service
public class UserServiceImpl implements UserService {

    private static final int STATE_ENABLED = 1;
    private static final int STATE_DISABLED = 0;

    @Resource
    private IUserService iUserService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @Override
    public Result<UserVo> info(Long userId) {
        UserEntity userEntity = iUserService.getById(userId);
        if (Objects.isNull(userEntity)) {
            throw new BizException(AppError.USER_NOT_FOUND);
        }

        UserVo vo = new UserVo();
        BeanUtils.copyProperties(userEntity, vo);
        return Result.success(vo);
    }

    @Override
    public Result<Void> forgetPassword(ForgetPasswordBO forgetPasswordBO) {
        UserEntity userEntity = iUserService.getById(forgetPasswordBO.getUserId());
        if (Objects.isNull(userEntity)) {
            throw new BizException(AppError.USER_NOT_FOUND);
        }

        if (!verifyCodeService.verify(userEntity.getPhone(), forgetPasswordBO.getCode())) {
            throw new BizException(AppError.VERIFY_CODE_ERROR);
        }

        iUserService.update(new UserEntity(), new LambdaUpdateWrapper<UserEntity>()
                .eq(UserEntity::getUserId, forgetPasswordBO.getUserId())
                .set(UserEntity::getPassword, DigestUtils.md5Hex(forgetPasswordBO.getPassword()))
        );

        return Result.success();
    }

    @Override
    public Page<UserEntity> list(ListUserBO listUserBO) {
        Page<UserEntity> initPage = PageUtil.initPage(listUserBO.getPage(), listUserBO.getLimit());

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(listUserBO.getPhone()), UserEntity::getPhone, listUserBO.getPhone())
                .eq(StringUtils.isNotBlank(listUserBO.getUsername()), UserEntity::getUserName, listUserBO.getUsername())
                .eq(Objects.nonNull(listUserBO.getStatus()), UserEntity::getStatus, listUserBO.getStatus())
                .eq(Objects.nonNull(listUserBO.getUserType()), UserEntity::getUserType, listUserBO.getUserType())
                .eq(StringUtils.isNotBlank(listUserBO.getSuperiorInvitationCode()), UserEntity::getSuperiorInvitationCode, listUserBO.getSuperiorInvitationCode())
                .eq(StringUtils.isNotBlank(listUserBO.getInvitationCode()), UserEntity::getInvitationCode, listUserBO.getInvitationCode())
                .eq(StringUtils.isNotBlank(listUserBO.getPlatform()), UserEntity::getPlatform, listUserBO.getPlatform())
                .orderByDesc(UserEntity::getCreateTime);

        return iUserService.page(initPage, queryWrapper);
    }

    @Override
    public void update(UpdateUserBO updateUserBO) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(updateUserBO, userEntity);

        iUserService.updateById(userEntity);
    }

    @Override
    public UserMessageNotifyVo updateMessageNotifyEnabled(Long userId, UserMessageNotifyBo messageNotifyBo) {
        UserEntity userEntity = iUserService.getById(userId);
        if (Objects.isNull(userEntity)) {
            throw new BizException(AppError.USER_NOT_FOUND);
        }

        boolean enabled = Boolean.TRUE.equals(messageNotifyBo.getEnabled());
        UserEntity updateEntity = new UserEntity();
        updateEntity.setUserId(userId);
        updateEntity.setMessageNotifyEnabled(enabled ? STATE_ENABLED : STATE_DISABLED);
        iUserService.updateById(updateEntity);

        UserMessageNotifyVo vo = new UserMessageNotifyVo();
        vo.setMessageNotifyEnabled(enabled);
        return vo;
    }

    @Override
    public void changePassword(Long userId, ChangePasswordBo changePasswordBo) {
        UserEntity userEntity = iUserService.getById(userId);
        if (Objects.isNull(userEntity)) {
            throw new BizException(AppError.USER_NOT_FOUND);
        }

        String oldPassword = StringUtils.trimToEmpty(changePasswordBo.getOldPassword());
        String newPassword = StringUtils.trimToEmpty(changePasswordBo.getNewPassword());
        if (newPassword.length() < 6) {
            throw new BizException("新密码不能少于6位");
        }
        if (Objects.equals(oldPassword, newPassword)) {
            throw new BizException("新密码不能与当前密码相同");
        }

        String encryptedOldPassword = Md5Utils.md5s(oldPassword);
        if (!Objects.equals(encryptedOldPassword, userEntity.getPassword())) {
            throw new BizException("当前密码不正确");
        }

        UserEntity updateEntity = new UserEntity();
        updateEntity.setUserId(userId);
        updateEntity.setPassword(Md5Utils.md5s(newPassword));
        iUserService.updateById(updateEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(DeleteUserBO deleteUserBO) {
        iUserService.removeById(deleteUserBO.getUserId());

        // 其他需要删除的信息
    }
}
