package com.sqx.app.base.map.service.impl;

import com.sqx.app.base.map.domain.vo.AddressVo;
import com.sqx.app.base.map.factory.MapFactory;
import com.sqx.app.base.map.strategy.IMapPlatformStrategy;
import com.sqx.common.redis.utils.ConfigUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MapServiceImplTest {

    @Mock
    private MapFactory mapFactory;
    @Mock
    private IMapPlatformStrategy platformStrategy;

    @InjectMocks
    private MapServiceImpl mapService;

    @Test
    void reverseGeocodeShouldDelegateToConfiguredPlatform() {
        AddressVo addressVo = new AddressVo();
        addressVo.setProvince("广东省");
        addressVo.setCity("深圳市");
        addressVo.setDistrict("南山区");

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<MapFactory> factory = mockStatic(MapFactory.class)) {
            config.when(() -> ConfigUtil.getConfigValue("map_platform")).thenReturn("tencent");
            factory.when(MapFactory::getInstance).thenReturn(mapFactory);
            when(mapFactory.getPlatform("tencent")).thenReturn(platformStrategy);
            when(platformStrategy.reverseGeocode("22.5", "113.9")).thenReturn(addressVo);

            AddressVo result = mapService.reverseGeocode("22.5", "113.9");

            assertEquals("广东省", result.getProvince());
            assertEquals("深圳市", result.getCity());
            assertEquals("南山区", result.getDistrict());
            verify(platformStrategy).reverseGeocode("22.5", "113.9");
        }
    }
}
