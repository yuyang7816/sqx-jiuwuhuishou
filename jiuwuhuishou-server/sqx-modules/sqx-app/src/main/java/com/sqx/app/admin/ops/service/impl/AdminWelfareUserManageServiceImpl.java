package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareUserPageBo;
import com.sqx.app.admin.ops.domain.vo.AdminWelfareUserVo;
import com.sqx.app.admin.ops.service.AdminWelfareUserManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.domain.entity.UserWelfare;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AdminWelfareUserManageServiceImpl implements AdminWelfareUserManageService {

    @Resource
    private IUserWelfareService iUserWelfareService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public IPage<AdminWelfareUserVo> pageInfo(AdminWelfareUserPageBo pageBo) {
        Page<UserWelfare> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<UserWelfare> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, UserWelfare::getUserId, userIds)
                .orderByDesc(UserWelfare::getUpdateTime)
                .orderByDesc(UserWelfare::getId);

        IPage<UserWelfare> iPage = iUserWelfareService.page(initPage, wrapper);
        List<AdminWelfareUserVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (UserWelfare record : iPage.getRecords()) {
                AdminWelfareUserVo vo = new AdminWelfareUserVo();
                BeanUtils.copyProperties(record, vo);
                adminLookupSupport.fillUserFields(record.getUserId(), vo);
                voList.add(vo);
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }
}
