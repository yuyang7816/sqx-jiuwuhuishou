package com.sqx.app.wallet.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionInfoBo;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionPageBo;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionSaveBo;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionUpdateBo;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.transaction.domain.vo.UserWalletTransactionVo;
import com.sqx.app.wallet.transaction.service.UserWalletTransactionService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户钱包交易记录表 服务实现类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-13 15:12:03
 */
@Service
public class UserWalletTransactionServiceImpl implements UserWalletTransactionService {

    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public void saveInfo(UserWalletTransactionSaveBo userWalletTransactionSaveBo) {
        UserWalletTransaction userWalletTransaction = new UserWalletTransaction();
        BeanUtils.copyProperties(userWalletTransactionSaveBo, userWalletTransaction);

        iUserWalletTransactionService.save(userWalletTransaction);
    }

    @Override
    public IPage<UserWalletTransactionVo> pageInfo(UserWalletTransactionPageBo userWalletTransactionPageBo) {
        Page<UserWalletTransaction> initPage = PageUtil.initPage(userWalletTransactionPageBo.getPage(), userWalletTransactionPageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(
                userWalletTransactionPageBo.getUserName(), userWalletTransactionPageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        QueryWrapper<UserWalletTransaction> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Objects.nonNull(userWalletTransactionPageBo.getUserId()), UserWalletTransaction::getUserId, userWalletTransactionPageBo.getUserId())
                .in(userIds != null, UserWalletTransaction::getUserId, userIds)
                .orderByDesc(UserWalletTransaction::getCreateTime);

        IPage<UserWalletTransaction> iPage = iUserWalletTransactionService.page(initPage, queryWrapper);
        List<UserWalletTransactionVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (UserWalletTransaction record : iPage.getRecords()) {
                UserWalletTransactionVo vo = new UserWalletTransactionVo();
                BeanUtils.copyProperties(record, vo);
                adminLookupSupport.fillUserFields(record.getUserId(), vo);
                voList.add(vo);
            }
        }

        IPage<UserWalletTransactionVo> voIPage = PageUtil.copyPage(iPage, voList);
        return voIPage;
    }

    @Override
    public UserWalletTransactionVo info(UserWalletTransactionInfoBo userWalletTransactionInfoBo) {
        UserWalletTransaction userWalletTransaction = iUserWalletTransactionService.getById(userWalletTransactionInfoBo.getId());
        if (Objects.isNull(userWalletTransaction)) {
            throw new BizException("信息不存在");
        }

        UserWalletTransactionVo vo = new UserWalletTransactionVo();
        BeanUtils.copyProperties(userWalletTransaction, vo);
        return vo;
    }

    @Override
    public void updateInfo(UserWalletTransactionUpdateBo userWalletTransactionUpdateBo) {
        UserWalletTransaction userWalletTransaction = new UserWalletTransaction();
        BeanUtils.copyProperties(userWalletTransactionUpdateBo, userWalletTransaction);

        iUserWalletTransactionService.updateById(userWalletTransaction);
    }

    @Override
    public void delete(UserWalletTransactionInfoBo userWalletTransactionInfoBo) {
        iUserWalletTransactionService.removeById(userWalletTransactionInfoBo.getId());
    }
}
