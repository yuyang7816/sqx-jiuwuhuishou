package com.sqx.app.referral.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.base.user.enums.UserStatus;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
import com.sqx.app.referral.commission.domain.vo.ReferralLeaderboardVo;
import com.sqx.app.referral.commission.domain.vo.ReferralListVo;
import com.sqx.app.referral.commission.enums.Direction;
import com.sqx.app.referral.commission.enums.ReferralCommissionType;
import com.sqx.app.referral.constants.ReferralConstant;
import com.sqx.app.referral.user.db.service.IReferralUserCommissionService;
import com.sqx.app.referral.user.domain.bo.ReferralCommissionWithdrawBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionInfoBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionPageBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionUpdateBo;
import com.sqx.app.referral.user.domain.entity.ReferralUserCommission;
import com.sqx.app.referral.user.domain.vo.InvitePosterVo;
import com.sqx.app.referral.user.domain.vo.ReferralCommissionWithdrawResultVo;
import com.sqx.app.referral.user.domain.vo.ReferralDataVo;
import com.sqx.app.referral.user.domain.vo.ReferralLawyerVo;
import com.sqx.app.referral.user.domain.vo.ReferralUserCommissionVo;
import com.sqx.app.referral.user.service.ReferralUserCommissionService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.utils.DateUtil;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.welfare.service.WelfareService;
import com.sqx.app.utils.model.StartAndEndTime;
import com.sqx.app.base.enums.ClientEnum;
import com.sqx.app.base.utils.wx.WxUtil;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import com.sqx.common.shiro.utils.UserContextHolder;
import com.sqx.system.db.entity.Banner;
import com.sqx.system.db.service.IBannerService;
import com.sqx.system.db.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户佣金 服务实现类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-04 14:44:53
 */
@Slf4j
@Service
public class ReferralUserCommissionServiceImpl implements ReferralUserCommissionService {

    private static final int LEADERBOARD_LIMIT = 50;

    @Resource
    private IReferralUserCommissionService iReferralUserCommissionService;
    @Resource
    private ISysConfigService iSysConfigService;
    @Resource
    private IUserService iUserService;
    @Resource
    private IReferralCommissionService iReferralCommissionService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private WelfareService welfareService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private IBannerService iBannerService;
    @Resource
    private WxUtil wxUtil;
    @Resource
    private IUserWalletService iUserWalletService;
    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;

    /** 管理端首页装修：邀请背景图 */
    private static final int BANNER_CLASSIFY_INVITE_BG = 4;
    private static final int BANNER_STATE_ENABLED = 1;
    private static final String INVITE_LANDING_PAGE = "pages/index/index";
    private static final String DEFAULT_POSTER_TITLE = "邀请好友 共享返佣";
    private static final String DEFAULT_POSTER_DESC = "我正在参与旧物回收环保行动，邀请你一起为地球减负，首单回收即领惊喜红包！";
    private static final int TX_TYPE_SYSTEM = 5;
    private static final int BIZ_TYPE_REWARD = 3;
    private static final int DIRECTION_IN = 1;
    private static final int TX_STATUS_DONE = 2;

    @Override
    public IPage<ReferralUserCommissionVo> pageInfo(ReferralUserCommissionPageBo pageBo) {
        Page<ReferralUserCommission> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        QueryWrapper<ReferralUserCommission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(userIds != null, ReferralUserCommission::getUserId, userIds)
                // 佣金金额排名：余额高的在前
                .orderByDesc(ReferralUserCommission::getCommission)
                .orderByDesc(ReferralUserCommission::getId);

        IPage<ReferralUserCommission> iPage = iReferralUserCommissionService.page(initPage, queryWrapper);
        List<ReferralUserCommissionVo> voList = new ArrayList<>();
        if (iPage.getRecords() != null) {
            for (ReferralUserCommission record : iPage.getRecords()) {
                ReferralUserCommissionVo vo = new ReferralUserCommissionVo();
                BeanUtils.copyProperties(record, vo);
                adminLookupSupport.fillUserFields(record.getUserId(), vo);
                voList.add(vo);
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public ReferralUserCommissionVo info(Long userId) {
        ReferralUserCommission referralUserCommission = iReferralUserCommissionService.getOne(
                new LambdaQueryWrapper<ReferralUserCommission>().eq(ReferralUserCommission::getUserId, userId));
        if (Objects.isNull(referralUserCommission)) {
            referralUserCommission = new ReferralUserCommission();
            referralUserCommission.setUserId(userId);
            referralUserCommission.setCommission(BigDecimal.ZERO);
            iReferralUserCommissionService.save(referralUserCommission);
        }

        ReferralUserCommissionVo vo = new ReferralUserCommissionVo();
        BeanUtils.copyProperties(referralUserCommission, vo);
        adminLookupSupport.fillUserFields(userId, vo);
        return vo;
    }

    @Override
    public void updateInfo(ReferralUserCommissionUpdateBo referralUserCommissionUpdateBo) {
        ReferralUserCommission referralUserCommission = new ReferralUserCommission();
        BeanUtils.copyProperties(referralUserCommissionUpdateBo, referralUserCommission);

        iReferralUserCommissionService.updateById(referralUserCommission);
    }

    @Override
    public void delete(ReferralUserCommissionInfoBo referralUserCommissionInfoBo) {
        iReferralUserCommissionService.removeById(referralUserCommissionInfoBo.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserCommission(Long orderId) {
        long exists = iReferralCommissionService.count(new LambdaQueryWrapper<ReferralCommission>()
                .eq(ReferralCommission::getOrderId, orderId)
                .eq(ReferralCommission::getType, ReferralCommissionType.ORDER.getValue()));
        if (exists > 0) {
            log.info("订单[{}]佣金已结算，跳过", orderId);
            return;
        }

        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order)) {
            log.info("订单[{}]不存在，不计算佣金", orderId);
            return;
        }

        if (!RecycleOrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            log.info("订单[{}]未完成，不计算佣金", orderId);
            return;
        }

        UserEntity user = iUserService.getById(order.getUserId());
        if (Objects.isNull(user) || StringUtils.isBlank(user.getSuperiorInvitationCode())) {
            log.info("订单[{}]用户无上级邀请码，不计算佣金", orderId);
            return;
        }

        UserEntity inviteUser = iUserService.getOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getInvitationCode, user.getSuperiorInvitationCode())
                .last("LIMIT 1"));
        if (Objects.isNull(inviteUser)) {
            log.info("订单[{}]邀请用户不存在，不计算佣金", orderId);
            return;
        }

        if (Objects.equals(inviteUser.getUserId(), order.getUserId())) {
            log.info("订单[{}]邀请人与下单用户相同，不计算佣金", orderId);
            return;
        }

        if (Objects.equals(UserStatus.DISABLE.getCode(), inviteUser.getStatus())) {
            log.info("邀请用户[{}]被禁用，取消计算佣金", inviteUser.getUserId());
            return;
        }

        BigDecimal payAmount = order.getActualAmount();
        if (Objects.isNull(payAmount) || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            payAmount = order.getEstimateAmount();
        }
        if (Objects.isNull(payAmount) || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("订单[{}]成交金额为 0，不计算佣金", orderId);
            return;
        }

        String commissionRateStr = iSysConfigService.getConfigValue(ReferralConstant.commission.BIZ_REFERRAL_COMMISSION_RATE);
        if (StringUtils.isBlank(commissionRateStr)) {
            commissionRateStr = "0.1";
        }
        BigDecimal commissionRate = new BigDecimal(commissionRateStr);
        BigDecimal commission = payAmount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);
        if (commission.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("订单[{}]佣金金额为 0，不计算佣金", orderId);
            return;
        }

        iReferralUserCommissionService.incr(inviteUser.getUserId(), commission);

        ReferralCommission referralCommission = new ReferralCommission();
        referralCommission.setUserId(inviteUser.getUserId());
        referralCommission.setOrderId(orderId);
        referralCommission.setRate(commissionRate);
        referralCommission.setAmount(commission);
        referralCommission.setType(ReferralCommissionType.ORDER.getValue());
        referralCommission.setDirection(Direction.INCR.getValue());
        referralCommission.setTitle("邀请奖励");
        referralCommission.setDescription("好友完成订单：" + order.getOrderNo());
        iReferralCommissionService.save(referralCommission);
        welfareService.incrReferralCoin(inviteUser.getUserId(), commission);
    }

    @Override
    public ReferralDataVo referralData(Long userId) {
        UserEntity user = iUserService.getById(userId);
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }

        ReferralDataVo vo = new ReferralDataVo();
        String invitationCode = user.getInvitationCode();
        if (StringUtils.isBlank(invitationCode)) {
            vo.setNum(0L);
            vo.setMonthNum(0L);
        } else {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserEntity::getSuperiorInvitationCode, invitationCode);
            long count = iUserService.count(queryWrapper);

            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime nextMonthStart = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();
            QueryWrapper<UserEntity> monthWrapper = new QueryWrapper<>();
            monthWrapper.lambda()
                    .eq(UserEntity::getSuperiorInvitationCode, invitationCode)
                    .ge(UserEntity::getCreateTime, monthStart)
                    .lt(UserEntity::getCreateTime, nextMonthStart);
            long monthCount = iUserService.count(monthWrapper);

            vo.setNum(count);
            vo.setMonthNum(monthCount);
        }

        BigDecimal commission = iReferralCommissionService.sumUserCommission(userId);
        if (Objects.isNull(commission)) {
            commission = BigDecimal.ZERO;
        }

        vo.setCommission(commission);
        return vo;
    }

    @Override
    public IPage<UserEntity> referralUserList(Long userId) {
        Page<UserEntity> page = new Page<>(1, 10);

        UserEntity user = iUserService.getById(userId);
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getSuperiorInvitationCode, user.getInvitationCode());
        IPage<UserEntity> userEntityPage = iUserService.page(page, queryWrapper);

        return userEntityPage;
    }

    @Override
    public IPage<ReferralListVo> referralList(Long userId) {
        Page<ReferralCommission> page = new Page<>(1, 10);

        IPage<ReferralListVo> voIPage = iReferralCommissionService.referralList(page, userId);

        return voIPage;
    }

    @Override
    public ReferralLawyerVo lawyer() {
        ReferralCommission referralCommission = iReferralCommissionService.getByUserId(UserContextHolder.getAppUserId());
        if (Objects.isNull(referralCommission)) {
            log.error("用户[{}]资产不存在", UserContextHolder.getAppUserId());
            throw new BizException("用户资产不存在");
        }

        // 昨日收益
        StartAndEndTime startAndEndTime = DateUtil.getStartAndEndTime(LocalDate.now().plusDays(1));
        BigDecimal yesterdayEarn = iReferralCommissionService.sumAmount(UserContextHolder.getAppUserId(), startAndEndTime.getStartTime(), startAndEndTime.getEndTime());

        ReferralLawyerVo vo = new ReferralLawyerVo();
        vo.setAmount(referralCommission.getAmount());
        vo.setYesterdayEarn(yesterdayEarn);
        return vo;
    }

    @Override
    public List<ReferralLeaderboardVo> leaderboard() {
        List<ReferralLeaderboardVo> list = iReferralCommissionService.leaderboard(LEADERBOARD_LIMIT);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public InvitePosterVo invitePoster(Long userId) {
        UserEntity user = iUserService.getById(userId);
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }
        if (StringUtils.isBlank(user.getInvitationCode())) {
            throw new BizException("邀请码不存在，请重新登录后再试");
        }

        Banner banner = iBannerService.getOne(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getClassify, BANNER_CLASSIFY_INVITE_BG)
                .eq(Banner::getState, BANNER_STATE_ENABLED)
                .orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getCreateTime)
                .last("LIMIT 1"));
        if (Objects.isNull(banner) || StringUtils.isBlank(banner.getImageUrl())) {
            throw new BizException("暂未配置邀请背景图，请联系管理员在首页装修中上传");
        }

        String invitationCode = user.getInvitationCode().trim();
        String invitePath = INVITE_LANDING_PAGE + "?invitationCode=" + invitationCode;

        InvitePosterVo vo = new InvitePosterVo();
        vo.setBackgroundImageUrl(banner.getImageUrl());
        vo.setUserName(StringUtils.defaultIfBlank(user.getUserName(), "旧物回收用户"));
        vo.setAvatar(user.getAvatar());
        vo.setInvitationCode(invitationCode);
        vo.setInvitePath(invitePath);
        vo.setTitle(StringUtils.defaultIfBlank(banner.getName(), DEFAULT_POSTER_TITLE));
        vo.setDescription(StringUtils.defaultIfBlank(banner.getDescription(), DEFAULT_POSTER_DESC));

        try {
            String qrCodeImage = wxUtil.getWxaCodeBase64(invitationCode, INVITE_LANDING_PAGE, ClientEnum.USER.getCode());
            vo.setQrCodeImage(qrCodeImage);
        } catch (Exception e) {
            log.warn("生成邀请小程序码失败，将由前端降级绘制二维码: {}", e.getMessage());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReferralCommissionWithdrawResultVo withdrawToWallet(Long userId, ReferralCommissionWithdrawBo withdrawBo) {
        BigDecimal amount = withdrawBo == null ? null : withdrawBo.getAmount();
        if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("提现金额必须大于0");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("提现金额必须大于0");
        }

        ReferralUserCommission commission = iReferralUserCommissionService.getByUserId(userId);
        BigDecimal balance = Objects.isNull(commission) || Objects.isNull(commission.getCommission())
                ? BigDecimal.ZERO
                : commission.getCommission();
        if (amount.compareTo(balance) > 0) {
            throw new BizException("佣金余额不足");
        }

        iReferralUserCommissionService.decr(userId, amount);
        iReferralCommissionService.saveWithdraw(userId, amount);
        welfareService.decrReferralCoin(userId, amount);

        UserWallet wallet = getOrCreateUserWallet(userId);
        BigDecimal walletAmount = defaultAmount(wallet.getAmount()).add(amount);
        iUserWalletService.incr(userId, amount);
        recordWalletIncome(userId, amount);

        ReferralCommissionWithdrawResultVo vo = new ReferralCommissionWithdrawResultVo();
        vo.setWithdrawAmount(amount);
        vo.setCommission(balance.subtract(amount));
        vo.setWalletAmount(walletAmount);
        return vo;
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

    private void recordWalletIncome(Long userId, BigDecimal amount) {
        UserWalletTransaction transaction = new UserWalletTransaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TX_TYPE_SYSTEM);
        transaction.setBizType(BIZ_TYPE_REWARD);
        transaction.setDirection(DIRECTION_IN);
        transaction.setStatus(TX_STATUS_DONE);
        transaction.setTitle("佣金提现到账");
        transaction.setDescription("佣金提现到钱包: " + amount.toPlainString());
        try {
            iUserWalletTransactionService.save(transaction);
        } catch (Exception e) {
            log.warn("保存佣金提现流水失败，userId={}, amount={}", userId, amount, e);
        }
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }
}
