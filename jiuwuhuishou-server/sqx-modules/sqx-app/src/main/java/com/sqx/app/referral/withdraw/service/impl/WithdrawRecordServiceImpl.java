package com.sqx.app.referral.withdraw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.referral.constants.ReferralConstant;
import com.sqx.app.referral.withdraw.db.service.IWithdrawRecordService;
import com.sqx.app.referral.withdraw.db.service.IWithdrawalAccountService;
import com.sqx.app.referral.withdraw.domain.bo.*;
import com.sqx.app.referral.withdraw.domain.entity.WithdrawRecord;
import com.sqx.app.referral.withdraw.domain.entity.WithdrawalAccount;
import com.sqx.app.referral.withdraw.domain.vo.WithdrawRecordVo;
import com.sqx.app.referral.withdraw.service.WithdrawRecordService;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import com.sqx.system.db.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 提现记录 服务实现类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-05 10:10:32
 */
@Slf4j
@Service
public class WithdrawRecordServiceImpl implements WithdrawRecordService {

    private static final int TX_TYPE_WITHDRAW = 4;
    private static final int BIZ_TYPE_WITHDRAW = 4;
    private static final int DIRECTION_IN = 1;
    private static final int DIRECTION_OUT = 2;
    private static final int TX_STATUS_DONE = 2;

    @Resource
    private IWithdrawRecordService iWithdrawRecordService;
    @Resource
    private ISysConfigService iSysConfigService;
    @Resource
    private IUserService iUserService;
    @Resource
    private IWithdrawalAccountService iWithdrawalAccountService;
    @Resource
    private IUserWalletService iUserWalletService;
    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveInfo(WithdrawRecordSaveBo withdrawRecordSaveBo) {
        Long userId = withdrawRecordSaveBo.getUserId();
        BigDecimal amount = withdrawRecordSaveBo.getAmount();
        if (Objects.isNull(userId) || Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("提现金额必须大于0");
        }

        UserWallet userWallet = getOrCreateWallet(userId);
        BigDecimal balance = defaultAmount(userWallet.getAmount());
        if (amount.compareTo(balance) > 0) {
            throw new BizException("钱包余额不足");
        }

        String maxAmount = iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MAX);
        if (amount.compareTo(new BigDecimal(maxAmount)) > 0) {
            throw new BizException("最大提现金额为" + maxAmount);
        }

        String minAmount = iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_MIN);
        if (amount.compareTo(new BigDecimal(minAmount)) < 0) {
            throw new BizException("最小提现金额为" + minAmount);
        }

        // 计算到账金额
        String serviceFeeRate = iSysConfigService.getConfigValue(ReferralConstant.withdraw.SYS_WITHDRAW_RATE);
        BigDecimal serviceFee = amount.multiply(new BigDecimal(serviceFeeRate));
        BigDecimal actualAmount = amount.subtract(serviceFee);

        // 保存提现记录
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        BeanUtils.copyProperties(withdrawRecordSaveBo, withdrawRecord);
        withdrawRecord.setActualAmount(actualAmount);
        withdrawRecord.setServiceFee(serviceFee);
        withdrawRecord.setStatus(1);
        iWithdrawRecordService.save(withdrawRecord);

        // 扣除钱包余额
        iUserWalletService.decr(userId, amount);
        recordWalletTransaction(
                userId,
                amount,
                DIRECTION_OUT,
                "发起提现",
                String.format("发起提现: %s", amount.toPlainString()),
                withdrawRecord.getId()
        );
    }

    @Override
    public IPage<WithdrawRecordVo> pageInfo(WithdrawRecordPageBo withdrawRecordPageBo) {
        Page<WithdrawRecord> initPage = PageUtil.initPage(withdrawRecordPageBo.getPage(), withdrawRecordPageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(
                withdrawRecordPageBo.getUserName(), withdrawRecordPageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        QueryWrapper<WithdrawRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Objects.nonNull(withdrawRecordPageBo.getUserId()), WithdrawRecord::getUserId, withdrawRecordPageBo.getUserId())
                .in(userIds != null, WithdrawRecord::getUserId, userIds)
                .eq(Objects.nonNull(withdrawRecordPageBo.getStatus()), WithdrawRecord::getStatus, withdrawRecordPageBo.getStatus())
                .orderByDesc(WithdrawRecord::getCreateTime);

        IPage<WithdrawRecord> iPage = iWithdrawRecordService.page(initPage, queryWrapper);
        List<WithdrawRecordVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (WithdrawRecord record : iPage.getRecords()) {
                WithdrawRecordVo vo = new WithdrawRecordVo();
                BeanUtils.copyProperties(record, vo);

                UserEntity userEntity = iUserService.getById(record.getUserId());
                if (Objects.nonNull(userEntity)) {
                    vo.setUserEntity(userEntity);
                }

                WithdrawalAccount withdrawalAccount = iWithdrawalAccountService.getOne(new LambdaQueryWrapper<WithdrawalAccount>()
                        .eq(WithdrawalAccount::getUserId, record.getUserId())
                        .eq(WithdrawalAccount::getType, record.getAccountType())
                );
                if (Objects.nonNull(withdrawalAccount)) {
                    vo.setWithdrawalAccount(withdrawalAccount);
                }

                voList.add(vo);
            }
        }

        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public WithdrawRecordVo info(WithdrawRecordInfoBo withdrawRecordInfoBo) {
        WithdrawRecord withdrawRecord = iWithdrawRecordService.getById(withdrawRecordInfoBo.getId());
        if (Objects.isNull(withdrawRecord)) {
            throw new BizException("信息不存在");
        }

        WithdrawRecordVo vo = new WithdrawRecordVo();
        BeanUtils.copyProperties(withdrawRecord, vo);

        UserEntity userEntity = iUserService.getById(withdrawRecord.getUserId());
        if (Objects.nonNull(userEntity)) {
            vo.setUserEntity(userEntity);
        }

        return vo;
    }

    @Override
    public void updateInfo(WithdrawRecordUpdateBo withdrawRecordUpdateBo) {
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        BeanUtils.copyProperties(withdrawRecordUpdateBo, withdrawRecord);

        iWithdrawRecordService.updateById(withdrawRecord);
    }

    @Override
    public void delete(WithdrawRecordInfoBo withdrawRecordInfoBo) {
        iWithdrawRecordService.removeById(withdrawRecordInfoBo.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void confirm(WithdrawRecordConfirmBo withdrawRecordConfirmBo) {
        WithdrawRecord withdrawRecord = iWithdrawRecordService.getById(withdrawRecordConfirmBo.getId());
        if (Objects.isNull(withdrawRecord)) {
            throw new BizException("提现信息不存在");
        }

        if (2 == withdrawRecordConfirmBo.getStatus()) {
            // 提现成功（发起时已扣钱包余额）
            withdrawRecord.setStatus(2);
            withdrawRecord.setWithdrawTime(LocalDateTime.now());
            iWithdrawRecordService.updateById(withdrawRecord);
        } else if (3 == withdrawRecordConfirmBo.getStatus()) {
            // 拒绝提现，退回钱包余额
            withdrawRecord.setStatus(3);
            withdrawRecord.setReason(withdrawRecordConfirmBo.getReason());
            iWithdrawRecordService.updateById(withdrawRecord);

            iUserWalletService.incr(withdrawRecord.getUserId(), withdrawRecord.getAmount());
            recordWalletTransaction(
                    withdrawRecord.getUserId(),
                    withdrawRecord.getAmount(),
                    DIRECTION_IN,
                    "提现退回",
                    String.format("提现被拒绝退回: %s", withdrawRecord.getAmount().toPlainString()),
                    withdrawRecord.getId()
            );
        } else {
            throw new BizException("非法操作");
        }
    }

    private UserWallet getOrCreateWallet(Long userId) {
        UserWallet wallet = iUserWalletService.getOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));
        if (Objects.nonNull(wallet)) {
            return wallet;
        }
        wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setAmount(BigDecimal.ZERO);
        iUserWalletService.save(wallet);
        return wallet;
    }

    private void recordWalletTransaction(Long userId, BigDecimal amount, int direction, String title, String description, Long bizId) {
        UserWalletTransaction transaction = new UserWalletTransaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TX_TYPE_WITHDRAW);
        transaction.setBizType(BIZ_TYPE_WITHDRAW);
        transaction.setBizId(bizId);
        transaction.setDirection(direction);
        transaction.setStatus(TX_STATUS_DONE);
        transaction.setTitle(title);
        transaction.setDescription(description);
        try {
            iUserWalletTransactionService.save(transaction);
        } catch (Exception e) {
            log.warn("保存钱包提现流水失败，userId={}, amount={}", userId, amount, e);
        }
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return Objects.isNull(amount) ? BigDecimal.ZERO : amount;
    }
}
