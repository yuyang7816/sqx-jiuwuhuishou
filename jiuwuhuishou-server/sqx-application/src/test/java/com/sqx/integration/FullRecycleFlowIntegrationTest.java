package com.sqx.integration;

import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo;
import com.sqx.app.order.domain.bo.RecycleOrderInfoBo;
import com.sqx.app.settlement.domain.bo.SettlementConfirmBo;
import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 回收主链路烟测：下单 → 管理端改状态 → 结算确认 → 订单完成
 */
class FullRecycleFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void clothesOrderAdminStatusAndSettlementConfirmShouldComplete() throws Exception {
        // 下单后自动派单为 IN_PROGRESS，管理端推进到待确认
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        AdminOrderUpdateStatusBo toSettlement = new AdminOrderUpdateStatusBo();
        toSettlement.setId(orderId);
        toSettlement.setTargetStatus("WAIT_SETTLEMENT_CONFIRM");
        toSettlement.setRemark("集成测试-待确认成交");
        postAdminJson("/admin/order/updateStatus", toSettlement)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        jdbcTemplate.update(
                "INSERT INTO order_settlement (settlement_no, order_id, user_id, estimate_amount, actual_amount, status, create_time, update_time) "
                        + "VALUES (?, ?, ?, ?, ?, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "ST" + orderId, orderId, TestDataFactory.TEST_USER_ID, new BigDecimal("18.00"), new BigDecimal("18.00"));

        SettlementConfirmBo confirmBo = new SettlementConfirmBo();
        confirmBo.setOrderId(orderId);
        postJson("/app/settlement/confirm", TestDataFactory.TEST_USER_ID, confirmBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderStatusKey").value("done"));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}
