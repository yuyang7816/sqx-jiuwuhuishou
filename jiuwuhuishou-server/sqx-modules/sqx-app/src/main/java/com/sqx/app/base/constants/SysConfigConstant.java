package com.sqx.app.base.constants;

/**
 * @author chengming
 * @since 2025/12/19
 */
public interface SysConfigConstant {

    /**
     * 系统配置
     */
    interface Sys {
        /**
         * 用户端小程序appid
         */
        String SYS_USER_MA_APPID = "sys_user_ma_appid";

        /**
         * 用户端小程序secret
         */
        String SYS_USER_MA_SECRET = "sys_user_ma_secret";

        /**
         * h5服务域名配置
         */
        String SYS_H5_DOMAIN = "sys_h5_domain";
    }

    /**
     * 平台邮寄收件信息
     */
    interface PlatformMail {
        String RECEIVER = "platform_mail_receiver";
        String RECEIVER_PHONE = "platform_mail_receiver_phone";
        String RECEIVER_ADDRESS = "platform_mail_receiver_address";
        String RECEIVER_PROVINCE = "platform_mail_receiver_province";
        String RECEIVER_CITY = "platform_mail_receiver_city";
        String RECEIVER_COUNTY = "platform_mail_receiver_county";
    }

    /**
     * 顺丰快递管家
     */
    interface SfButler {
        String BASE_URL = "sf_butler_base_url";
        String APP_ID = "sf_butler_app_id";
        String SK = "sf_butler_sk";
        String COMPANY_ID = "sf_butler_company_id";
        String CUSTID = "sf_butler_custid";
        String PAY_METHOD = "sf_butler_pay_method";
        String EXPRESS_TYPE = "sf_butler_express_type";
        String ROUTE_URI = "sf_butler_route_uri";
    }

    /**
     * 阿里云市场·全国快递物流查询
     */
    interface AliyunLogistics {
        /**
         * 买家中心 AppCode（Authorization: APPCODE xxx）
         */
        String APP_CODE = "aliyun_logistics_appcode";
    }
}
