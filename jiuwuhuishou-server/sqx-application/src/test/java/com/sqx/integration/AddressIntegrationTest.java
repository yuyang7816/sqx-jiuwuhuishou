package com.sqx.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.sqx.app.address.domain.bo.UserAddressDeleteBo;
import com.sqx.app.address.domain.bo.UserAddressSaveBo;
import com.sqx.app.address.domain.bo.UserAddressSetDefaultBo;
import com.sqx.app.address.domain.bo.UserAddressUpdateBo;
import com.sqx.config.AbstractIntegrationTest;
import com.sqx.config.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressIntegrationTest extends AbstractIntegrationTest {

    @Test
    void listShouldReturnFullMobile() throws Exception {
        getWithAuth("/app/address/list", TestDataFactory.TEST_USER_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].contactMobile").value("13812348888"));
    }

    @Test
    void updateAddressShouldPersistChanges() throws Exception {
        UserAddressUpdateBo updateBo = new UserAddressUpdateBo();
        updateBo.setId(TestDataFactory.SEED_ADDRESS_ID);
        updateBo.setContactName("更新联系人");
        updateBo.setContactMobile("13812348888");
        updateBo.setProvince("上海市");
        updateBo.setCity("上海市");
        updateBo.setDistrict("浦东新区");
        updateBo.setDetailAddress("测试路1号");
        updateBo.setTag("家");
        updateBo.setIsDefault(1);

        postJson("/app/address/update", TestDataFactory.TEST_USER_ID, updateBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        getWithAuth("/app/address/list", TestDataFactory.TEST_USER_ID)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[?(@.id==1)].contactMobile").value("13812348888"));
    }

    @Test
    void setDefaultShouldLeaveOnlyOneDefaultAddress() throws Exception {
        UserAddressSaveBo saveBo = new UserAddressSaveBo();
        saveBo.setContactName("新地址");
        saveBo.setContactMobile("13900001111");
        saveBo.setProvince("上海市");
        saveBo.setCity("上海市");
        saveBo.setDistrict("静安区");
        saveBo.setDetailAddress("南京西路100号");
        saveBo.setTag("公司");
        saveBo.setIsDefault(1);

        postJson("/app/address/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(jsonPath("$.code").value(0));

        UserAddressSetDefaultBo setDefaultBo = new UserAddressSetDefaultBo();
        setDefaultBo.setId(TestDataFactory.SEED_ADDRESS_ID);
        postJson("/app/address/setDefault", TestDataFactory.TEST_USER_ID, setDefaultBo)
                .andExpect(jsonPath("$.code").value(0));

        String listJson = getWithAuth("/app/address/list", TestDataFactory.TEST_USER_ID)
                .andExpect(jsonPath("$.code").value(0))
                .andReturn().getResponse().getContentAsString();
        int defaultCount = 0;
        for (JsonNode item : objectMapper.readTree(listJson).path("data")) {
            if (item.path("isDefault").asInt() == 1) {
                defaultCount++;
            }
        }
        org.junit.jupiter.api.Assertions.assertEquals(1, defaultCount);
    }

    @Test
    void saveWithEmptyMobileShouldFailValidation() throws Exception {
        UserAddressSaveBo saveBo = new UserAddressSaveBo();
        saveBo.setContactName("无效地址");
        saveBo.setContactMobile("");
        saveBo.setProvince("上海市");
        saveBo.setCity("上海市");
        saveBo.setDistrict("浦东新区");
        saveBo.setDetailAddress("无效路1号");

        postJson("/app/address/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("联系电话不能为空"));
    }

    @Test
    void saveSetDefaultAndDeleteFlow() throws Exception {
        UserAddressSaveBo saveBo = new UserAddressSaveBo();
        saveBo.setContactName("新地址");
        saveBo.setContactMobile("13900001111");
        saveBo.setProvince("上海市");
        saveBo.setCity("上海市");
        saveBo.setDistrict("静安区");
        saveBo.setDetailAddress("南京西路100号");
        saveBo.setTag("公司");
        saveBo.setIsDefault(1);

        postJson("/app/address/save", TestDataFactory.TEST_USER_ID, saveBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        UserAddressSetDefaultBo setDefaultBo = new UserAddressSetDefaultBo();
        setDefaultBo.setId(TestDataFactory.SEED_ADDRESS_ID);
        postJson("/app/address/setDefault", TestDataFactory.TEST_USER_ID, setDefaultBo)
                .andExpect(jsonPath("$.code").value(0));

        UserAddressDeleteBo deleteBo = new UserAddressDeleteBo();
        deleteBo.setId(TestDataFactory.SEED_ADDRESS_ID);
        postJson("/app/address/delete", TestDataFactory.TEST_USER_ID, deleteBo)
                .andExpect(jsonPath("$.code").value(0));
    }
}
