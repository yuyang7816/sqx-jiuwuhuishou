package com.sqx.app.wallet.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.bo.*;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.wallet.wallet.domain.vo.UserWalletVo;
import com.sqx.app.wallet.wallet.enums.Operator;
import com.sqx.app.wallet.wallet.service.UserWalletService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import com.sqx.common.shiro.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户钱包主表 服务实现类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-13 15:09:58
 */
@Slf4j
@Service
public class UserWalletServiceImpl implements UserWalletService {

    @Resource
    private IUserWalletService iUserWalletService;
    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public void saveInfo(UserWalletSaveBo userWalletSaveBo) {
        UserWallet userWallet = new UserWallet();
        BeanUtils.copyProperties(userWalletSaveBo, userWallet);

        iUserWalletService.save(userWallet);
    }

    @Override
    public IPage<UserWalletVo> pageInfo(UserWalletPageBo userWalletPageBo) {
        Page<UserWallet> initPage = PageUtil.initPage(userWalletPageBo.getPage(), userWalletPageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(userWalletPageBo.getUserName(), userWalletPageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        QueryWrapper<UserWallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(userIds != null, UserWallet::getUserId, userIds)
                .orderByDesc(UserWallet::getCreateTime);

        IPage<UserWallet> iPage = iUserWalletService.page(initPage, queryWrapper);
        List<UserWalletVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (UserWallet record : iPage.getRecords()) {
                UserWalletVo vo = new UserWalletVo();
                BeanUtils.copyProperties(record, vo);
                adminLookupSupport.fillUserFields(record.getUserId(), vo);
                voList.add(vo);
            }
        }

        IPage<UserWalletVo> voIPage = PageUtil.copyPage(iPage, voList);
        return voIPage;
    }

    @Override
    public UserWalletVo info(UserWalletInfoBo userWalletInfoBo) {
        UserWallet userWallet = iUserWalletService.getById(userWalletInfoBo.getId());
        if (Objects.isNull(userWallet)) {
            throw new BizException("信息不存在");
        }

        UserWalletVo vo = new UserWalletVo();
        BeanUtils.copyProperties(userWallet, vo);
        adminLookupSupport.fillUserFields(userWallet.getUserId(), vo);
        return vo;
    }

    @Override
    public void updateInfo(UserWalletUpdateBo userWalletUpdateBo) {
        UserWallet userWallet = new UserWallet();
        BeanUtils.copyProperties(userWalletUpdateBo, userWallet);

        iUserWalletService.updateById(userWallet);
    }

    @Override
    public void delete(UserWalletInfoBo userWalletInfoBo) {
        iUserWalletService.removeById(userWalletInfoBo.getId());
    }

    @Override
    public UserWalletVo wallet() {
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getUserId, UserContextHolder.getAppUserId());
        UserWallet userWallet = iUserWalletService.getOne(queryWrapper);
        if (Objects.isNull(userWallet)) {
            throw new BizException("信息不存在");
        }

        UserWalletVo vo = new UserWalletVo();
        BeanUtils.copyProperties(userWallet, vo);
        adminLookupSupport.fillUserFields(userWallet.getUserId(), vo);
        return vo;
    }

    @Override
    public void updateAmount(UserWalletUpdateAmountBo userWalletUpdateAmountBo) {
        if (userWalletUpdateAmountBo.getUserId() == null) {
            throw new BizException("用户ID不能为空");
        }
        if (userWalletUpdateAmountBo.getAmount() == null || userWalletUpdateAmountBo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("金额必须大于0");
        }

        if (Objects.equals(Operator.INCR.getValue(), userWalletUpdateAmountBo.getOperator())) {
            iUserWalletService.incr(userWalletUpdateAmountBo.getUserId(), userWalletUpdateAmountBo.getAmount());
            recordTransaction(userWalletUpdateAmountBo, 1, "管理员增加余额", String.format("管理员增加余额: %s", userWalletUpdateAmountBo.getAmount()));
            return;
        }

        if (Objects.equals(Operator.DECR.getValue(), userWalletUpdateAmountBo.getOperator())) {
            iUserWalletService.decr(userWalletUpdateAmountBo.getUserId(), userWalletUpdateAmountBo.getAmount());
            recordTransaction(userWalletUpdateAmountBo, 2, "管理员扣除余额", String.format("管理员扣除余额: %s", userWalletUpdateAmountBo.getAmount()));
            return;
        }

        log.error("不支持的操作: [{}]", userWalletUpdateAmountBo.getOperator());
        throw new BizException("不支持的操作");
    }
    /**
     * 记录钱包交易流水
     */
    private void recordTransaction(UserWalletUpdateAmountBo bo, int direction, String title, String description) {
        UserWalletTransaction transaction = new UserWalletTransaction();
        transaction.setUserId(bo.getUserId());
        transaction.setAmount(bo.getAmount());
        transaction.setTransactionType(5);
        transaction.setStatus(2);
        transaction.setDirection(direction);
        transaction.setTitle(title);
        transaction.setDescription(description);

        try {
            iUserWalletTransactionService.save(transaction);
        } catch (Exception e) {
            log.warn("保存交易记录失败，用户ID: {}, 金额: {}", bo.getUserId(), bo.getAmount(), e);
        }
    }

}
