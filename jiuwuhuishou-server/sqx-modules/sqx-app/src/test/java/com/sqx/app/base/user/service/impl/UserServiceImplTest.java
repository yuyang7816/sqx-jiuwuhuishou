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
import com.sqx.app.base.verifyCode.service.VerifyCodeService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.Md5Utils;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private IUserService iUserService;
    @Mock
    private VerifyCodeService verifyCodeService;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserEntity.class);
    }

    @Test
    void infoShouldReturnVoOrThrowWhenUserMissing() {
        when(iUserService.getById(1L)).thenReturn(null);
        BizException notFound = assertThrows(BizException.class, () -> service.info(1L));
        assertEquals(AppError.USER_NOT_FOUND.getCode(), notFound.getErrorCode().getCode());

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserName("alice");
        user.setPhone("13800138000");
        when(iUserService.getById(1L)).thenReturn(user);

        assertEquals("alice", service.info(1L).getData().getUserName());
        assertEquals("13800138000", service.info(1L).getData().getPhone());
    }

    @Test
    void forgetPasswordShouldValidateUserAndCode() {
        ForgetPasswordBO bo = new ForgetPasswordBO();
        bo.setUserId(2L);
        bo.setCode("123456");
        bo.setPassword("newpass1");

        when(iUserService.getById(2L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.forgetPassword(bo));

        UserEntity user = new UserEntity();
        user.setUserId(2L);
        user.setPhone("13900139000");
        when(iUserService.getById(2L)).thenReturn(user);
        when(verifyCodeService.verify("13900139000", "123456")).thenReturn(false);
        BizException verifyFail = assertThrows(BizException.class, () -> service.forgetPassword(bo));
        assertEquals(AppError.VERIFY_CODE_ERROR.getCode(), verifyFail.getErrorCode().getCode());

        when(verifyCodeService.verify("13900139000", "123456")).thenReturn(true);
        assertDoesNotThrow(() -> service.forgetPassword(bo));
        verify(iUserService).update(any(UserEntity.class), any(LambdaUpdateWrapper.class));
    }

    @Test
    void listShouldApplyFiltersAndDelegatePage() {
        ListUserBO withFilters = new ListUserBO();
        withFilters.setPage(1L);
        withFilters.setLimit(10L);
        withFilters.setPhone("13800138000");
        withFilters.setUsername("bob");
        withFilters.setStatus(1);
        withFilters.setUserType(2);
        withFilters.setSuperiorInvitationCode("SUP001");
        withFilters.setInvitationCode("INV001");

        Page<UserEntity> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new UserEntity()));
        when(iUserService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        assertEquals(1, service.list(withFilters).getRecords().size());

        ListUserBO noFilters = new ListUserBO();
        noFilters.setPage(2L);
        noFilters.setLimit(20L);
        assertEquals(1, service.list(noFilters).getRecords().size());
        verify(iUserService, times(2)).page(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void updateShouldDelegateUpdateById() {
        UpdateUserBO updateBo = new UpdateUserBO();
        updateBo.setUserId(3L);
        updateBo.setUserName("carol");
        service.update(updateBo);
        verify(iUserService).updateById(argThat(entity ->
                Long.valueOf(3L).equals(entity.getUserId()) && "carol".equals(entity.getUserName())));
    }

    @Test
    void updateMessageNotifyEnabledShouldHandleEnabledStates() {
        UserMessageNotifyBo notifyBo = new UserMessageNotifyBo();
        notifyBo.setEnabled(true);

        when(iUserService.getById(4L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.updateMessageNotifyEnabled(4L, notifyBo));

        UserEntity user = new UserEntity();
        user.setUserId(4L);
        when(iUserService.getById(4L)).thenReturn(user);

        assertTrue(service.updateMessageNotifyEnabled(4L, notifyBo).getMessageNotifyEnabled());
        verify(iUserService).updateById(argThat(entity ->
                Long.valueOf(4L).equals(entity.getUserId()) && Integer.valueOf(1).equals(entity.getMessageNotifyEnabled())));

        notifyBo.setEnabled(false);
        assertFalse(service.updateMessageNotifyEnabled(4L, notifyBo).getMessageNotifyEnabled());
        verify(iUserService).updateById(argThat(entity ->
                Long.valueOf(4L).equals(entity.getUserId()) && Integer.valueOf(0).equals(entity.getMessageNotifyEnabled())));

        notifyBo.setEnabled(null);
        assertFalse(service.updateMessageNotifyEnabled(4L, notifyBo).getMessageNotifyEnabled());
    }

    @Test
    void changePasswordShouldValidateAndUpdate() {
        ChangePasswordBo bo = new ChangePasswordBo();
        bo.setOldPassword("oldpass1");
        bo.setNewPassword("newpass1");

        when(iUserService.getById(5L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.changePassword(5L, bo));

        UserEntity user = new UserEntity();
        user.setUserId(5L);
        user.setPassword(Md5Utils.md5s("oldpass1"));
        when(iUserService.getById(5L)).thenReturn(user);

        bo.setNewPassword("12345");
        BizException tooShort = assertThrows(BizException.class, () -> service.changePassword(5L, bo));
        assertEquals("新密码不能少于6位", tooShort.getMessage());

        bo.setNewPassword("oldpass1");
        BizException samePassword = assertThrows(BizException.class, () -> service.changePassword(5L, bo));
        assertEquals("新密码不能与当前密码相同", samePassword.getMessage());

        bo.setNewPassword("newpass1");
        user.setPassword("wrong-hash");
        BizException wrongOld = assertThrows(BizException.class, () -> service.changePassword(5L, bo));
        assertEquals("当前密码不正确", wrongOld.getMessage());

        user.setPassword(Md5Utils.md5s("oldpass1"));
        service.changePassword(5L, bo);
        verify(iUserService).updateById(argThat(entity ->
                Long.valueOf(5L).equals(entity.getUserId())
                        && Md5Utils.md5s("newpass1").equals(entity.getPassword())));
    }

    @Test
    void deleteShouldRemoveUserById() {
        DeleteUserBO deleteBo = new DeleteUserBO();
        deleteBo.setUserId(6L);
        service.delete(deleteBo);
        verify(iUserService).removeById(6L);
    }
}
