package com.sqx.controller;

import com.sqx.app.recycle.config.controller.AppRecycleConfigController;
import com.sqx.app.recycle.config.domain.vo.RecycleCategoryVo;
import com.sqx.app.recycle.config.domain.vo.RecycleRuleVo;
import com.sqx.app.recycle.config.domain.vo.ServiceAreaCheckVo;
import com.sqx.app.recycle.config.service.RecycleConfigService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppRecycleConfigController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        },
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = com.sqx.app.base.auth.auth.config.WebMvcConfig.class
        ))
@AutoConfigureMockMvc(addFilters = false)
@Disabled("由 RecycleConfigIntegrationTest 覆盖；WebMvc 切片会误加载全局 MapperScan")
class RecycleConfigControllerTest extends ControllerSliceTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecycleConfigService recycleConfigService;

    @Test
    void categoryListShouldReturnSuccess() throws Exception {
        RecycleCategoryVo categoryVo = new RecycleCategoryVo();
        categoryVo.setCode("clothes");
        categoryVo.setName("旧衣回收");
        when(recycleConfigService.listCategories()).thenReturn(Collections.singletonList(categoryVo));

        mockMvc.perform(get("/app/recycle/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].code").value("clothes"));
    }

    @Test
    void ruleListShouldReturnSuccess() throws Exception {
        RecycleRuleVo ruleVo = new RecycleRuleVo();
        ruleVo.setTitle("回收范围");
        when(recycleConfigService.listRules()).thenReturn(Collections.singletonList(ruleVo));

        mockMvc.perform(get("/app/recycle/rule/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].title").value("回收范围"));
    }

    @Test
    void serviceAreaCheckShouldReturnSuccess() throws Exception {
        ServiceAreaCheckVo checkVo = new ServiceAreaCheckVo();
        checkVo.setInService(true);
        when(recycleConfigService.checkServiceArea(any())).thenReturn(checkVo);

        mockMvc.perform(post("/app/recycle/serviceArea/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"province\":\"上海市\",\"city\":\"上海市\",\"district\":\"浦东新区\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.inService").value(true));
    }
}
