package com.sqx.app.base.banner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.base.banner.domain.bo.BannerInfoBo;
import com.sqx.app.base.banner.domain.bo.BannerPageBo;
import com.sqx.app.base.banner.domain.bo.BannerSaveBo;
import com.sqx.app.base.banner.domain.bo.BannerUpdateBo;
import com.sqx.app.base.banner.domain.bo.BannerUpdateStateBo;
import com.sqx.common.core.exception.BizException;
import com.sqx.system.db.entity.Banner;
import com.sqx.system.db.service.IBannerService;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BannerServiceImplTest {

    @Mock
    private IBannerService iBannerService;

    @InjectMocks
    private BannerServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(Banner.class);
    }

    @Test
    void crudAndUpdateState() {
        BannerSaveBo saveBo = new BannerSaveBo();
        saveBo.setName("home");
        saveBo.setClassify(1);
        service.saveInfo(saveBo);
        verify(iBannerService).save(argThat(b -> Integer.valueOf(1).equals(b.getState())
                && Integer.valueOf(1).equals(b.getClassify())));

        BannerUpdateBo updateBo = new BannerUpdateBo();
        updateBo.setId(1L);
        updateBo.setClassify(5);
        service.updateInfo(updateBo);
        verify(iBannerService).updateById(any(Banner.class));

        BannerInfoBo infoBo = new BannerInfoBo();
        infoBo.setId(1L);
        when(iBannerService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        Banner banner = new Banner();
        banner.setId(1L);
        banner.setClassify(6);
        when(iBannerService.getById(1L)).thenReturn(banner);
        assertEquals(6, service.info(infoBo).getClassify());

        service.delete(infoBo);
        verify(iBannerService).removeById(1L);

        BannerUpdateStateBo stateBo = new BannerUpdateStateBo();
        stateBo.setId(1L);
        stateBo.setState(2);
        service.updateState(stateBo);
        verify(iBannerService).update(any(Banner.class), any(LambdaUpdateWrapper.class));
    }

    @Test
    void pageInfoShouldFilterClassifyAndMap() {
        BannerPageBo pageBo = new BannerPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setName("轮播");
        pageBo.setClassify(1);
        pageBo.setState(1);

        Page<Banner> empty = new Page<>(1, 10);
        empty.setRecords(Collections.emptyList());
        when(iBannerService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(empty);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        Banner banner = new Banner();
        banner.setId(2L);
        banner.setName("分类入口");
        banner.setClassify(5);
        empty.setRecords(Collections.singletonList(banner));
        pageBo.setClassify(5);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        assertEquals(5, service.pageInfo(pageBo).getRecords().get(0).getClassify());

        pageBo.setClassify(6);
        pageBo.setName(null);
        pageBo.setState(null);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }
}
