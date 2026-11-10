package com.sqx.app.recycle.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.recycle.config.db.service.IRecycleCategoryService;
import com.sqx.app.recycle.config.db.service.IRecycleRuleService;
import com.sqx.app.recycle.config.db.service.IServiceAreaService;
import com.sqx.app.recycle.config.domain.bo.ServiceAreaCheckBo;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;
import com.sqx.app.recycle.config.domain.vo.RecycleCategoryVo;
import com.sqx.app.recycle.config.domain.vo.RecycleRuleVo;
import com.sqx.app.recycle.config.domain.vo.ServiceAreaCheckVo;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecycleConfigServiceImplTest {

    @Mock
    private IRecycleCategoryService iRecycleCategoryService;
    @Mock
    private IRecycleRuleService iRecycleRuleService;
    @Mock
    private IServiceAreaService iServiceAreaService;

    @InjectMocks
    private RecycleConfigServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(RecycleCategory.class, RecycleRule.class, ServiceArea.class);
    }

    @Test
    void listCategoriesShouldHandleEmptyAndMapped() {
        when(iRecycleCategoryService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.listCategories().isEmpty());

        RecycleCategory category = new RecycleCategory();
        category.setId(1L);
        category.setName("家电");
        category.setState(1);
        when(iRecycleCategoryService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(category));
        List<RecycleCategoryVo> result = service.listCategories();
        assertEquals(1, result.size());
        assertEquals("家电", result.get(0).getName());
    }

    @Test
    void listRulesShouldHandleEmptyAndMapped() {
        when(iRecycleRuleService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.listRules().isEmpty());

        RecycleRule rule = new RecycleRule();
        rule.setId(2L);
        rule.setTitle("上门规则");
        rule.setState(1);
        when(iRecycleRuleService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(rule));
        List<RecycleRuleVo> result = service.listRules();
        assertEquals(1, result.size());
        assertEquals("上门规则", result.get(0).getTitle());
    }

    @Test
    void checkServiceAreaShouldCoverDistrictAndResult() {
        ServiceAreaCheckBo bo = new ServiceAreaCheckBo();
        bo.setProvince("广东省");
        bo.setCity("深圳市");
        when(iServiceAreaService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        ServiceAreaCheckVo emptyDistrict = service.checkServiceArea(bo);
        assertFalse(emptyDistrict.getInService());

        bo.setDistrict("南山区");
        when(iServiceAreaService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        ServiceAreaCheckVo withDistrict = service.checkServiceArea(bo);
        assertTrue(withDistrict.getInService());
    }
}
