package com.sqx.app.address.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.bo.UserAddressDeleteBo;
import com.sqx.app.address.domain.bo.UserAddressSaveBo;
import com.sqx.app.address.domain.bo.UserAddressSetDefaultBo;
import com.sqx.app.address.domain.bo.UserAddressUpdateBo;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.address.domain.vo.UserAddressVo;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserAddressServiceTest {

    @Mock
    private IUserAddressService iUserAddressService;

    @InjectMocks
    private UserAddressServiceImpl userAddressService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserAddress.class);
    }

    @Test
    void listShouldHandleEmptyAndMapped() {
        when(iUserAddressService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(userAddressService.list(100L).isEmpty());

        UserAddress address = buildAddress(1L, 100L, "13812348888", 1);
        when(iUserAddressService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(address));
        List<UserAddressVo> result = userAddressService.list(100L);
        assertEquals(1, result.size());
        assertEquals("13812348888", result.get(0).getContactMobile());
    }

    @Test
    void saveShouldClearDefaultOnlyWhenMarked() {
        UserAddressSaveBo saveBo = new UserAddressSaveBo();
        saveBo.setContactName("张三");
        saveBo.setContactMobile("13800001111");
        saveBo.setDetailAddress("测试地址");
        saveBo.setIsDefault(0);
        userAddressService.save(100L, saveBo);
        verify(iUserAddressService, never()).update(any(LambdaUpdateWrapper.class));
        verify(iUserAddressService).save(any(UserAddress.class));

        saveBo.setIsDefault(1);
        userAddressService.save(100L, saveBo);
        verify(iUserAddressService).update(any(LambdaUpdateWrapper.class));
        verify(iUserAddressService, times(2)).save(any(UserAddress.class));
    }

    @Test
    void updateShouldValidateAndClearDefault() {
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        UserAddressUpdateBo updateBo = new UserAddressUpdateBo();
        updateBo.setId(1L);
        assertThrows(BizException.class, () -> userAddressService.update(100L, updateBo));

        UserAddress existing = buildAddress(1L, 100L, "13800001111", 0);
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        updateBo.setIsDefault(0);
        userAddressService.update(100L, updateBo);
        verify(iUserAddressService, never()).update(any(LambdaUpdateWrapper.class));
        verify(iUserAddressService).updateById(any(UserAddress.class));

        updateBo.setIsDefault(1);
        userAddressService.update(100L, updateBo);
        verify(iUserAddressService).update(any(LambdaUpdateWrapper.class));
    }

    @Test
    void deleteShouldPromoteOrSkip() {
        UserAddress nonDefault = buildAddress(3L, 100L, "13800003333", 0);
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(nonDefault);
        UserAddressDeleteBo deleteBo = new UserAddressDeleteBo();
        deleteBo.setId(3L);
        userAddressService.delete(100L, deleteBo);
        verify(iUserAddressService).removeById(3L);
        verify(iUserAddressService, never()).updateById(any(UserAddress.class));

        UserAddress defaultAddress = buildAddress(1L, 100L, "13800001111", 1);
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(defaultAddress);
        when(iUserAddressService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        deleteBo.setId(1L);
        userAddressService.delete(100L, deleteBo);
        verify(iUserAddressService).removeById(1L);
        verify(iUserAddressService, never()).updateById(any(UserAddress.class));

        UserAddress remaining = buildAddress(2L, 100L, "13800002222", 0);
        when(iUserAddressService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(remaining));
        userAddressService.delete(100L, deleteBo);
        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(iUserAddressService).updateById(captor.capture());
        assertEquals(1, captor.getValue().getIsDefault());
        assertEquals(2L, captor.getValue().getId());
    }

    @Test
    void setDefaultShouldSucceedAndThrowWhenMissing() {
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        UserAddressSetDefaultBo bo = new UserAddressSetDefaultBo();
        bo.setId(99L);
        assertThrows(BizException.class, () -> userAddressService.setDefault(100L, bo));

        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(buildAddress(1L, 100L, "138", 0));
        bo.setId(1L);
        userAddressService.setDefault(100L, bo);
        verify(iUserAddressService).update(any(LambdaUpdateWrapper.class));
        verify(iUserAddressService).updateById(any(UserAddress.class));
    }

    private UserAddress buildAddress(Long id, Long userId, String mobile, Integer isDefault) {
        UserAddress address = new UserAddress();
        address.setId(id);
        address.setUserId(userId);
        address.setContactName("测试");
        address.setContactMobile(mobile);
        address.setDetailAddress("详细地址");
        address.setIsDefault(isDefault);
        return address;
    }
}
