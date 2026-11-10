package com.sqx.app.welfare.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.welfare.domain.bo.WelfareCoinLogPageBo;
import com.sqx.app.welfare.domain.bo.WelfareContinuousSignBo;
import com.sqx.app.welfare.domain.bo.WelfareWithdrawBo;
import com.sqx.app.welfare.domain.vo.WelfareCoinLogVo;
import com.sqx.app.welfare.domain.vo.WelfareSignResultVo;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
import com.sqx.app.welfare.domain.vo.WelfareTaskVo;
import com.sqx.app.welfare.domain.vo.WelfareWithdrawResultVo;

import java.math.BigDecimal;
import java.util.List;

public interface WelfareService {

    WelfareSummaryVo summary(Long userId);

    List<WelfareTaskVo> listTasks();

    WelfareSignResultVo sign(Long userId);

    WelfareSummaryVo updateContinuousSignEnabled(Long userId, WelfareContinuousSignBo continuousSignBo);

    WelfareWithdrawResultVo withdraw(Long userId, WelfareWithdrawBo withdrawBo);

    IPage<WelfareCoinLogVo> coinLogPage(Long userId, WelfareCoinLogPageBo pageBo);

    /**
     * 推荐佣金同步入账福利金币
     */
    void incrReferralCoin(Long userId, BigDecimal commissionYuan);

    /**
     * 推荐佣金提现时同步扣减福利金币
     */
    void decrReferralCoin(Long userId, BigDecimal amountYuan);
}
