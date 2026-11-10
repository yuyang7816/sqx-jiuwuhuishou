package com.sqx.app.base.appConfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.base.appConfig.domain.bo.SysConfigInfoBO;
import com.sqx.app.base.appConfig.domain.vo.SysConfigVO;
import com.sqx.app.base.appConfig.service.AppConfigService;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.system.db.entity.SysConfigEntity;
import com.sqx.system.db.service.ISysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author chengming
 * @since 2025/6/9
 */
@Service
public class AppConfigServiceImpl implements AppConfigService {

    @Resource
    private ISysConfigService iSysConfigService;

    @Override
    public SysConfigVO info(SysConfigInfoBO sysConfigInfoBO) {
        SysConfigVO vo = new SysConfigVO();
        vo.setValue(resolveConfigValue(sysConfigInfoBO.getKey()));
        return vo;
    }

    private String resolveConfigValue(String paramKey) {
        if (StringUtils.isBlank(paramKey)) {
            return "";
        }

        String cacheValue = ConfigUtil.getConfigValueOrDefault(paramKey);
        if (Objects.nonNull(cacheValue)) {
            return cacheValue;
        }

        SysConfigEntity sysConfig = iSysConfigService.getOne(new LambdaQueryWrapper<SysConfigEntity>()
                .eq(SysConfigEntity::getParamKey, paramKey)
                .eq(SysConfigEntity::getStatus, 1)
                .last("LIMIT 1"));
        return Objects.nonNull(sysConfig) && Objects.nonNull(sysConfig.getParamValue())
                ? sysConfig.getParamValue()
                : "";
    }
}
