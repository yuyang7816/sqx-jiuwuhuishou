package com.sqx.integration;

import com.sqx.app.order.domain.bo.CancelOrderBo;
import com.sqx.app.order.domain.bo.ExceptionBo;
import com.sqx.app.order.domain.bo.RecycleOrderInfoBo;
import com.sqx.app.order.domain.bo.RecycleOrderPageBo;
import com.sqx.app.order.domain.bo.RescheduleBo;
import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderLifecycleIntegrationTest extends AbstractIntegrationTest {

    @Test
    void pageAndInfoShouldReturnOrderWithLogs() throws Exception {
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        RecycleOrderPageBo pageBo = new RecycleOrderPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        postJson("/app/order/page", TestDataFactory.TEST_USER_ID, pageBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].id").value(orderId));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);

        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.estimateAmountConfirmed").value(false))
                .andExpect(jsonPath("$.data.logs[0].actionType").exists());
    }

    @Test
    void rescheduleShouldWriteLog() throws Exception {
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        RescheduleBo rescheduleBo = new RescheduleBo();
        rescheduleBo.setOrderId(orderId);
        rescheduleBo.setReason("时间冲突");
        rescheduleBo.setNewAppointmentTime(LocalDateTime.now().plusDays(2));
        rescheduleBo.setRemark("希望改到周末");

        postJson("/app/order/reschedule", TestDataFactory.TEST_USER_ID, rescheduleBo)
                .andExpect(jsonPath("$.code").value(0));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.statusKey").value("running"))
                .andExpect(jsonPath("$.data.logs[0].actionType").value("RESCHEDULE"));
    }

    @Test
    void exceptionShouldMoveToExceptionPending() throws Exception {
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        ExceptionBo exceptionBo = new ExceptionBo();
        exceptionBo.setOrderId(orderId);
        exceptionBo.setExceptionType("回收员迟到");
        exceptionBo.setExpectAction("客服回电");
        exceptionBo.setDescription("超过1小时未上门");

        postJson("/app/order/exception", TestDataFactory.TEST_USER_ID, exceptionBo)
                .andExpect(jsonPath("$.code").value(0));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.data.status").value("EXCEPTION_PENDING"));
    }

    @Test
    void cancelShouldMoveToCancelled() throws Exception {
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        CancelOrderBo cancelBo = new CancelOrderBo();
        cancelBo.setOrderId(orderId);

        postJson("/app/order/cancel", TestDataFactory.TEST_USER_ID, cancelBo)
                .andExpect(jsonPath("$.code").value(0));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void adminUpdateStatusShouldTransitionOrder() throws Exception {
        // 下单后自动派单，订单已是 IN_PROGRESS
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo toSettlement =
                new com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo();
        toSettlement.setId(orderId);
        toSettlement.setTargetStatus("WAIT_SETTLEMENT_CONFIRM");
        postAdminJson("/admin/order/updateStatus", toSettlement)
                .andExpect(jsonPath("$.code").value(0));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(orderId);
        postJson("/app/order/info", TestDataFactory.TEST_USER_ID, infoBo)
                .andExpect(jsonPath("$.data.status").value("WAIT_SETTLEMENT_CONFIRM"));
    }

    @Test
    void adminIllegalStatusTransitionShouldFail() throws Exception {
        Long orderId = testDataFactory.createClothesOrder(TestDataFactory.TEST_USER_ID, TestDataFactory.SEED_ADDRESS_ID);

        com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo updateBo =
                new com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo();
        updateBo.setId(orderId);
        updateBo.setTargetStatus("COMPLETED");
        postAdminJson("/admin/order/updateStatus", updateBo)
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("订单状态不允许从[进行中]变更为[已完成]"));
    }
}
