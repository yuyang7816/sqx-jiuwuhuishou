package com.sqx.app.express.sf;

import com.sqx.app.express.sf.domain.SfRouteNode;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SfExpressOrderServiceTest {

    @Mock
    private SfButlerClient sfButlerClient;

    @InjectMocks
    private SfExpressOrderService service;

    @Test
    void buildCheckPhoneNoShouldDeduplicateLast4() {
        assertNull(SfExpressOrderService.buildCheckPhoneNo());
        assertNull(SfExpressOrderService.buildCheckPhoneNo("12", null, "  "));
        assertEquals("0002,6688", SfExpressOrderService.buildCheckPhoneNo("15200000002", "400-800-6688", "15200000002"));
    }

    @Test
    void listExpressTracksShouldReturnEmptyWhenNoExpressNo() {
        assertTrue(service.listExpressTracks("").isEmpty());
        assertTrue(service.listExpressTracks(null, "0002").isEmpty());
    }

    @Test
    void listExpressTracksShouldFailWhenNotConfigured() {
        when(sfButlerClient.isConfigured()).thenReturn(false);
        BizException ex = assertThrows(BizException.class, () -> service.listExpressTracks("SF1"));
        assertTrue(ex.getMessage().contains("凭证未配置"));
    }

    @Test
    void listExpressTracksShouldMapRoutes() {
        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("1001");

        SfRouteNode node = new SfRouteNode();
        node.setOpCode("50");
        node.setAcceptTime("2026-07-22 10:00:00");
        node.setAcceptAddress("上海");
        node.setRemark("快件已揽收");
        when(sfButlerClient.listRoutes(eq("1001"), eq("SF0217121758386"), eq("0002")))
                .thenReturn(Collections.singletonList(node));

        List<ExpressTrackVo> tracks = service.listExpressTracks("SF0217121758386", "0002");
        assertEquals(1, tracks.size());
        assertEquals("50", tracks.get(0).getTitle());
        assertTrue(tracks.get(0).getActive());
        assertTrue(tracks.get(0).getDesc().contains("上海"));
    }

    @Test
    void listExpressTracksShouldPropagateBizException() {
        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("1001");
        when(sfButlerClient.listRoutes(eq("1001"), eq("SF2"), eq("1234")))
                .thenThrow(new BizException("鉴权失败"));
        BizException ex = assertThrows(BizException.class, () -> service.listExpressTracks("SF2", "1234"));
        assertEquals("鉴权失败", ex.getMessage());
    }

    @Test
    void listExpressTracksShouldWrapUnexpectedException() {
        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("1001");
        when(sfButlerClient.listRoutes(eq("1001"), eq("SF3"), isNull()))
                .thenThrow(new RuntimeException("timeout"));
        BizException wrapped = assertThrows(BizException.class, () -> service.listExpressTracks("SF3"));
        assertTrue(wrapped.getMessage().contains("查询物流轨迹失败"));
    }

    @Test
    void isConfiguredShouldDelegate() {
        when(sfButlerClient.isConfigured()).thenReturn(true);
        assertTrue(service.isConfigured());
    }

    @Test
    void placeMailOrderShouldValidateAndCallClient() {
        when(sfButlerClient.isConfigured()).thenReturn(false);
        assertThrows(BizException.class, () -> service.placeMailOrder(null, null));

        com.sqx.app.order.domain.entity.RecycleOrder order = new com.sqx.app.order.domain.entity.RecycleOrder();
        order.setOrderNo("RE1");
        order.setCategoryName("旧衣");
        order.setRemark("轻拿轻放请备注超过十二个字截断标题场景");
        order.setAppointmentTime(java.time.LocalDateTime.of(2026, 7, 22, 10, 0));
        com.sqx.app.address.domain.entity.UserAddress address = new com.sqx.app.address.domain.entity.UserAddress();
        address.setContactName("张三");
        address.setContactMobile("13800138000");
        address.setProvince("上海市");
        address.setCity("上海市");
        address.setDistrict("浦东新区");
        address.setDetailAddress("纳贤路1号");

        when(sfButlerClient.isConfigured()).thenReturn(false);
        assertThrows(BizException.class, () -> service.placeMailOrder(order, address));

        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("company-x");
        com.sqx.app.express.sf.domain.SfPlaceOrderResult result = new com.sqx.app.express.sf.domain.SfPlaceOrderResult();
        result.setMailNo("SF999");
        when(sfButlerClient.placeOrder(anyMap())).thenReturn(result);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            assertEquals("SF999", service.placeMailOrder(order, address));
            verify(sfButlerClient).placeOrder(argThat(body ->
                    "company-x".equals(String.valueOf(body.get("companyId")))
                            && body.containsKey("sendStartTime")
                            && body.containsKey("remark")));
        }

        when(sfButlerClient.requireCompanyId()).thenReturn("1001");
        address.setStreet("测试街");
        address.setDetailAddress("");
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("400-800-6688");
            assertEquals("SF999", service.placeMailOrder(order, address));
        }

        // 无明细时回退完整地址；收件手机 normalize 短号 / 空号分支
        address.setStreet(null);
        address.setDetailAddress(null);
        address.setProvince("上海市");
        address.setCity("上海市");
        address.setDistrict("浦东新区");
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("receiver_phone"))).thenReturn("12345");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            assertEquals("SF999", service.placeMailOrder(order, address));
        }
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(contains("receiver_phone"))).thenReturn(null);
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            // default receiver phone 400-800-6688 → normalize 取后11位
            assertEquals("SF999", service.placeMailOrder(order, address));
        }

        // street + detail 同时存在触发 append 空格
        address.setStreet("街A");
        address.setDetailAddress("门牌B");
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            assertEquals("SF999", service.placeMailOrder(order, address));
        }
    }

    @Test
    void listExpressTracksShouldReturnEmptyWhenRoutesEmpty() {
        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("1001");
        when(sfButlerClient.listRoutes(eq("1001"), eq("SF-EMPTY"), isNull()))
                .thenReturn(Collections.emptyList());
        assertTrue(service.listExpressTracks("SF-EMPTY").isEmpty());
    }

    @Test
    void listExpressTracksShouldTruncateTitleWhenOpCodeBlank() {
        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("1001");
        SfRouteNode node = new SfRouteNode();
        node.setRemark("这是一段很长的物流备注用于截断标题覆盖");
        node.setAcceptTime("2026-07-22 11:00:00");
        when(sfButlerClient.listRoutes(eq("1001"), eq("SF4"), isNull()))
                .thenReturn(Collections.singletonList(node));
        ExpressTrackVo vo = service.listExpressTracks("SF4").get(0);
        assertEquals(12, vo.getTitle().length());
        assertTrue(vo.getActive());
    }

    @Test
    void normalizeMobileShouldHandleBlankAndLongDigits() throws Exception {
        java.lang.reflect.Method method = SfExpressOrderService.class
                .getDeclaredMethod("normalizeMobile", String.class);
        method.setAccessible(true);
        assertNull(method.invoke(null, new Object[]{null}));
        assertEquals("  ", method.invoke(null, "  "));
        assertEquals("13800138000", method.invoke(null, "008613800138000"));
        assertEquals("", method.invoke(null, ""));
    }

    @Test
    void cancelMailOrderShouldSkipBlankExpressNo() {
        service.cancelMailOrder(null);
        com.sqx.app.order.domain.entity.RecycleOrder order = new com.sqx.app.order.domain.entity.RecycleOrder();
        order.setOrderNo("RE1");
        service.cancelMailOrder(order);
        verify(sfButlerClient, never()).cancelOrder(anyString(), anyString());

        order.setExpressNo("SF1");
        when(sfButlerClient.isConfigured()).thenReturn(true);
        when(sfButlerClient.requireCompanyId()).thenReturn("1001");
        service.cancelMailOrder(order);
        verify(sfButlerClient).cancelOrder("1001", "RE1");
    }

    @Test
    void cancelMailOrderShouldSkipWhenSfNotConfigured() {
        com.sqx.app.order.domain.entity.RecycleOrder order = new com.sqx.app.order.domain.entity.RecycleOrder();
        order.setOrderNo("RE2");
        order.setExpressNo("SF2");
        when(sfButlerClient.isConfigured()).thenReturn(false);

        service.cancelMailOrder(order);

        verify(sfButlerClient, never()).requireCompanyId();
        verify(sfButlerClient, never()).cancelOrder(anyString(), anyString());
    }
}
