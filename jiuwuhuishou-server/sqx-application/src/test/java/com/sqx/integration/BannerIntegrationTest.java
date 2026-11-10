package com.sqx.integration;

import com.sqx.app.base.banner.domain.bo.BannerPageBo;
import com.sqx.app.base.banner.domain.bo.BannerUpdateStateBo;
import com.sqx.config.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BannerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void appPageClassify1ShouldReturnEnabledCarousel() throws Exception {
        BannerPageBo pageBo = new BannerPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setClassify(1);
        pageBo.setState(1);

        postJsonNoAuth("/app/base/banner/page", pageBo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[?(@.id==1)]").exists())
                .andExpect(jsonPath("$.data.records[?(@.id==4)]").doesNotExist());
    }

    @Test
    void appPageClassify5ShouldReturnHomeCategory() throws Exception {
        BannerPageBo pageBo = new BannerPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setClassify(5);
        pageBo.setState(1);

        postJsonNoAuth("/app/base/banner/page", pageBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].classify").value(5))
                .andExpect(jsonPath("$.data.records[0].id").value(2));
    }

    @Test
    void appPageClassify6ShouldReturnNotice() throws Exception {
        BannerPageBo pageBo = new BannerPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setClassify(6);
        pageBo.setState(1);

        postJsonNoAuth("/app/base/banner/page", pageBo)
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].classify").value(6))
                .andExpect(jsonPath("$.data.records[0].id").value(3));
    }

    @Test
    void adminUpdateStateShouldHideBannerFromAppList() throws Exception {
        BannerUpdateStateBo disableBo = new BannerUpdateStateBo();
        disableBo.setId(1L);
        disableBo.setState(2);
        postAdminJson("/admin/base/banner/updateState", disableBo)
                .andExpect(jsonPath("$.code").value(0));

        BannerPageBo pageBo = new BannerPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setClassify(1);
        pageBo.setState(1);

        postJsonNoAuth("/app/base/banner/page", pageBo)
                .andExpect(jsonPath("$.data.records[?(@.id==1)]").doesNotExist());

        BannerUpdateStateBo enableBo = new BannerUpdateStateBo();
        enableBo.setId(1L);
        enableBo.setState(1);
        postAdminJson("/admin/base/banner/updateState", enableBo)
                .andExpect(jsonPath("$.code").value(0));
    }
}
