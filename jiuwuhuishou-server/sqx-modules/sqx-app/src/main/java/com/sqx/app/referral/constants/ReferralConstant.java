package com.sqx.app.referral.constants;

/**
 * @author chengming
 * @since 2026/2/6
 */
public interface ReferralConstant {

    interface commission {
        /**
         * 订单推荐佣金比例（小数，如 0.1 表示 10%）
         */
        String BIZ_REFERRAL_COMMISSION_RATE = "biz_referral_commission_rate";

        /**
         * 推荐佣金折算福利金币比例（1 表示 1 元佣金 = 1 金币）
         */
        String BIZ_REFERRAL_COMMISSION_COIN_RATE = "biz_referral_commission_coin_rate";
    }

    interface withdraw {
        /**
         * 最大提现金额
         */
        String SYS_WITHDRAW_MAX = "sys_withdraw_max";

        /**
         * 最小提现金额
         */
        String SYS_WITHDRAW_MIN = "sys_withdraw_min";

        /**
         * 提现费率
         */
        String SYS_WITHDRAW_RATE = "sys_withdraw_rate";
    }

}
