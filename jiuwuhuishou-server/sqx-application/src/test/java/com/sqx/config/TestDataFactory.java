package com.sqx.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqx.app.address.domain.bo.UserAddressSaveBo;
import com.sqx.app.order.domain.bo.ApplianceOrderSaveBo;
import com.sqx.app.order.domain.bo.ClothesOrderSaveBo;
import com.sqx.app.order.domain.bo.DigitalOrderSaveBo;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.common.shiro.constant.ShiroConstant;
import com.sqx.common.shiro.utils.JwtUtil;
import com.sqx.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 集成测试数据工厂
 */
@Component
public class TestDataFactory {

    public static final Long TEST_USER_ID = 100L;
    public static final Long ADMIN_USER_ID = 1L;
    public static final Long STAFF_USER_ID = 200L;
    public static final String TEST_PHONE = "13800000001";
    public static final String TEST_PASSWORD = "12345678";
    public static final String TEST_VERIFY_CODE = "888888";
    public static final Long SEED_ADDRESS_ID = 1L;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SysUserService sysUserService;

    @Value("${sqx.jwt.expire:2592000}")
    private Long jwtExpireSeconds;

    public String tokenForUser(Long userId) {
        return jwtUtil.generateToken(userId);
    }

    /**
     * 管理端 JWT：写入 Shiro SYS_TOKEN Redis 键（与 SysLoginController 一致）
     */
    public String adminToken() {
        sysUserService.refreshPermissionCache(ADMIN_USER_ID);
        String token = jwtUtil.generateToken(ADMIN_USER_ID);
        RedisUtil.setCacheObject(
                String.format(ShiroConstant.SYS_TOKEN, ADMIN_USER_ID),
                token,
                Duration.ofSeconds(jwtExpireSeconds));
        return token;
    }

    public String loginByPassword() throws Exception {
        String body = String.format("{\"phoneNo\":\"%s\",\"password\":\"%s\"}", TEST_PHONE, TEST_PASSWORD);
        MvcResult result = mockMvc.perform(post("/app/base/auth/phonePasswordLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    public Long createAddress(Long userId) throws Exception {
        UserAddressSaveBo saveBo = new UserAddressSaveBo();
        saveBo.setContactName("集成测试");
        saveBo.setContactMobile("13800009999");
        saveBo.setProvince("上海市");
        saveBo.setCity("上海市");
        saveBo.setDistrict("浦东新区");
        saveBo.setDetailAddress("联调路88号");
        saveBo.setTag("公司");
        saveBo.setIsDefault(0);

        mockMvc.perform(post("/app/address/save")
                        .header("token", tokenForUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveBo)))
                .andExpect(jsonPath("$.code").value(0));

        MvcResult listResult = mockMvc.perform(get("/app/address/list")
                        .header("token", tokenForUser(userId)))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        JsonNode list = objectMapper.readTree(listResult.getResponse().getContentAsString()).path("data");
        for (JsonNode item : list) {
            if ("联调路88号".equals(item.path("detailAddress").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new IllegalStateException("新建地址未找到");
    }

    /**
     * 书籍回收下单：与 App 端一致，走 clothes/save + categoryName=书籍回收，服务端标记 orderType=book
     */
    public Long createBookOrder(Long userId, Long addressId) throws Exception {
        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(addressId);
        saveBo.setCategoryName("书籍回收");
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("约5kg");
        saveBo.setEstimateAmount(new BigDecimal("5.00"));

        MvcResult result = mockMvc.perform(post("/app/order/clothes/save")
                        .header("token", tokenForUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveBo)))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").asLong();
    }

    public Long createClothesOrder(Long userId, Long addressId) throws Exception {
        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(addressId);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("约18kg");
        saveBo.setEstimateAmount(new BigDecimal("18.00"));

        MvcResult result = mockMvc.perform(post("/app/order/clothes/save")
                        .header("token", tokenForUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveBo)))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").asLong();
    }

    public Long createApplianceOrder(Long userId, Long addressId) throws Exception {
        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(addressId);
        saveBo.setModelId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setEstimateAmount(new BigDecimal("226.00"));

        MvcResult result = mockMvc.perform(post("/app/order/appliance/save")
                        .header("token", tokenForUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveBo)))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").asLong();
    }

    public Long createApplianceMailOrder(Long userId, Long addressId) throws Exception {
        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(addressId);
        saveBo.setModelId(1L);
        saveBo.setFulfillType("mail");
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setEstimateAmount(new BigDecimal("226.00"));

        MvcResult result = mockMvc.perform(post("/app/order/appliance/save")
                        .header("token", tokenForUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveBo)))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").asLong();
    }

    public Long createDigitalOrder(Long userId, Long addressId) throws Exception {
        DigitalOrderSaveBo saveBo = new DigitalOrderSaveBo();
        saveBo.setAddressId(addressId);
        saveBo.setModelId(2L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setEstimateAmount(new BigDecimal("6120.00"));

        MvcResult result = mockMvc.perform(post("/app/order/digital/save")
                        .header("token", tokenForUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveBo)))
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").asLong();
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public void seedPhoneVerifyCode(String phone, String bizCode) throws Exception {
        String body = String.format("{\"phone\":\"%s\",\"bizCode\":\"%s\"}", phone, bizCode);
        mockMvc.perform(post("/app/base/verifyCode/sendPhoneCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(jsonPath("$.code").value(0));
    }
}
