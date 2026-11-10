package com.sqx.app.express.aliyun;

import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class AliyunLogisticsServiceTest {

    private final AliyunLogisticsService service = new AliyunLogisticsService();

    @Test
    void listExpressTracksShouldReturnEmptyWhenNoTrackingNumber() {
        assertTrue(service.listExpressTracks("").isEmpty());
        assertTrue(service.listExpressTracks("  ").isEmpty());
        assertTrue(service.listExpressTracks(null).isEmpty());
    }

    @Test
    void listExpressTracksShouldFailWhenAppCodeMissing() {
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValue(eq("aliyun_logistics_appcode"))).thenReturn("");
            BizException ex = assertThrows(BizException.class,
                    () -> service.listExpressTracks("YT123", "15200000002"));
            assertTrue(ex.getMessage().contains("aliyun_logistics_appcode"));
        }
    }

    @Test
    void buildQueryCandidatesShouldAppendPhoneTailForSf() {
        List<String> candidates = service.buildQueryCandidates("SF0217121758386", "15200000002", "400-800-6688");
        assertEquals(2, candidates.size());
        assertEquals("SF0217121758386:0002", candidates.get(0));
        assertEquals("SF0217121758386:6688", candidates.get(1));
    }

    @Test
    void buildQueryCandidatesShouldKeepExistingPhoneTail() {
        List<String> candidates = service.buildQueryCandidates("SF123:9999", "15200000002");
        assertEquals(1, candidates.size());
        assertEquals("SF123:9999", candidates.get(0));
    }

    @Test
    void buildQueryCandidatesShouldFailWhenSfWithoutPhone() {
        BizException ex = assertThrows(BizException.class,
                () -> service.buildQueryCandidates("SF12345678"));
        assertTrue(ex.getMessage().contains("后四位"));
    }

    @Test
    void parseTracksShouldMapListAndMarkFirstActive() {
        String json = "{"
                + "\"status\":\"0\","
                + "\"msg\":\"ok\","
                + "\"result\":{"
                + "\"number\":\"13800138000:SF123\","
                + "\"list\":["
                + "{\"time\":\"2026-03-24 10:00:00\",\"status\":\"快件已签收\"},"
                + "{\"time\":\"2026-03-24 08:00:00\",\"status\":\"快件正在派送\"}"
                + "]"
                + "}"
                + "}";
        List<ExpressTrackVo> tracks = service.parseTracks(json);
        assertEquals(2, tracks.size());
        assertTrue(tracks.get(0).getActive());
        assertEquals("2026-03-24 10:00:00", tracks.get(0).getTime());
        assertEquals("快件已签收", tracks.get(0).getDesc());
        assertFalse(tracks.get(1).getActive());
    }

    @Test
    void parseTracksShouldRejectNonZeroStatus() {
        BizException ex = assertThrows(BizException.class,
                () -> service.parseTracks("{\"status\":\"205\",\"msg\":\"单号不存在\"}"));
        assertTrue(ex.getMessage().contains("单号"));
    }

    @Test
    void parseTracksShouldReturnEmptyWhenNoList() {
        assertTrue(service.parseTracks("{\"status\":\"0\",\"result\":{}}").isEmpty());
    }
}
