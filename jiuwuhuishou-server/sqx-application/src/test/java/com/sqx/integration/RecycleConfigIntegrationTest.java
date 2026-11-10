package com.sqx.integration;

import com.sqx.app.recycle.appliance.domain.bo.ApplianceEstimateBo;
import com.sqx.app.recycle.config.domain.bo.ServiceAreaCheckBo;
import com.sqx.app.recycle.digital.domain.bo.DigitalEstimateBo;
import com.sqx.config.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecycleConfigIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void categoryListShouldReturnSeedData() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].code").value("clothes"));
    }

    @Test
    void ruleListShouldReturnSeedData() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/rule/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].title").exists());
    }

    @Test
    void serviceAreaCheckShouldReturnInService() throws Exception {
        // 用 JDBC 写入，避免 SQL 脚本编码导致中文省份匹配失败
        jdbcTemplate.update(
                "DELETE FROM service_area WHERE province = ? AND city = ?",
                "上海市", "上海市");
        jdbcTemplate.update(
                "INSERT INTO service_area (province, city, district, state, create_time, update_time) VALUES (?, ?, ?, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                "上海市", "上海市", "浦东新区");

        ServiceAreaCheckBo checkBo = new ServiceAreaCheckBo();
        checkBo.setProvince("上海市");
        checkBo.setCity("上海市");
        checkBo.setDistrict("浦东新区");

        postJsonNoAuth("/app/recycle/serviceArea/check", checkBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.inService").value(true));
    }

    @Test
    void applianceEstimateShouldReturnPrice() throws Exception {
        ApplianceEstimateBo estimateBo = new ApplianceEstimateBo();
        estimateBo.setModelId(1L);
        estimateBo.setCondition("fair");

        postJsonNoAuth("/app/recycle/appliance/estimate", estimateBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.estimatePrice").value(266.0));
    }

    @Test
    void digitalEstimateShouldReturnPrice() throws Exception {
        DigitalEstimateBo estimateBo = new DigitalEstimateBo();
        estimateBo.setModelId(2L);
        estimateBo.setCondition("fair");

        postJsonNoAuth("/app/recycle/digital/estimate", estimateBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.estimatePrice").isNumber())
                .andExpect(jsonPath("$.data.estimatePrice").value(org.hamcrest.Matchers.greaterThan(0.0)));
    }

    @Test
    void digitalSpecPageShouldReturnSections() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/digital/spec/page")
                        .param("modelId", "2")
                        .param("scene", "digital"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.modelName").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.data.sections[0].key").value("storage"))
                .andExpect(jsonPath("$.data.sections[0].options[0]").value("256G"));
    }

    @Test
    void clothesEstimateConfigShouldReturnWeights() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/clothes/estimate/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.weights[0].title").value("5-20kg"))
                .andExpect(jsonPath("$.data.weights[0].estimateAmount").value(18.5));
    }

    @Test
    void clothesCategoryListShouldReturnDefaults() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/clothes/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("上衣/裤子"));
    }

    @Test
    void bookCategoryListShouldReturnDefaults() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/book/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("文学小说"));
    }

    @Test
    void bookEstimateConfigShouldReturnWeights() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/book/estimate/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.weights[0].estimateAmount").value(5.0));
    }

    @Test
    void appointmentOptionsShouldReturnSeedSlots() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/order/appointment/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].label").value("今日"))
                .andExpect(jsonPath("$.data[0].slots[0].time").value("09:00 - 12:00"));
    }

    @Test
    void applianceHeroFlowShouldReturnSeedData() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/recycle/appliance/heroFlow/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void serviceAreaCheckShouldReturnOutOfService() throws Exception {
        ServiceAreaCheckBo checkBo = new ServiceAreaCheckBo();
        checkBo.setProvince("新疆维吾尔自治区");
        checkBo.setCity("乌鲁木齐市");
        checkBo.setDistrict("天山区");

        postJsonNoAuth("/app/recycle/serviceArea/check", checkBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.inService").value(false));
    }
}
