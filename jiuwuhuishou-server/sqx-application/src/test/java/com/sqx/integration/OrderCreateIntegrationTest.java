package com.sqx.integration;

import com.sqx.app.order.domain.bo.ApplianceOrderSaveBo;
import com.sqx.app.order.domain.bo.ClothesOrderSaveBo;
import com.sqx.app.order.domain.bo.DigitalOrderSaveBo;
import com.sqx.app.order.domain.bo.RecycleOrderInfoBo;
import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderCreateIntegrationTest extends AbstractIntegrationTest {

    @Test
    void createClothesOrderShouldPersist() throws Exception {
        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(TestDataFactory.SEED_ADDRESS_ID);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("约18kg");
        saveBo.setEstimateAmount(new BigDecimal("18.00"));

        postJson("/app/order/clothes/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void createApplianceOrderShouldPersist() throws Exception {
        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(TestDataFactory.SEED_ADDRESS_ID);
        saveBo.setModelId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setEstimateAmount(new BigDecimal("226.00"));

        postJson("/app/order/appliance/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void createApplianceMailOrderShouldPersistFulfillType() throws Exception {
        Long orderId = testDataFactory.createApplianceMailOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.fulfillType").value("mail"));
    }

    @Test
    void createDigitalOrderShouldPersist() throws Exception {
        DigitalOrderSaveBo saveBo = new DigitalOrderSaveBo();
        saveBo.setAddressId(TestDataFactory.SEED_ADDRESS_ID);
        saveBo.setModelId(2L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setEstimateAmount(new BigDecimal("6120.00"));

        postJson("/app/order/digital/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void createBookOrderShouldPersistOrderType() throws Exception {
        Long orderId = testDataFactory.createBookOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderType").value("book"))
                .andExpect(jsonPath("$.data.categoryName").value("书籍回收"));
    }

    @Test
    void createOrderWithInvalidAddressIdShouldFail() throws Exception {
        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(99999L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("约18kg");
        saveBo.setEstimateAmount(new BigDecimal("18.00"));

        postJson("/app/order/clothes/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("地址不存在"));
    }
}
