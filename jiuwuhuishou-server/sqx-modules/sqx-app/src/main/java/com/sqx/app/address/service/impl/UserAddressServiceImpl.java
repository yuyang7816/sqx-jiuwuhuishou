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
import com.sqx.app.address.service.UserAddressService;
import com.sqx.common.core.exception.BizException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户地址业务服务实现
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    private static final Integer DEFAULT_YES = 1;

    @Resource
    private IUserAddressService iUserAddressService;

    @Override
    public List<UserAddressVo> list(Long userId) {
        List<UserAddress> addressList = iUserAddressService.list(buildUserQuery(userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime));
        if (CollectionUtils.isEmpty(addressList)) {
            return Collections.emptyList();
        }
        return addressList.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long userId, UserAddressSaveBo saveBo) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(saveBo, userAddress);
        userAddress.setUserId(userId);
        if (Objects.equals(saveBo.getIsDefault(), DEFAULT_YES)) {
            clearDefault(userId, null);
        }
        iUserAddressService.save(userAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, UserAddressUpdateBo updateBo) {
        getUserAddress(userId, updateBo.getId());
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(updateBo, userAddress);
        userAddress.setUserId(userId);
        if (Objects.equals(updateBo.getIsDefault(), DEFAULT_YES)) {
            clearDefault(userId, updateBo.getId());
        }
        iUserAddressService.updateById(userAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, UserAddressDeleteBo deleteBo) {
        UserAddress existing = getUserAddress(userId, deleteBo.getId());
        boolean wasDefault = Objects.equals(existing.getIsDefault(), DEFAULT_YES);
        iUserAddressService.removeById(deleteBo.getId());
        if (wasDefault) {
            List<UserAddress> remaining = iUserAddressService.list(buildUserQuery(userId)
                    .orderByAsc(UserAddress::getCreateTime)
                    .last("LIMIT 1"));
            if (CollectionUtils.isNotEmpty(remaining)) {
                UserAddress first = remaining.get(0);
                first.setIsDefault(DEFAULT_YES);
                iUserAddressService.updateById(first);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, UserAddressSetDefaultBo setDefaultBo) {
        getUserAddress(userId, setDefaultBo.getId());
        clearDefault(userId, null);
        UserAddress userAddress = new UserAddress();
        userAddress.setId(setDefaultBo.getId());
        userAddress.setIsDefault(DEFAULT_YES);
        iUserAddressService.updateById(userAddress);
    }

    private UserAddress getUserAddress(Long userId, Long addressId) {
        UserAddress userAddress = iUserAddressService.getOne(buildUserQuery(userId)
                .eq(UserAddress::getId, addressId));
        if (Objects.isNull(userAddress)) {
            throw new BizException("地址不存在");
        }
        return userAddress;
    }

    private LambdaQueryWrapper<UserAddress> buildUserQuery(Long userId) {
        return new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId);
    }

    private void clearDefault(Long userId, Long excludeId) {
        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, DEFAULT_YES)
                .set(UserAddress::getIsDefault, 0);
        if (Objects.nonNull(excludeId)) {
            updateWrapper.ne(UserAddress::getId, excludeId);
        }
        iUserAddressService.update(updateWrapper);
    }

    private UserAddressVo toVo(UserAddress userAddress) {
        UserAddressVo vo = new UserAddressVo();
        BeanUtils.copyProperties(userAddress, vo);
        return vo;
    }
}
