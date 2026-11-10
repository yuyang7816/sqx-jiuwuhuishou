package com.sqx.app.welfare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.appConfig.domain.bo.SysConfigInfoBO;
import com.sqx.app.base.appConfig.service.AppConfigService;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.constants.ReferralConstant;
import com.sqx.app.referral.user.db.service.IReferralUserCommissionService;
import com.sqx.app.referral.user.domain.entity.ReferralUserCommission;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.welfare.db.service.IUserSignRecordService;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.db.service.IWelfareTaskService;
import com.sqx.app.welfare.domain.bo.WelfareCoinLogPageBo;
import com.sqx.app.welfare.domain.bo.WelfareContinuousSignBo;
import com.sqx.app.welfare.domain.bo.WelfareWithdrawBo;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.entity.UserWelfare;
import com.sqx.app.welfare.domain.entity.WelfareTask;
import com.sqx.app.welfare.domain.vo.WelfareCoinLogVo;
import com.sqx.app.welfare.domain.vo.WelfareSignResultVo;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
import com.sqx.app.welfare.domain.vo.WelfareTaskVo;
import com.sqx.app.welfare.domain.vo.WelfareWithdrawResultVo;
import com.sqx.app.welfare.service.WelfareService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WelfareServiceImpl implements WelfareService {

    private static final Integer STATE_ENABLED = 1;
    private static final Integer STATE_DISABLED = 0;
    private static final String CONFIG_SIGN_TOTAL_DAYS = "welfare_sign_total_days";
    private static final String CONFIG_SIGN_DAILY_COIN = "welfare_sign_daily_coin";
    private static final String CONFIG_WITHDRAW_MIN_AMOUNT = "welfare_withdraw_min_amount";
    /** 环保积分兑换钱包余额比例：1 积分 = N 元 */
    private static final String CONFIG_COIN_WALLET_RATE = "welfare_coin_wallet_rate";
    /** 交易类型：系统调整 */
    private static final int TX_TYPE_SYSTEM = 5;
    /** 业务类型：活动奖励 */
    private static final int BIZ_TYPE_REWARD = 3;
    private static final int DIRECTION_IN = 1;
    private static final int TX_STATUS_DONE = 2;
    private static final int COIN_LOG_INCOME = 1;

    @Resource
    private IUserWelfareService iUserWelfareService;

    @Resource
    private IUserSignRecordService iUserSignRecordService;

    @Resource
    private IWelfareTaskService iWelfareTaskService;

    @Resource
    private IUserWalletService iUserWalletService;

    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;

    @Resource
    private AppConfigService appConfigService;

    @Resource
    private IReferralUserCommissionService iReferralUserCommissionService;

    @Resource
    private IReferralCommissionService iReferralCommissionService;

    @Override
    public WelfareSummaryVo summary(Long userId) {
        UserWelfare userWelfare = getOrCreateUserWelfare(userId);
        WelfareSummaryVo vo = new WelfareSummaryVo();
        vo.setCoinBalance(defaultInt(userWelfare.getCoinBalance()));
        vo.setContinuousSignDays(defaultInt(userWelfare.getContinuousSignDays()));
        vo.setContinuousSignEnabled(isContinuousSignEnabled(userWelfare));
        vo.setSignedToday(isSignedToday(userWelfare));
        return vo;
    }

    @Override
    public List<WelfareTaskVo> listTasks() {
        List<WelfareTask> taskList = iWelfareTaskService.list(new LambdaQueryWrapper<WelfareTask>()
                .eq(WelfareTask::getState, STATE_ENABLED)
                .orderByAsc(WelfareTask::getSort)
                .orderByDesc(WelfareTask::getCreateTime));
        if (CollectionUtils.isEmpty(taskList)) {
            return Collections.emptyList();
        }
        return taskList.stream().map(task -> {
            WelfareTaskVo vo = new WelfareTaskVo();
            BeanUtils.copyProperties(task, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WelfareSignResultVo sign(Long userId) {
        UserWelfare userWelfare = getOrCreateUserWelfare(userId);
        LocalDate today = LocalDate.now();
        if (isSignedToday(userWelfare)) {
            throw new BizException("今日已签到");
        }

        int dailyCoin = resolveDailyCoin();
        int continuousDays = isContinuousSignEnabled(userWelfare)
                ? calcNextContinuousDays(userWelfare, today)
                : 0;
        int totalDays = resolveTotalSignDays();
        if (continuousDays > totalDays) {
            continuousDays = 1;
        }

        userWelfare.setCoinBalance(defaultInt(userWelfare.getCoinBalance()) + dailyCoin);
        userWelfare.setContinuousSignDays(continuousDays);
        userWelfare.setLastSignDate(today);
        iUserWelfareService.updateById(userWelfare);

        UserSignRecord signRecord = new UserSignRecord();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        signRecord.setCoinReward(dailyCoin);
        iUserSignRecordService.save(signRecord);

        WelfareSignResultVo vo = new WelfareSignResultVo();
        vo.setCoinReward(dailyCoin);
        vo.setContinuousSignDays(continuousDays);
        vo.setCoinBalance(userWelfare.getCoinBalance());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WelfareSummaryVo updateContinuousSignEnabled(Long userId, WelfareContinuousSignBo continuousSignBo) {
        UserWelfare userWelfare = getOrCreateUserWelfare(userId);
        boolean enabled = Boolean.TRUE.equals(continuousSignBo.getEnabled());
        userWelfare.setContinuousSignEnabled(enabled ? STATE_ENABLED : STATE_DISABLED);
        if (!enabled) {
            userWelfare.setContinuousSignDays(0);
        }
        iUserWelfareService.updateById(userWelfare);
        return summary(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WelfareWithdrawResultVo withdraw(Long userId, WelfareWithdrawBo withdrawBo) {
        BigDecimal amount = withdrawBo.getAmount();
        if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("提现金额必须大于0");
        }

        int withdrawAmount = amount.intValue();
        if (amount.compareTo(new BigDecimal(withdrawAmount)) != 0) {
            throw new BizException("提现金币必须为整数");
        }

        int minAmount = resolveWithdrawMinAmount();
        if (withdrawAmount < minAmount) {
            throw new BizException("最小提现金额为" + minAmount);
        }

        UserWelfare userWelfare = getOrCreateUserWelfare(userId);
        int coinBalance = defaultInt(userWelfare.getCoinBalance());
        if (withdrawAmount > coinBalance) {
            throw new BizException("金币余额不足");
        }

        userWelfare.setCoinBalance(coinBalance - withdrawAmount);
        iUserWelfareService.updateById(userWelfare);

        BigDecimal coinWalletRate = resolveCoinWalletRate();
        BigDecimal walletCredit = new BigDecimal(withdrawAmount)
                .multiply(coinWalletRate)
                .setScale(2, RoundingMode.HALF_UP);
        if (walletCredit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("兑换后金额必须大于0，请调整提现积分或兑换比例");
        }

        syncReferralCommissionOnWelfareWithdraw(userId, walletCredit);

        UserWallet userWallet = getOrCreateUserWallet(userId);
        BigDecimal walletAmount = defaultAmount(userWallet.getAmount()).add(walletCredit);
        iUserWalletService.incr(userId, walletCredit);
        // 余额明细依赖 user_wallet_transaction 流水，积分提现到钱包需同步写入收入记录
        recordWalletIncome(
                userId,
                walletCredit,
                "环保积分提现到账",
                String.format("环保积分提现到余额: %s积分×%s=￥%s",
                        withdrawAmount, coinWalletRate.toPlainString(), walletCredit.toPlainString())
        );

        WelfareWithdrawResultVo vo = new WelfareWithdrawResultVo();
        vo.setWithdrawAmount(withdrawAmount);
        vo.setCoinBalance(userWelfare.getCoinBalance());
        vo.setWalletAmount(walletAmount);
        return vo;
    }

    @Override
    public IPage<WelfareCoinLogVo> coinLogPage(Long userId, WelfareCoinLogPageBo pageBo) {
        Page<UserSignRecord> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<UserSignRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSignRecord::getUserId, userId)
                .orderByDesc(UserSignRecord::getCreateTime)
                .orderByDesc(UserSignRecord::getId);
        IPage<UserSignRecord> page = iUserSignRecordService.page(initPage, queryWrapper);
        List<WelfareCoinLogVo> records = page.getRecords().stream()
                .map(this::toCoinLogVo)
                .collect(Collectors.toList());
        return PageUtil.copyPage(page, records);
    }

    private WelfareCoinLogVo toCoinLogVo(UserSignRecord record) {
        WelfareCoinLogVo vo = new WelfareCoinLogVo();
        vo.setId(record.getId());
        vo.setTitle("每日签到");
        vo.setDescription(record.getSignDate() == null ? "签到奖励" : "签到日期 " + record.getSignDate());
        vo.setCoinAmount(defaultInt(record.getCoinReward()));
        vo.setType(COIN_LOG_INCOME);
        vo.setSignDate(record.getSignDate());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    private void recordWalletIncome(Long userId, BigDecimal amount, String title, String description) {
        UserWalletTransaction transaction = new UserWalletTransaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TX_TYPE_SYSTEM);
        transaction.setBizType(BIZ_TYPE_REWARD);
        transaction.setDirection(DIRECTION_IN);
        transaction.setStatus(TX_STATUS_DONE);
        transaction.setTitle(title);
        transaction.setDescription(description);
        try {
            iUserWalletTransactionService.save(transaction);
        } catch (Exception e) {
            log.warn("保存环保积分提现流水失败，userId={}, amount={}", userId, amount, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrReferralCoin(Long userId, BigDecimal commissionYuan) {
        int coins = convertCommissionToCoin(commissionYuan);
        if (coins <= 0) {
            return;
        }

        UserWelfare userWelfare = getOrCreateUserWelfare(userId);
        userWelfare.setCoinBalance(defaultInt(userWelfare.getCoinBalance()) + coins);
        iUserWelfareService.updateById(userWelfare);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrReferralCoin(Long userId, BigDecimal amountYuan) {
        int coins = convertCommissionToCoin(amountYuan);
        if (coins <= 0) {
            return;
        }

        UserWelfare userWelfare = getOrCreateUserWelfare(userId);
        int balance = defaultInt(userWelfare.getCoinBalance());
        userWelfare.setCoinBalance(Math.max(0, balance - coins));
        iUserWelfareService.updateById(userWelfare);
    }

    private void syncReferralCommissionOnWelfareWithdraw(Long userId, BigDecimal walletCredit) {
        ReferralUserCommission referralUserCommission = iReferralUserCommissionService.getByUserId(userId);
        if (Objects.isNull(referralUserCommission)) {
            return;
        }

        BigDecimal referralBalance = defaultAmount(referralUserCommission.getCommission());
        BigDecimal referralDeduct = defaultAmount(walletCredit).min(referralBalance);
        if (referralDeduct.compareTo(BigDecimal.ZERO) > 0) {
            iReferralUserCommissionService.decr(userId, referralDeduct);
            // 佣金明细页依赖 referral_commission 流水，提现到钱包需同步写入支出记录
            iReferralCommissionService.saveWithdraw(userId, referralDeduct);
        }
    }

    private BigDecimal resolveCoinWalletRate() {
        String rateStr = resolveConfigValue(CONFIG_COIN_WALLET_RATE, "1");
        try {
            BigDecimal rate = new BigDecimal(rateStr);
            return rate.compareTo(BigDecimal.ZERO) > 0 ? rate : BigDecimal.ONE;
        } catch (NumberFormatException ex) {
            return BigDecimal.ONE;
        }
    }

    private int convertCommissionToCoin(BigDecimal commissionYuan) {
        if (Objects.isNull(commissionYuan) || commissionYuan.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal rate = resolveReferralCoinRate();
        return commissionYuan.multiply(rate).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private BigDecimal resolveReferralCoinRate() {
        String rateStr = resolveConfigValue(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_COIN_RATE, "1");
        try {
            BigDecimal rate = new BigDecimal(rateStr);
            return rate.compareTo(BigDecimal.ZERO) > 0 ? rate : BigDecimal.ONE;
        } catch (NumberFormatException ex) {
            return BigDecimal.ONE;
        }
    }

    private UserWelfare getOrCreateUserWelfare(Long userId) {
        UserWelfare userWelfare = iUserWelfareService.getOne(new LambdaQueryWrapper<UserWelfare>()
                .eq(UserWelfare::getUserId, userId)
                .last("LIMIT 1"));
        if (Objects.nonNull(userWelfare)) {
            return userWelfare;
        }

        userWelfare = new UserWelfare();
        userWelfare.setUserId(userId);
        userWelfare.setCoinBalance(0);
        userWelfare.setContinuousSignDays(0);
        userWelfare.setContinuousSignEnabled(STATE_ENABLED);
        iUserWelfareService.save(userWelfare);
        return userWelfare;
    }

    private boolean isContinuousSignEnabled(UserWelfare userWelfare) {
        return !STATE_DISABLED.equals(userWelfare.getContinuousSignEnabled());
    }

    private boolean isSignedToday(UserWelfare userWelfare) {
        return Objects.nonNull(userWelfare.getLastSignDate())
                && userWelfare.getLastSignDate().equals(LocalDate.now());
    }

    private int calcNextContinuousDays(UserWelfare userWelfare, LocalDate today) {
        LocalDate lastSignDate = userWelfare.getLastSignDate();
        if (Objects.isNull(lastSignDate)) {
            return 1;
        }
        if (lastSignDate.plusDays(1).equals(today)) {
            return defaultInt(userWelfare.getContinuousSignDays()) + 1;
        }
        return 1;
    }

    private int resolveTotalSignDays() {
        return Math.max(1, NumberUtils.toInt(resolveConfigValue(CONFIG_SIGN_TOTAL_DAYS, "7"), 7));
    }

    private int resolveDailyCoin() {
        return Math.max(0, NumberUtils.toInt(resolveConfigValue(CONFIG_SIGN_DAILY_COIN, "5"), 5));
    }

    private int resolveWithdrawMinAmount() {
        return Math.max(1, NumberUtils.toInt(resolveConfigValue(CONFIG_WITHDRAW_MIN_AMOUNT, "10"), 10));
    }

    private UserWallet getOrCreateUserWallet(Long userId) {
        UserWallet userWallet = iUserWalletService.getOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("LIMIT 1"));
        if (Objects.nonNull(userWallet)) {
            return userWallet;
        }

        userWallet = new UserWallet();
        userWallet.setUserId(userId);
        userWallet.setAmount(BigDecimal.ZERO);
        iUserWalletService.save(userWallet);
        return userWallet;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }

    private String resolveConfigValue(String key, String defaultValue) {
        SysConfigInfoBO configInfoBO = new SysConfigInfoBO();
        configInfoBO.setKey(key);
        String value = appConfigService.info(configInfoBO).getValue();
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private int defaultInt(Integer value) {
        return Objects.isNull(value) ? 0 : value;
    }
}
