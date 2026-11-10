package com.sqx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqx.common.shiro.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * 集成测试基类：全 Spring 上下文 + MockMvc + H2
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@org.springframework.test.context.TestPropertySource(properties = {
        "sqx.shiro.redis=false"
})
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestDataFactory testDataFactory;

    protected String tokenForUser(Long userId) {
        return jwtUtil.generateToken(userId);
    }

    protected String adminToken() {
        return testDataFactory.adminToken();
    }

    protected ResultActions postJson(String url, Long userId, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .header("token", tokenForUser(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions postAdminJson(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .header("token", adminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions getAdmin(String url) throws Exception {
        return mockMvc.perform(get(url).header("token", adminToken()));
    }

    protected ResultActions postJsonNoAuth(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions getWithAuth(String url, Long userId) throws Exception {
        return mockMvc.perform(get(url).header("token", tokenForUser(userId)));
    }
}
