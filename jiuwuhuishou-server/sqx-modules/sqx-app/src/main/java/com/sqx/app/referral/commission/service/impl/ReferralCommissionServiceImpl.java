package com.sqx.app.referral.commission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionInfoBo;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionPageBo;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionSaveBo;
import com.sqx.app.referral.commission.domain.bo.ReferralCommissionUpdateBo;
import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
import com.sqx.app.referral.commission.domain.vo.ReferralCommissionVo;
import com.sqx.app.referral.commission.service.ReferralCommissionService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 佣金信息 服务实现类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-04 14:44:40
 */
@Slf4j
@Service
public class ReferralCommissionServiceImpl implements ReferralCommissionService {

    @Resource
    private IReferralCommissionService iReferralCommissionService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public void saveInfo(ReferralCommissionSaveBo referralCommissionSaveBo) {
        ReferralCommission referralCommission = new ReferralCommission();
        BeanUtils.copyProperties(referralCommissionSaveBo, referralCommission);

        iReferralCommissionService.save(referralCommission);
    }

    @Override
    public IPage<ReferralCommissionVo> pageInfo(ReferralCommissionPageBo referralCommissionPageBo) {
        Page<ReferralCommission> initPage = PageUtil.initPage(referralCommissionPageBo.getPage(), referralCommissionPageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(
                referralCommissionPageBo.getUserName(), referralCommissionPageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }
        List<Long> orderIds = adminLookupSupport.resolveOrderIds(referralCommissionPageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        QueryWrapper<ReferralCommission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Objects.nonNull(referralCommissionPageBo.getUserId()), ReferralCommission::getUserId, referralCommissionPageBo.getUserId())
                .in(userIds != null, ReferralCommission::getUserId, userIds)
                .in(orderIds != null, ReferralCommission::getOrderId, orderIds)
                .orderByDesc(ReferralCommission::getCreateTime);

        IPage<ReferralCommission> iPage = iReferralCommissionService.page(initPage, queryWrapper);
        List<ReferralCommissionVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (ReferralCommission record : iPage.getRecords()) {
                ReferralCommissionVo vo = new ReferralCommissionVo();
                BeanUtils.copyProperties(record, vo);
                UserEntity userEntity = adminLookupSupport.getUser(record.getUserId());
                if (userEntity != null) {
                    vo.setUserEntity(userEntity);
                    vo.setUserName(userEntity.getUserName());
                    vo.setPhone(userEntity.getPhone());
                }
                vo.setOrderNo(adminLookupSupport.resolveOrderNo(record.getOrderId()));
                voList.add(vo);
            }
        }

        IPage<ReferralCommissionVo> voIPage = PageUtil.copyPage(iPage, voList);
        return voIPage;
    }

    @Override
    public ReferralCommissionVo info(ReferralCommissionInfoBo referralCommissionInfoBo) {
        ReferralCommission referralCommission = iReferralCommissionService.getById(referralCommissionInfoBo.getId());
        if (Objects.isNull(referralCommission)) {
            throw new BizException("信息不存在");
        }

        ReferralCommissionVo vo = new ReferralCommissionVo();
        BeanUtils.copyProperties(referralCommission, vo);

        return vo;
    }

    @Override
    public void updateInfo(ReferralCommissionUpdateBo referralCommissionUpdateBo) {
        ReferralCommission referralCommission = new ReferralCommission();
        BeanUtils.copyProperties(referralCommissionUpdateBo, referralCommission);

        iReferralCommissionService.updateById(referralCommission);
    }

    @Override
    public void delete(ReferralCommissionInfoBo referralCommissionInfoBo) {
        iReferralCommissionService.removeById(referralCommissionInfoBo.getId());
    }
}
