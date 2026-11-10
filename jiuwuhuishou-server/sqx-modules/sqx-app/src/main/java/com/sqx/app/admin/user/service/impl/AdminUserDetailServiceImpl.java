package com.sqx.app.admin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.address.domain.vo.UserAddressVo;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressListBo;
import com.sqx.app.admin.user.domain.bo.AdminUserSignRecordPageBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareInfoBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareUpdateCoinBo;
import com.sqx.app.admin.user.service.AdminUserDetailService;
import com.sqx.app.welfare.db.service.IUserSignRecordService;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.entity.UserWelfare;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminUserDetailServiceImpl implements AdminUserDetailService {

    @Resource
    private IUserAddressService iUserAddressService;
    @Resource
    private IUserWelfareService iUserWelfareService;
    @Resource
    private IUserSignRecordService iUserSignRecordService;

    @Override
    public List<UserAddressVo> listAddresses(AdminUserAddressListBo listBo) {
        List<UserAddress> addressList = iUserAddressService.list(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, listBo.getUserId())
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime));
        if (CollectionUtils.isEmpty(addressList)) {
            return new ArrayList<>();
        }
        List<UserAddressVo> voList = new ArrayList<>();
        for (UserAddress address : addressList) {
            UserAddressVo vo = new UserAddressVo();
            BeanUtils.copyProperties(address, vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public WelfareSummaryVo welfareInfo(AdminUserWelfareInfoBo infoBo) {
        UserWelfare userWelfare = getOrCreateUserWelfare(infoBo.getUserId());
        WelfareSummaryVo vo = new WelfareSummaryVo();
        vo.setCoinBalance(defaultInt(userWelfare.getCoinBalance()));
        vo.setContinuousSignDays(defaultInt(userWelfare.getContinuousSignDays()));
        vo.setContinuousSignEnabled(Objects.equals(userWelfare.getContinuousSignEnabled(), 1));
        vo.setSignedToday(false);
        return vo;
    }

    @Override
    public void updateWelfareCoin(AdminUserWelfareUpdateCoinBo updateBo) {
        if (updateBo.getAmount() == null || updateBo.getAmount() <= 0) {
            throw new BizException("积分数量必须大于0");
        }
        UserWelfare userWelfare = getOrCreateUserWelfare(updateBo.getUserId());
        int balance = defaultInt(userWelfare.getCoinBalance());
        if ("decr".equalsIgnoreCase(updateBo.getOperator())) {
            userWelfare.setCoinBalance(Math.max(0, balance - updateBo.getAmount()));
        } else {
            userWelfare.setCoinBalance(balance + updateBo.getAmount());
        }
        iUserWelfareService.updateById(userWelfare);
    }

    @Override
    public IPage<UserSignRecord> signRecordPage(AdminUserSignRecordPageBo pageBo) {
        Page<UserSignRecord> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<UserSignRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSignRecord::getUserId, pageBo.getUserId())
                .orderByDesc(UserSignRecord::getCreateTime);
        return iUserSignRecordService.page(initPage, queryWrapper);
    }

    private UserWelfare getOrCreateUserWelfare(Long userId) {
        UserWelfare userWelfare = iUserWelfareService.getOne(new LambdaQueryWrapper<UserWelfare>()
                .eq(UserWelfare::getUserId, userId)
                .last("limit 1"));
        if (Objects.nonNull(userWelfare)) {
            return userWelfare;
        }
        userWelfare = new UserWelfare();
        userWelfare.setUserId(userId);
        userWelfare.setCoinBalance(0);
        userWelfare.setContinuousSignDays(0);
        userWelfare.setContinuousSignEnabled(0);
        iUserWelfareService.save(userWelfare);
        return userWelfare;
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }
}
