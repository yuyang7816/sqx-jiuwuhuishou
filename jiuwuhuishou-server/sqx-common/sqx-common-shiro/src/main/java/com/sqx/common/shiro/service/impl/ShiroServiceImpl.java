package com.sqx.common.shiro.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.sqx.common.core.constant.Constant;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.common.shiro.constant.ShiroConstant;
import com.sqx.common.shiro.model.AuthSysMenu;
import com.sqx.common.shiro.service.ShiroService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chengming
 * @since 2024/7/11
 */
@Service
public class ShiroServiceImpl implements ShiroService {
    @Override
    public Set<String> getUserPermissions(long userId) {
        if (userId == Constant.SUPER_ADMIN) {
            Set<String> allMenuPerms = extractPermissionsFromMenuJson(
                RedisUtil.getCacheObject(ShiroConstant.AUTH_SYS_MENU)
            );
            if (!allMenuPerms.isEmpty()) {
                return allMenuPerms;
            }
        }

        List<String> permsList = new ArrayList<>();
        String cacheKey = String.format(ShiroConstant.AUTH_USER_MENU, userId);
        String menuListJson = RedisUtil.getCacheObject(cacheKey);
        if (StringUtils.isNotBlank(menuListJson)) {
            List<AuthSysMenu> menuList = JSONObject.parseObject(menuListJson, new TypeReference<List<AuthSysMenu>>() {});
            if (menuList != null) {
                for (AuthSysMenu menu : menuList) {
                    if (StringUtils.isNotBlank(menu.getPerms())) {
                        permsList.add(menu.getPerms());
                    }
                }
            }
        }
        return splitPermissionStrings(permsList);
    }

    private Set<String> extractPermissionsFromMenuJson(String menuListJson) {
        if (StringUtils.isBlank(menuListJson)) {
            return Collections.emptySet();
        }
        List<AuthSysMenu> menuList = JSONObject.parseObject(menuListJson, new TypeReference<List<AuthSysMenu>>() {});
        List<String> permsList = new ArrayList<>();
        collectMenuPermissions(menuList, permsList);
        return splitPermissionStrings(permsList);
    }

    private void collectMenuPermissions(List<AuthSysMenu> menuList, List<String> permsList) {
        if (menuList == null) {
            return;
        }
        for (AuthSysMenu menu : menuList) {
            if (StringUtils.isNotBlank(menu.getPerms())) {
                permsList.add(menu.getPerms());
            }
            if (menu.getList() != null && !menu.getList().isEmpty()) {
                collectMenuPermissions(JSONObject.parseObject(
                    JSONObject.toJSONString(menu.getList()),
                    new TypeReference<List<AuthSysMenu>>() {}
                ), permsList);
            }
        }
    }

    private Set<String> splitPermissionStrings(List<String> permsList) {
        Set<String> permsSet = new HashSet<>();
        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            for (String perm : perms.trim().split(",")) {
                if (StringUtils.isNotBlank(perm)) {
                    permsSet.add(perm.trim());
                }
            }
        }
        return permsSet;
    }
}
