package com.sqx.integration;

import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void loginWithPasswordShouldReturnToken() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/phonePasswordLogin")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(String.format("{\"phoneNo\":\"%s\",\"password\":\"%s\"}",
                                TestDataFactory.TEST_PHONE, TestDataFactory.TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void phoneCodeLoginShouldReturnTokenForExistingUser() throws Exception {
        testDataFactory.seedPhoneVerifyCode(TestDataFactory.TEST_PHONE, "register");
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/phoneCodeLogin")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(String.format("{\"phoneNo\":\"%s\",\"verifyCode\":\"%s\"}",
                                TestDataFactory.TEST_PHONE, TestDataFactory.TEST_VERIFY_CODE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void phoneRegisterShouldCreateNewUser() throws Exception {
        String newPhone = "13800000099";
        testDataFactory.seedPhoneVerifyCode(newPhone, "register");
        String body = String.format(
                "{\"phoneNo\":\"%s\",\"code\":\"%s\",\"password\":\"%s\",\"username\":\"新用户\"}",
                newPhone, TestDataFactory.TEST_VERIFY_CODE, TestDataFactory.TEST_PASSWORD);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/phoneRegister")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void phoneCodeLoginWithWrongCodeShouldFail() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/phoneCodeLogin")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(String.format("{\"phoneNo\":\"%s\",\"verifyCode\":\"000000\"}", TestDataFactory.TEST_PHONE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(350004));
    }

    @Test
    void logoutShouldInvalidateTokenForProfile() throws Exception {
        String token = testDataFactory.loginByPassword();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/logout")
                        .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/base/user/info")
                        .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(org.springframework.http.HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void resetPasswordShouldSucceedForExistingUser() throws Exception {
        testDataFactory.seedPhoneVerifyCode(TestDataFactory.TEST_PHONE, "resetPassword");
        String body = String.format(
                "{\"phone\":\"%s\",\"code\":\"%s\",\"password\":\"87654321\"}",
                TestDataFactory.TEST_PHONE, TestDataFactory.TEST_VERIFY_CODE);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/resetPassword")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void profileShouldRequireToken() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/app/base/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(org.springframework.http.HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void profileShouldReturnUserWhenAuthenticated() throws Exception {
        getWithAuth("/app/base/user/info", TestDataFactory.TEST_USER_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.phone").value(TestDataFactory.TEST_PHONE));
    }

    @Test
    void loginWithWrongPasswordShouldFail() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/app/base/auth/phonePasswordLogin")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(String.format("{\"phoneNo\":\"%s\",\"password\":\"wrong-pass\"}",
                                TestDataFactory.TEST_PHONE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(350003));
    }
}
