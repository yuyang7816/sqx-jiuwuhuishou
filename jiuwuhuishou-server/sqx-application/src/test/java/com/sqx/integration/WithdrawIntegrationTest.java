package com.sqx.integration;

import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordConfirmBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordPageBo;
import com.sqx.app.referral.withdraw.domain.bo.WithdrawRecordSaveBo;
import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WithdrawIntegrationTest extends AbstractIntegrationTest {

    @Test
    void appSaveAndAdminConfirmShouldApproveWithdraw() throws Exception {
        WithdrawRecordSaveBo saveBo = new WithdrawRecordSaveBo();
        saveBo.setAmount(new BigDecimal("10.00"));
        saveBo.setAccountType(1);

        postJson("/app/withdrawRecord/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        WithdrawRecordPageBo pageBo = new WithdrawRecordPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserId(TestDataFactory.TEST_USER_ID);
        pageBo.setStatus(1);

        String pageJson = postAdminJson("/admin/withdrawRecord/page", pageBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].status").value(1))
                .andReturn().getResponse().getContentAsString();

        Long recordId = objectMapper.readTree(pageJson).path("data").path("records").get(0).path("id").asLong();

        WithdrawRecordConfirmBo confirmBo = new WithdrawRecordConfirmBo();
        confirmBo.setId(recordId);
        confirmBo.setStatus(2);
        postAdminJson("/admin/withdrawRecord/confirm", confirmBo)
                .andExpect(jsonPath("$.code").value(0));

        pageBo.setStatus(2);
        postAdminJson("/admin/withdrawRecord/page", pageBo)
                .andExpect(jsonPath("$.data.records[0].status").value(2));
    }
}
