package com.sqx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
@TestPropertySource(properties = {
        // 独立内存库，避免与 IntegrationTest 共用 H2 导致 data-test 重复插入
        "spring.datasource.url=jdbc:h2:mem:sqx_context_smoke;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE"
})
class SqxApplicationTests {

    @Test
    void contextLoads() {
    }
}
