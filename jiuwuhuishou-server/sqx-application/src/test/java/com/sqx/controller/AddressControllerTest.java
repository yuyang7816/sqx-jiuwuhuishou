package com.sqx.controller;

import com.sqx.app.address.controller.AppUserAddressController;
import com.sqx.app.address.domain.vo.UserAddressVo;
import com.sqx.app.address.service.UserAddressService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppUserAddressController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        },
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = com.sqx.app.base.auth.auth.config.WebMvcConfig.class
        ))
@AutoConfigureMockMvc(addFilters = false)
@Disabled("由 AddressIntegrationTest 覆盖；WebMvc 切片会误加载全局 MapperScan")
class AddressControllerTest extends ControllerSliceTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAddressService userAddressService;

    @Test
    void listShouldReturnFullMobileWhenAuthenticated() throws Exception {
        UserAddressVo vo = new UserAddressVo();
        vo.setId(1L);
        vo.setContactName("测试");
        vo.setContactMobile("13812348888");
        when(userAddressService.list(1L)).thenReturn(Collections.singletonList(vo));

        mockMvc.perform(get("/app/address/list").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].contactMobile").value("13812348888"));
    }
}
