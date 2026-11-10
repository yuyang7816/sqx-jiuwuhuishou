package com.sqx.app.express.sf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqx.app.express.sf.domain.SfPlaceOrderResult;
import com.sqx.app.express.sf.domain.SfRouteNode;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.http.OkHttpUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

class SfButlerClientTest {

    @Test
    void signShouldMatchSha512Base64OfBodySkTimestamp() throws Exception {
        String body = "{\"orderId\":\"RE1\"}";
        String sk = "test-sk";
        String timestamp = "1710000000000";

        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest((body + sk + timestamp).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String expected = java.util.Base64.getEncoder().encodeToString(hash);

        assertEquals(expected, SfButlerClient.sign(body, sk, timestamp));
    }

    @Test
    void isConfiguredAndRequireCompanyIdShouldReadConfig() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            SfButlerClient client = new SfButlerClient();
            assertFalse(client.isConfigured());
            assertThrows(BizException.class, client::requireCompanyId);

            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("app_id"))).thenReturn("app-1");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("sk"))).thenReturn("sk-1");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("company_id"))).thenReturn("1001");
            assertTrue(client.isConfigured());
            assertEquals("1001", client.requireCompanyId());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("company_id"))).thenReturn("");
            assertEquals("app-1", client.requireCompanyId());
        }
    }

    @Test
    void placeOrderShouldParseMailNoAndValidate() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<OkHttpUtil> http = mockStatic(OkHttpUtil.class)) {
            stubCredentials(config);
            SfButlerClient client = new SfButlerClient();

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"fail\",\"msg\":\"下单拒绝\"}");
            assertThrows(BizException.class, () -> client.placeOrder(new HashMap<>()));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"ok\"}");
            assertThrows(BizException.class, () -> client.placeOrder(new HashMap<>()));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"ok\",\"result\":{}}");
            assertThrows(BizException.class, () -> client.placeOrder(new HashMap<>()));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"ok\",\"result\":{\"orderId\":\"RE1\",\"mailno\":\"SF1,SF2\"}}");
            SfPlaceOrderResult ok = client.placeOrder(Collections.singletonMap("orderId", "RE1"));
            assertEquals("SF1", ok.getMailNo());
            assertEquals("RE1", ok.getOrderId());

            JSONArray waybills = new JSONArray();
            JSONObject child = new JSONObject();
            child.put("waybillNo", "SF-CHILD");
            child.put("waybillType", 2);
            waybills.add(child);
            JSONObject parent = new JSONObject();
            parent.put("waybillNo", "SF-PARENT");
            parent.put("waybillType", 1);
            waybills.add(parent);
            JSONObject result = new JSONObject();
            result.put("orderid", "RE2");
            result.put("waybillNoInfoList", waybills);
            JSONObject resp = new JSONObject();
            resp.put("succ", "ok");
            resp.put("result", result);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(resp.toJSONString());
            assertEquals("SF-PARENT", client.placeOrder(new HashMap<>()).getMailNo());

            JSONArray onlyChild = new JSONArray();
            onlyChild.add(child);
            result.put("waybillNoInfoList", onlyChild.toJSONString());
            result.remove("mailno");
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(resp.toJSONString());
            assertEquals("SF-CHILD", client.placeOrder(new HashMap<>()).getMailNo());
        }
    }

    @Test
    void cancelOrderShouldSupportIdempotentAndFailures() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<OkHttpUtil> http = mockStatic(OkHttpUtil.class)) {
            stubCredentials(config);
            SfButlerClient client = new SfButlerClient();

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"fail\",\"msg\":\"订单已取消\"}");
            assertDoesNotThrow(() -> client.cancelOrder("1001", "RE1"));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"fail\",\"msg\":\"系统繁忙\"}");
            assertThrows(BizException.class, () -> client.cancelOrder("1001", "RE1"));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"ok\",\"result\":{\"code\":\"1\"}}");
            assertThrows(BizException.class, () -> client.cancelOrder("1001", "RE1"));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"ok\",\"result\":{\"code\":\"2\"}}");
            assertDoesNotThrow(() -> client.cancelOrder("1001", "RE1"));
        }
    }

    @Test
    void listRoutesShouldHandleBlankAuthFailuresAndParseVariants() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<OkHttpUtil> http = mockStatic(OkHttpUtil.class)) {
            stubCredentials(config);
            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("route"))).thenReturn("");
            SfButlerClient client = new SfButlerClient();

            assertTrue(client.listRoutes("1001", "").isEmpty());
            assertTrue(client.listRoutes("1001", null).isEmpty());

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"login\",\"msg\":\"login\"}");
            BizException auth = assertThrows(BizException.class,
                    () -> client.listRoutes("1001", "SF1", "0002"));
            assertTrue(auth.getMessage().contains("鉴权"));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn("{\"succ\":\"fail\",\"msg\":\"路由失败\"}");
            assertThrows(BizException.class, () -> client.listRoutes("abc", "SF1"));

            JSONObject route = new JSONObject();
            route.put("accept_time", "2026-07-22 10:00:00");
            route.put("accept_address", "上海");
            route.put("remark_desc", "已揽收");
            route.put("opcode", "50");
            JSONArray routes = new JSONArray();
            routes.add(route);
            JSONObject respItem = new JSONObject();
            respItem.put("mailNo", "SF0217121758386");
            respItem.put("routes", routes);
            JSONArray routeResps = new JSONArray();
            routeResps.add(new JSONObject());
            routeResps.add(respItem);
            JSONObject result = new JSONObject();
            result.put("routeResps", routeResps);
            JSONObject okResp = new JSONObject();
            okResp.put("succ", "ok");
            okResp.put("result", result);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            List<SfRouteNode> parsed = client.listRoutes("1001", "SF0217121758386", "0002");
            assertEquals(1, parsed.size());
            assertEquals("50", parsed.get(0).getOpCode());

            JSONObject byMail = new JSONObject();
            JSONObject nested = new JSONObject();
            nested.put("routes", routes);
            byMail.put("SF9", nested);
            okResp.put("result", byMail);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertEquals(1, client.listRoutes("1001", "SF9").size());

            JSONArray arrWrap = new JSONArray();
            JSONObject first = new JSONObject();
            first.put("routeList", routes);
            arrWrap.add(first);
            okResp.put("result", arrWrap);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertEquals(1, client.listRoutes("1001", "SF-ARR").size());

            okResp.put("result", "unexpected");
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertTrue(client.listRoutes("1001", "SF-X").isEmpty());

            JSONArray emptyArr = new JSONArray();
            okResp.put("result", emptyArr);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertTrue(client.listRoutes("1001", "SF-EMPTY").isEmpty());

            JSONArray nonObjArr = new JSONArray();
            nonObjArr.add("not-object");
            okResp.put("result", nonObjArr);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertTrue(client.listRoutes("1001", "SF-NONOBJ").isEmpty());

            JSONArray routesWithNull = new JSONArray();
            routesWithNull.add(null);
            routesWithNull.add(route);
            JSONObject routeWrap = new JSONObject();
            routeWrap.put("routes", routesWithNull);
            okResp.put("result", routeWrap);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertEquals(1, client.listRoutes("1001", "SF-NULL-ROUTE").size());

            JSONArray respsWithNull = new JSONArray();
            respsWithNull.add(null);
            JSONObject respMatch = new JSONObject();
            respMatch.put("mailNo", "SF-MATCH");
            respMatch.put("routes", routes);
            respsWithNull.add(respMatch);
            JSONObject respsResult = new JSONObject();
            respsResult.put("routeResps", respsWithNull);
            okResp.put("result", respsResult);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(okResp.toJSONString());
            assertEquals(1, client.listRoutes("1001", "SF-MATCH").size());
        }
    }

    @Test
    void placeOrderShouldSkipInvalidWaybillEntries() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<OkHttpUtil> http = mockStatic(OkHttpUtil.class)) {
            stubCredentials(config);
            SfButlerClient client = new SfButlerClient();

            JSONArray waybills = new JSONArray();
            waybills.add(null);
            JSONObject blank = new JSONObject();
            blank.put("waybillNo", " ");
            waybills.add(blank);
            JSONObject okItem = new JSONObject();
            okItem.put("waybillNo", "SF-OK");
            okItem.put("waybillType", 2);
            waybills.add(okItem);
            JSONObject result = new JSONObject();
            result.put("waybillNoInfoList", waybills);
            JSONObject resp = new JSONObject();
            resp.put("succ", "ok");
            resp.put("result", result);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(resp.toJSONString());
            assertEquals("SF-OK", client.placeOrder(new HashMap<>()).getMailNo());
        }
    }

    @Test
    void signShouldWrapDigestFailure() {
        try (MockedStatic<java.security.MessageDigest> digest = mockStatic(java.security.MessageDigest.class)) {
            digest.when(() -> java.security.MessageDigest.getInstance("SHA-512"))
                    .thenThrow(new java.security.NoSuchAlgorithmException("no sha"));
            BizException ex = assertThrows(BizException.class, () -> SfButlerClient.sign("a", "b", "c"));
            assertTrue(ex.getMessage().contains("签名失败"));
        }
    }

    @Test
    void postShouldValidateCredentialsAndResponse() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<OkHttpUtil> http = mockStatic(OkHttpUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            SfButlerClient client = new SfButlerClient();
            assertThrows(BizException.class, () -> client.placeOrder(new HashMap<>()));

            stubCredentials(config);
            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenThrow(new BizException("network"));
            assertThrows(BizException.class, () -> client.placeOrder(new HashMap<>()));

            http.when(() -> OkHttpUtil.doPost(anyString(), anyString(), anyString(), anyMap()))
                    .thenReturn(" ");
            assertThrows(BizException.class, () -> client.placeOrder(new HashMap<>()));
        }
    }

    private static void stubCredentials(MockedStatic<ConfigUtil> config) {
        config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
        config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("app_id"))).thenReturn("app-1");
        config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("sk"))).thenReturn("sk-1");
        config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("company_id"))).thenReturn("1001");
        config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("base_url")))
                .thenReturn("https://butler-dev.sit.sf-express.com/");
    }
}
