package com.sqx.app.base.auth.support;

import com.sqx.common.core.utils.SpringContextHolder;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.common.shiro.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppAuthTokenSupportTest {

    private static MockedStatic<SpringContextHolder> springContextHolderMock;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AppAuthTokenSupport appAuthTokenSupport;

    @BeforeAll
    static void initRedisUtilClass() {
        springContextHolderMock = mockStatic(SpringContextHolder.class);
        springContextHolderMock.when(() -> SpringContextHolder.getBean(RedissonClient.class))
                .thenReturn(mock(RedissonClient.class));
        try {
            Class.forName("com.sqx.common.redis.utils.RedisUtil");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterAll
    static void closeSpringContextHolderMock() {
        if (springContextHolderMock != null) {
            springContextHolderMock.close();
        }
    }

    @Test
    void invalidateTokenShouldSkipBlankInvalidOrExpiredClaims() {
        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            appAuthTokenSupport.invalidateToken(null);
            appAuthTokenSupport.invalidateToken("  ");

            when(jwtUtil.getClaimByToken("bad")).thenReturn(null);
            appAuthTokenSupport.invalidateToken("bad");

            Claims noExpiration = mock(Claims.class);
            when(noExpiration.getExpiration()).thenReturn(null);
            when(jwtUtil.getClaimByToken("no-exp")).thenReturn(noExpiration);
            appAuthTokenSupport.invalidateToken("no-exp");

            Claims expired = mock(Claims.class);
            when(expired.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 1000));
            when(jwtUtil.getClaimByToken("expired")).thenReturn(expired);
            appAuthTokenSupport.invalidateToken("expired");

            redis.verifyNoInteractions();
        }
    }

    @Test
    void invalidateTokenShouldStoreLogoutKeyUntilExpiration() {
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 60_000));
        when(jwtUtil.getClaimByToken("valid-token")).thenReturn(claims);

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            appAuthTokenSupport.invalidateToken("valid-token");

            redis.verify(() -> RedisUtil.setCacheObject(anyString(), eq("1"), any(Duration.class)));
        }
    }

    @Test
    void isTokenInvalidatedShouldUseRedisLookup() {
        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            assertFalse(appAuthTokenSupport.isTokenInvalidated(null));
            assertFalse(appAuthTokenSupport.isTokenInvalidated(" "));

            redis.when(() -> RedisUtil.hasKey(anyString())).thenReturn(true);
            assertTrue(appAuthTokenSupport.isTokenInvalidated("valid-token"));
            redis.verify(() -> RedisUtil.hasKey(anyString()));
        }
    }
}
