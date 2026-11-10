package com.sqx.app.base.appConfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.base.appConfig.domain.bo.SysConfigInfoBO;
import com.sqx.app.base.appConfig.domain.vo.SysConfigVO;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.system.db.entity.SysConfigEntity;
import com.sqx.system.db.service.ISysConfigService;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppConfigServiceImplTest {

    @Mock
    private ISysConfigService iSysConfigService;

    @InjectMocks
    private AppConfigServiceImpl appConfigService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(SysConfigEntity.class);
    }

    @Test
    void infoShouldReturnBlankWhenKeyMissing() {
        SysConfigInfoBO bo = new SysConfigInfoBO();
        bo.setKey("  ");

        SysConfigVO vo = appConfigService.info(bo);

        assertEquals("", vo.getValue());
        verify(iSysConfigService, never()).getOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void infoShouldPreferCacheValue() {
        SysConfigInfoBO bo = new SysConfigInfoBO();
        bo.setKey("site_name");

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("site_name")).thenReturn("cached");

            SysConfigVO vo = appConfigService.info(bo);

            assertEquals("cached", vo.getValue());
            verify(iSysConfigService, never()).getOne(any(LambdaQueryWrapper.class));
        }
    }

    @Test
    void infoShouldLoadFromDbWhenCacheMissing() {
        SysConfigInfoBO bo = new SysConfigInfoBO();
        bo.setKey("site_name");

        SysConfigEntity entity = new SysConfigEntity();
        entity.setParamValue("db-value");

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("site_name")).thenReturn(null);
            when(iSysConfigService.getOne(any(LambdaQueryWrapper.class))).thenReturn(entity);

            assertEquals("db-value", appConfigService.info(bo).getValue());
        }
    }

    @Test
    void infoShouldReturnBlankWhenDbMissingOrValueNull() {
        SysConfigInfoBO bo = new SysConfigInfoBO();
        bo.setKey("site_name");

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("site_name")).thenReturn(null);
            when(iSysConfigService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            assertEquals("", appConfigService.info(bo).getValue());

            SysConfigEntity blankValue = new SysConfigEntity();
            when(iSysConfigService.getOne(any(LambdaQueryWrapper.class))).thenReturn(blankValue);
            assertEquals("", appConfigService.info(bo).getValue());
        }
    }
}
