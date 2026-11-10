package com.sqx.app.admin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressPageBo;
import com.sqx.app.admin.user.domain.vo.AdminUserAddressVo;
import com.sqx.app.admin.user.service.AdminUserAddressManageService;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AdminUserAddressManageServiceImpl implements AdminUserAddressManageService {

    @Resource
    private IUserAddressService iUserAddressService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public IPage<AdminUserAddressVo> pageInfo(AdminUserAddressPageBo pageBo) {
        Page<UserAddress> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, UserAddress::getUserId, userIds)
                .like(StringUtils.isNotBlank(pageBo.getContactName()), UserAddress::getContactName, pageBo.getContactName())
                .like(StringUtils.isNotBlank(pageBo.getContactMobile()), UserAddress::getContactMobile, pageBo.getContactMobile())
                .eq(Objects.nonNull(pageBo.getIsDefault()), UserAddress::getIsDefault, pageBo.getIsDefault())
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime)
                .orderByDesc(UserAddress::getId);

        IPage<UserAddress> iPage = iUserAddressService.page(initPage, wrapper);
        List<AdminUserAddressVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (UserAddress record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    private AdminUserAddressVo toVo(UserAddress address) {
        AdminUserAddressVo vo = new AdminUserAddressVo();
        BeanUtils.copyProperties(address, vo);
        adminLookupSupport.fillUserFields(address.getUserId(), vo);
        String addressText = Stream.of(
                        address.getProvince(),
                        address.getCity(),
                        address.getDistrict(),
                        address.getStreet(),
                        address.getDetailAddress()
                )
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining());
        vo.setAddressText(addressText);
        return vo;
    }
}
