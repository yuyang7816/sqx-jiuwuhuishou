package com.sqx.app.express.sf;

import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.express.sf.domain.SfPlaceOrderResult;
import com.sqx.app.express.sf.domain.SfRouteNode;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 顺丰管家业务封装：下单 / 取消 / 路由
 */
@Slf4j
@Service
public class SfExpressOrderService {

    private static final DateTimeFormatter SEND_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_RECEIVER = "平台仓储部";
    private static final String DEFAULT_RECEIVER_PHONE = "400-800-6688";
    private static final String DEFAULT_RECEIVER_ADDRESS = "上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心";
    private static final String DEFAULT_PROVINCE = "上海市";
    private static final String DEFAULT_CITY = "上海市";
    private static final String DEFAULT_COUNTY = "浦东新区";

    @Resource
    private SfButlerClient sfButlerClient;

    public boolean isConfigured() {
        return sfButlerClient.isConfigured();
    }

    public String placeMailOrder(RecycleOrder order, UserAddress senderAddress) {
        if (Objects.isNull(order) || Objects.isNull(senderAddress)) {
            throw new BizException("顺丰下单参数不完整");
        }
        if (!sfButlerClient.isConfigured()) {
            throw new BizException("顺丰管家凭证未配置，请先在系统配置中填写 sf_butler_app_id / sf_butler_sk / sf_butler_company_id");
        }
        Map<String, Object> body = buildPlaceOrderBody(order, senderAddress);
        SfPlaceOrderResult result = sfButlerClient.placeOrder(body);
        log.info("顺丰下单成功: orderNo={}, mailNo={}", order.getOrderNo(), result.getMailNo());
        return result.getMailNo();
    }

    public void cancelMailOrder(RecycleOrder order) {
        if (Objects.isNull(order) || StringUtils.isBlank(order.getExpressNo())) {
            return;
        }
        // 与下单一致：凭证未配置时不阻断业务取消（本地订单仍可取消）
        if (!sfButlerClient.isConfigured()) {
            log.warn("顺丰管家凭证未配置，跳过取消运单: orderNo={}, expressNo={}，请在系统配置填写 sf_butler_app_id / sf_butler_sk / sf_butler_company_id",
                    order.getOrderNo(), order.getExpressNo());
            return;
        }
        String companyId = sfButlerClient.requireCompanyId();
        sfButlerClient.cancelOrder(companyId, order.getOrderNo());
        log.info("顺丰取消成功: orderNo={}, expressNo={}", order.getOrderNo(), order.getExpressNo());
    }

    public List<ExpressTrackVo> listExpressTracks(String expressNo) {
        return listExpressTracks(expressNo, null);
    }

    /**
     * @param checkPhoneNo 收/寄件人手机号后四位，手工补录运单时通常必填
     */
    public List<ExpressTrackVo> listExpressTracks(String expressNo, String checkPhoneNo) {
        if (StringUtils.isBlank(expressNo)) {
            return new ArrayList<>();
        }
        if (!sfButlerClient.isConfigured()) {
            throw new BizException("顺丰管家凭证未配置，请在系统配置填写 sf_butler_app_id / sf_butler_sk / sf_butler_company_id 并刷新缓存");
        }
        try {
            String companyId = sfButlerClient.requireCompanyId();
            List<SfRouteNode> routes = sfButlerClient.listRoutes(companyId, expressNo, checkPhoneNo);
            return toTrackVos(routes);
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("查询顺丰路由失败: expressNo={}, err={}", expressNo, ex.getMessage());
            throw new BizException("查询物流轨迹失败：" + StringUtils.defaultIfBlank(ex.getMessage(), "请稍后重试"));
        }
    }

    /**
     * 组装顺丰路由查询 checkPhoneNo（手机号后四位，多个用逗号分隔）
     */
    public static String buildCheckPhoneNo(String... phones) {
        if (phones == null || phones.length == 0) {
            return null;
        }
        List<String> last4List = new ArrayList<>();
        for (String phone : phones) {
            String digits = StringUtils.trimToEmpty(phone).replaceAll("\\D", "");
            if (digits.length() < 4) {
                continue;
            }
            String last4 = digits.substring(digits.length() - 4);
            if (!last4List.contains(last4)) {
                last4List.add(last4);
            }
        }
        if (last4List.isEmpty()) {
            return null;
        }
        return String.join(",", last4List);
    }

    private Map<String, Object> buildPlaceOrderBody(RecycleOrder order, UserAddress sender) {
        String companyId = sfButlerClient.requireCompanyId();
        String custid = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.CUSTID), "9999999999");
        String payMethod = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.PAY_METHOD), "0");
        String expressType = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.EXPRESS_TYPE), "2");

        String dContact = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER), DEFAULT_RECEIVER);
        String dMobile = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_PHONE), DEFAULT_RECEIVER_PHONE);
        String dAddress = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_ADDRESS), DEFAULT_RECEIVER_ADDRESS);
        String dProvince = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_PROVINCE), DEFAULT_PROVINCE);
        String dCity = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_CITY), DEFAULT_CITY);
        String dCounty = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_COUNTY), DEFAULT_COUNTY);

        Map<String, Object> body = new HashMap<>();
        body.put("orderId", order.getOrderNo());
        // 文档要求 companyId 为 Long；非纯数字时按字符串透传
        try {
            body.put("companyId", Long.valueOf(companyId));
        } catch (NumberFormatException ex) {
            body.put("companyId", companyId);
        }
        body.put("jContact", sender.getContactName());
        body.put("jMobile", sender.getContactMobile());
        body.put("jTel", sender.getContactMobile());
        body.put("jProvince", sender.getProvince());
        body.put("jCity", sender.getCity());
        body.put("jCounty", sender.getDistrict());
        body.put("jAddress", buildSenderDetailAddress(sender));
        body.put("dContact", dContact);
        body.put("dMobile", normalizeMobile(dMobile));
        body.put("dTel", dMobile);
        body.put("dProvince", dProvince);
        body.put("dCity", dCity);
        body.put("dCounty", dCounty);
        body.put("dAddress", dAddress);
        body.put("custid", custid);
        body.put("payMethod", payMethod);
        body.put("expressType", expressType);
        body.put("depositumInfo", StringUtils.defaultIfBlank(order.getCategoryName(), "旧物回收"));
        body.put("depositumNo", "1");
        body.put("isDoCall", "1");
        body.put("packagesNo", "1");
        if (Objects.nonNull(order.getAppointmentTime())) {
            body.put("sendStartTime", order.getAppointmentTime().format(SEND_TIME_FORMATTER));
        }
        if (StringUtils.isNotBlank(order.getRemark())) {
            body.put("remark", StringUtils.left(order.getRemark(), 100));
        }
        return body;
    }

    private static String buildSenderDetailAddress(UserAddress sender) {
        StringBuilder builder = new StringBuilder();
        append(builder, sender.getStreet());
        append(builder, sender.getDetailAddress());
        String detail = builder.toString().trim();
        if (StringUtils.isNotBlank(detail)) {
            return detail;
        }
        // 若无明细，拼完整地址避免 SF 拒单
        StringBuilder full = new StringBuilder();
        append(full, sender.getProvince());
        append(full, sender.getCity());
        append(full, sender.getDistrict());
        append(full, sender.getStreet());
        append(full, sender.getDetailAddress());
        return full.toString().trim();
    }

    private static void append(StringBuilder builder, String part) {
        if (StringUtils.isBlank(part)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(part.trim());
    }

    private static String normalizeMobile(String phone) {
        if (StringUtils.isBlank(phone)) {
            return phone;
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() >= 11) {
            return digits.substring(digits.length() - 11);
        }
        return digits;
    }

    private static List<ExpressTrackVo> toTrackVos(List<SfRouteNode> routes) {
        List<ExpressTrackVo> tracks = new ArrayList<>();
        if (CollectionUtils.isEmpty(routes)) {
            return tracks;
        }
        for (int i = 0; i < routes.size(); i++) {
            SfRouteNode route = routes.get(i);
            ExpressTrackVo vo = new ExpressTrackVo();
            String remark = StringUtils.defaultIfBlank(route.getRemark(), "物流更新");
            vo.setTitle(StringUtils.defaultIfBlank(route.getOpCode(), truncateTitle(remark)));
            vo.setTime(StringUtils.defaultIfBlank(route.getAcceptTime(), ""));
            String desc = remark;
            if (StringUtils.isNotBlank(route.getAcceptAddress())) {
                desc = remark + "（" + route.getAcceptAddress() + "）";
            }
            vo.setDesc(desc);
            vo.setActive(i == 0);
            tracks.add(vo);
        }
        return tracks;
    }

    private static String truncateTitle(String remark) {
        if (StringUtils.length(remark) <= 12) {
            return remark;
        }
        return remark.substring(0, 12);
    }
}
