package com.sqx.integration;

import com.sqx.app.admin.ops.domain.bo.AdminWelfareTaskPageBo;
import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WelfareIntegrationTest extends AbstractIntegrationTest {

    @Test
    void appTasksShouldReturnSeedData() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/welfare/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void appSignShouldReturnCoinReward() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/welfare/sign")
                        .header("token", tokenForUser(TestDataFactory.TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.coinReward").isNumber());
    }

    @Test
    void adminTaskPageShouldReturnSeedData() throws Exception {
        AdminWelfareTaskPageBo pageBo = new AdminWelfareTaskPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        postAdminJson("/admin/welfare/task/page", pageBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].id").value(1));
    }
}
