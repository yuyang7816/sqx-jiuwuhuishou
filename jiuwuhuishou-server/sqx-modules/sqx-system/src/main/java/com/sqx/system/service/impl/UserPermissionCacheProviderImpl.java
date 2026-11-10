package com.sqx.system.service.impl;

import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.common.shiro.constant.ShiroConstant;
import com.sqx.common.shiro.service.UserPermissionCacheProvider;
import com.sqx.system.service.SysMenuService;
import com.sqx.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionCacheProviderImpl implements UserPermissionCacheProvider {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public void ensurePermissionCache(long userId) {
        String userCacheKey = String.format(ShiroConstant.AUTH_USER_MENU, userId);
        if (!RedisUtil.hasKey(userCacheKey)) {
            sysUserService.refreshPermissionCache(userId);
        }
        if (!RedisUtil.hasKey(ShiroConstant.AUTH_SYS_MENU)) {
            sysMenuService.loadSysMenu();
        }
    }
}
