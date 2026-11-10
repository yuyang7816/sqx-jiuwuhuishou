package com.sqx.app.referral.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.referral.commission.domain.vo.ReferralLeaderboardVo;
import com.sqx.app.referral.commission.domain.vo.ReferralListVo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionInfoBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionPageBo;
import com.sqx.app.referral.user.domain.bo.ReferralUserCommissionUpdateBo;
import com.sqx.app.referral.user.domain.bo.ReferralCommissionWithdrawBo;
import com.sqx.app.referral.user.domain.vo.InvitePosterVo;
import com.sqx.app.referral.user.domain.vo.ReferralCommissionWithdrawResultVo;
import com.sqx.app.referral.user.domain.vo.ReferralDataVo;
import com.sqx.app.referral.user.domain.vo.ReferralLawyerVo;
import com.sqx.app.referral.user.domain.vo.ReferralUserCommissionVo;

import java.util.List;


/**
 * 用户佣金 服务类
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-04 14:44:53
 */
public interface ReferralUserCommissionService {

    ReferralUserCommissionVo info(Long userId);

    IPage<ReferralUserCommissionVo> pageInfo(ReferralUserCommissionPageBo pageBo);

    void updateInfo(ReferralUserCommissionUpdateBo referralUserCommissionUpdateBo);

    void delete(ReferralUserCommissionInfoBo referralUserCommissionInfoBo);

    /**
     * 订单完成后，保存佣金
     * @param orderId 订单id
     */
    void saveUserCommission(Long orderId);

    ReferralDataVo referralData(Long userId);

    IPage<UserEntity> referralUserList(Long userId);

    IPage<ReferralListVo> referralList(Long userId);

    ReferralLawyerVo lawyer();

    List<ReferralLeaderboardVo> leaderboard();

    /**
     * 生成邀请海报素材（背景图取自管理端首页装修-邀请背景图）
     */
    InvitePosterVo invitePoster(Long userId);

    ReferralCommissionWithdrawResultVo withdrawToWallet(Long userId, ReferralCommissionWithdrawBo withdrawBo);
}
