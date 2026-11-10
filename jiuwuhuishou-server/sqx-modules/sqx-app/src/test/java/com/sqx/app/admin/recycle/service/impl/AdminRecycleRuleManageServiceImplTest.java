package com.sqx.app.admin.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleRuleInfoBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleRulePageBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleRuleSaveBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleRuleUpdateBo;
import com.sqx.app.recycle.config.db.service.IRecycleRuleService;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import com.sqx.common.core.exception.BizException;
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
class AdminRecycleRuleManageServiceImplTest {

    @Mock
    private IRecycleRuleService iRecycleRuleService;

    @InjectMocks
    private AdminRecycleRuleManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(RecycleRule.class);
    }

    @Test
    void crudAndPage() {
        service.saveInfo(new AdminRecycleRuleSaveBo());
        verify(iRecycleRuleService).save(any(RecycleRule.class));

        AdminRecycleRuleUpdateBo updateBo = new AdminRecycleRuleUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iRecycleRuleService).updateById(any(RecycleRule.class));

        AdminRecycleRuleInfoBo infoBo = new AdminRecycleRuleInfoBo();
        infoBo.setId(1L);
        when(iRecycleRuleService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        RecycleRule entity = new RecycleRule();
        entity.setId(1L);
        entity.setTitle("规则");
        when(iRecycleRuleService.getById(1L)).thenReturn(entity);
        assertEquals("规则", service.info(infoBo).getTitle());

        service.delete(infoBo);
        verify(iRecycleRuleService).removeById(1L);

        AdminRecycleRulePageBo pageBo = new AdminRecycleRulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setTitle("规");
        pageBo.setRuleType("mail");
        pageBo.setState(1);
        Page<RecycleRule> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(entity));
        when(iRecycleRuleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }
}
