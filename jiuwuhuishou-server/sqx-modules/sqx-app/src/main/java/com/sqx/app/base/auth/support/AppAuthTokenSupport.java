package com.sqx.app.base.auth.support;

import com.sqx.common.core.utils.Md5Utils;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.common.shiro.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

@Component
public class AppAuthTokenSupport {

    private static final String LOGOUT_KEY_PREFIX = "app:auth:logout:";

    @Resource
    private JwtUtil jwtUtil;

    public void invalidateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        Claims claims = jwtUtil.getClaimByToken(token);
        if (claims == null || claims.getExpiration() == null) {
            return;
        }
        long ttlMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (ttlMillis <= 0) {
            return;
        }
        String cacheKey = LOGOUT_KEY_PREFIX + Md5Utils.md5s(token);
        RedisUtil.setCacheObject(cacheKey, "1", Duration.ofMillis(ttlMillis));
    }

    public boolean isTokenInvalidated(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return RedisUtil.hasKey(LOGOUT_KEY_PREFIX + Md5Utils.md5s(token));
    }
}
