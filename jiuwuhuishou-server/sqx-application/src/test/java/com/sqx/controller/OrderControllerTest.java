package com.sqx.controller;

import com.sqx.app.order.controller.AppRecycleOrderController;
import com.sqx.app.order.domain.bo.ClothesOrderSaveBo;
import com.sqx.app.order.service.RecycleOrderService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppRecycleOrderController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        },
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = com.sqx.app.base.auth.auth.config.WebMvcConfig.class
        ))
@AutoConfigureMockMvc(addFilters = false)
@Disabled("由 OrderCreateIntegrationTest 覆盖；WebMvc 切片会误加载全局 MapperScan")
class OrderControllerTest extends ControllerSliceTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecycleOrderService recycleOrderService;

    @Test
    void createClothesOrderShouldReturnOrderId() throws Exception {
        when(recycleOrderService.createClothesOrder(eq(1L), any(ClothesOrderSaveBo.class))).thenReturn(100L);

        mockMvc.perform(post("/app/order/clothes/save")
                        .requestAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"addressId\":1,\"appointmentTime\":\"2026-07-10 14:00:00\",\"weight\":\"约18kg\",\"estimateAmount\":18.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(100));
    }
}
