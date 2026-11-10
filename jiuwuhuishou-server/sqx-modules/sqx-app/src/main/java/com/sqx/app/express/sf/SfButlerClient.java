package com.sqx.app.express.sf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.express.sf.domain.SfPlaceOrderResult;
import com.sqx.app.express.sf.domain.SfRouteNode;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.http.OkHttpUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 顺丰快递管家 API 客户端（签名验签模式）
 * <p>
 * sign = base64(sha512(body + SK + timestamp))
 * Header: sendAppId / sign / timestamp
 */
@Slf4j
@Component
public class SfButlerClient {

    private static final String CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String PLACE_ORDER_URI = "/public/order/v2/placeOrder";
    private static final String CANCEL_ORDER_URI = "/public/order/v2/cancelOrder";
    private static final String DEFAULT_BASE_URL = "https://butler-dev.sit.sf-express.com";
    private static final String DEFAULT_ROUTE_URI = "/public/route/v1/listRoute";

    public SfPlaceOrderResult placeOrder(Map<String, Object> body) {
        JSONObject resp = post(PLACE_ORDER_URI, body);
        assertOk(resp, "顺丰下单失败");
        JSONObject result = resp.getJSONObject("result");
        if (Objects.isNull(result)) {
            throw new BizException("顺丰下单失败：无返回结果");
        }
        SfPlaceOrderResult placeResult = new SfPlaceOrderResult();
        placeResult.setOrderId(StringUtils.defaultIfBlank(result.getString("orderid"), result.getString("orderId")));
        placeResult.setMailNo(extractMailNo(result));
        if (StringUtils.isBlank(placeResult.getMailNo())) {
            throw new BizException("顺丰下单失败：未返回运单号");
        }
        return placeResult;
    }

    public void cancelOrder(String companyId, String orderId) {
        Map<String, Object> body = new HashMap<>();
        body.put("companyId", companyId);
        body.put("orderId", orderId);
        JSONObject resp = post(CANCEL_ORDER_URI, body);
        if (!isOk(resp)) {
            String msg = StringUtils.defaultIfBlank(resp.getString("msg"), "顺丰取消订单失败");
            // 已取消场景允许幂等通过
            if (StringUtils.contains(msg, "已消") || StringUtils.contains(msg, "已取消")) {
                log.warn("顺丰取消订单幂等通过: orderId={}, msg={}", orderId, msg);
                return;
            }
            throw new BizException(msg);
        }
        JSONObject result = resp.getJSONObject("result");
        if (Objects.nonNull(result) && !"2".equals(String.valueOf(result.get("code")))) {
            throw new BizException("顺丰取消订单失败");
        }
    }

    public List<SfRouteNode> listRoutes(String companyId, String mailNo) {
        return listRoutes(companyId, mailNo, null);
    }

    public List<SfRouteNode> listRoutes(String companyId, String mailNo, String checkPhoneNo) {
        if (StringUtils.isBlank(mailNo)) {
            return Collections.emptyList();
        }
        String routeUri = StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.ROUTE_URI),
                DEFAULT_ROUTE_URI);
        Map<String, Object> body = new HashMap<>();
        // 文档要求 companyId 为数值类型时按 Long 透传
        try {
            body.put("companyId", Long.valueOf(companyId));
        } catch (NumberFormatException ex) {
            body.put("companyId", companyId);
        }
        body.put("trackingType", "1");
        body.put("trackingNumber", Collections.singletonList(mailNo));
        body.put("methodType", "1");
        if (StringUtils.isNotBlank(checkPhoneNo)) {
            body.put("checkPhoneNo", checkPhoneNo);
        }
        JSONObject resp = post(routeUri, body);
        if (!isOk(resp)) {
            String succ = StringUtils.trimToEmpty(resp.getString("succ"));
            String msg = StringUtils.defaultIfBlank(resp.getString("msg"), "顺丰路由查询失败");
            if ("login".equalsIgnoreCase(succ) || StringUtils.containsIgnoreCase(msg, "login")) {
                throw new BizException("顺丰管家鉴权失败，请检查系统配置 sf_butler_app_id / sf_butler_sk 并刷新缓存");
            }
            log.warn("顺丰路由查询失败: mailNo={}, succ={}, msg={}", mailNo, succ, msg);
            throw new BizException(msg);
        }
        return parseRoutes(resp.get("result"), mailNo);
    }

    /**
     * 是否已配置可调用顺丰管家的凭证（appId + sk + companyId）
     */
    public boolean isConfigured() {
        String appId = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.APP_ID));
        String sk = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.SK));
        return StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(sk)
                && StringUtils.isNotBlank(resolveCompanyIdOrNull());
    }

    public String requireCompanyId() {
        String companyId = resolveCompanyIdOrNull();
        if (StringUtils.isBlank(companyId)) {
            throw new BizException("顺丰管家 companyId 未配置，请在系统配置中填写 sf_butler_company_id 或 sf_butler_app_id");
        }
        return companyId;
    }

    private String resolveCompanyIdOrNull() {
        String companyId = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.COMPANY_ID));
        if (StringUtils.isBlank(companyId)) {
            companyId = StringUtils.trimToEmpty(
                    ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.APP_ID));
        }
        return StringUtils.trimToNull(companyId);
    }

    private JSONObject post(String uri, Map<String, Object> body) {
        String appId = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.APP_ID));
        String sk = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.SK));
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(sk)) {
            throw new BizException("顺丰管家凭证未配置，请先填写 sf_butler_app_id / sf_butler_sk");
        }
        String baseUrl = StringUtils.removeEnd(StringUtils.defaultIfBlank(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.SfButler.BASE_URL),
                DEFAULT_BASE_URL), "/");
        String jsonBody = JSON.toJSONString(body);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = sign(jsonBody, sk, timestamp);

        Map<String, String> headers = new HashMap<>();
        headers.put("sendAppId", appId);
        headers.put("sign", sign);
        headers.put("timestamp", timestamp);

        String url = baseUrl + uri;
        log.info("顺丰管家请求: uri={}, body={}", uri, jsonBody);
        String resp;
        try {
            resp = OkHttpUtil.doPost(url, jsonBody, CONTENT_TYPE, headers);
        } catch (BizException ex) {
            throw new BizException("顺丰接口调用失败");
        }
        log.info("顺丰管家响应: uri={}, resp={}", uri, resp);
        if (StringUtils.isBlank(resp)) {
            throw new BizException("顺丰接口无响应");
        }
        return JSON.parseObject(resp);
    }

    static String sign(String body, String sk, String timestamp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest((body + sk + timestamp).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new BizException("顺丰签名失败");
        }
    }

    private static boolean isOk(JSONObject resp) {
        return Objects.nonNull(resp) && "ok".equalsIgnoreCase(StringUtils.trimToEmpty(resp.getString("succ")));
    }

    private static void assertOk(JSONObject resp, String defaultMsg) {
        if (!isOk(resp)) {
            throw new BizException(StringUtils.defaultIfBlank(
                    Objects.nonNull(resp) ? resp.getString("msg") : null, defaultMsg));
        }
    }

    private static String extractMailNo(JSONObject result) {
        String mailNo = StringUtils.trimToEmpty(result.getString("mailno"));
        if (StringUtils.isNotBlank(mailNo)) {
            // 子母件时母单在第一位
            return mailNo.split(",")[0].trim();
        }
        Object waybillObj = result.get("waybillNoInfoList");
        JSONArray waybills = null;
        if (waybillObj instanceof JSONArray) {
            waybills = (JSONArray) waybillObj;
        } else if (waybillObj instanceof String && StringUtils.isNotBlank((String) waybillObj)) {
            waybills = JSON.parseArray((String) waybillObj);
        }
        if (waybills == null || waybills.isEmpty()) {
            return null;
        }
        String first = null;
        for (int i = 0; i < waybills.size(); i++) {
            JSONObject item = waybills.getJSONObject(i);
            if (Objects.isNull(item)) {
                continue;
            }
            String no = StringUtils.trimToEmpty(item.getString("waybillNo"));
            if (StringUtils.isBlank(no)) {
                continue;
            }
            if (Objects.equals(item.getInteger("waybillType"), 1)) {
                return no;
            }
            if (first == null) {
                first = no;
            }
        }
        return first;
    }

    private static List<SfRouteNode> parseRoutes(Object resultObj, String mailNo) {
        List<SfRouteNode> nodes = new ArrayList<>();
        if (Objects.isNull(resultObj)) {
            return nodes;
        }
        JSONArray routeList = extractRouteList(resultObj, mailNo);
        if (routeList == null) {
            return nodes;
        }
        for (int i = 0; i < routeList.size(); i++) {
            JSONObject item = routeList.getJSONObject(i);
            if (Objects.isNull(item)) {
                continue;
            }
            SfRouteNode node = new SfRouteNode();
            node.setAcceptTime(StringUtils.defaultIfBlank(item.getString("acceptTime"), item.getString("accept_time")));
            node.setAcceptAddress(StringUtils.defaultIfBlank(item.getString("acceptAddress"), item.getString("accept_address")));
            node.setRemark(StringUtils.defaultIfBlank(item.getString("remark"), item.getString("remark_desc")));
            node.setOpCode(StringUtils.defaultIfBlank(item.getString("opCode"), item.getString("opcode")));
            nodes.add(node);
        }
        return nodes;
    }

    private static JSONArray extractRouteList(Object resultObj, String mailNo) {
        if (resultObj instanceof JSONArray) {
            JSONArray arr = (JSONArray) resultObj;
            if (arr.isEmpty()) {
                return null;
            }
            Object first = arr.get(0);
            if (first instanceof JSONObject) {
                return extractRouteListFromObject((JSONObject) first, mailNo);
            }
            return null;
        }
        if (resultObj instanceof JSONObject) {
            return extractRouteListFromObject((JSONObject) resultObj, mailNo);
        }
        return null;
    }

    private static JSONArray extractRouteListFromObject(JSONObject obj, String mailNo) {
        JSONArray routeList = obj.getJSONArray("routes");
        if (routeList == null) {
            routeList = obj.getJSONArray("routeList");
        }
        if (routeList == null) {
            JSONArray routeResps = obj.getJSONArray("routeResps");
            if (routeResps != null && !routeResps.isEmpty()) {
                JSONObject matched = null;
                for (int i = 0; i < routeResps.size(); i++) {
                    JSONObject item = routeResps.getJSONObject(i);
                    if (Objects.isNull(item)) {
                        continue;
                    }
                    if (matched == null) {
                        matched = item;
                    }
                    if (StringUtils.equals(mailNo, item.getString("mailNo"))) {
                        matched = item;
                        break;
                    }
                }
                if (matched != null) {
                    routeList = matched.getJSONArray("routes");
                }
            }
        }
        if (routeList == null && StringUtils.isNotBlank(mailNo) && obj.containsKey(mailNo)) {
            Object byMail = obj.get(mailNo);
            if (byMail instanceof JSONArray) {
                routeList = (JSONArray) byMail;
            } else if (byMail instanceof JSONObject) {
                routeList = ((JSONObject) byMail).getJSONArray("routes");
            }
        }
        return routeList;
    }
}
