package com.sqx.app.brand.service.impl;

import com.sqx.app.brand.db.service.IBrandCooperationService;
import com.sqx.app.brand.domain.bo.BrandCooperationSaveBo;
import com.sqx.app.brand.domain.entity.BrandCooperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BrandCooperationServiceImplTest {

    @Mock
    private IBrandCooperationService iBrandCooperationService;

    @InjectMocks
    private BrandCooperationServiceImpl service;

    @Test
    void saveShouldFillPendingAndDefaultImage() {
        BrandCooperationSaveBo saveBo = new BrandCooperationSaveBo();
        saveBo.setBrandName("品牌A");
        saveBo.setContactName("张三");
        saveBo.setMobile("13800000000");
        saveBo.setProvince("陕");
        saveBo.setCity("西安");
        saveBo.setDistrict("雁塔");
        saveBo.setAddress("科技路");
        saveBo.setBrandImage(null);

        service.save(9L, saveBo);

        ArgumentCaptor<BrandCooperation> captor = ArgumentCaptor.forClass(BrandCooperation.class);
        verify(iBrandCooperationService).save(captor.capture());
        BrandCooperation entity = captor.getValue();
        assertEquals(9L, entity.getUserId());
        assertEquals(1, entity.getStatus());
        assertEquals("", entity.getBrandImage());
        assertEquals("品牌A", entity.getBrandName());
    }

    @Test
    void saveShouldKeepBrandImage() {
        BrandCooperationSaveBo saveBo = new BrandCooperationSaveBo();
        saveBo.setBrandName("品牌B");
        saveBo.setBrandImage("http://img");
        service.save(1L, saveBo);
        ArgumentCaptor<BrandCooperation> captor = ArgumentCaptor.forClass(BrandCooperation.class);
        verify(iBrandCooperationService).save(captor.capture());
        assertEquals("http://img", captor.getValue().getBrandImage());
    }
}
