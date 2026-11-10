package com.sqx.common.shiro.service;

/**
 * 用户权限缓存刷新（由 system 模块提供实现）
 */
public interface UserPermissionCacheProvider {

    /**
     * 确保当前用户在 Redis 中已有 Shiro 鉴权所需的权限缓存
     */
    void ensurePermissionCache(long userId);
}
