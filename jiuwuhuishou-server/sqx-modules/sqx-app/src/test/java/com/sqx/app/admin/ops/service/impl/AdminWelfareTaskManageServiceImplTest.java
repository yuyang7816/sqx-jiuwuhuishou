package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareTaskInfoBo;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareTaskPageBo;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareTaskSaveBo;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareTaskUpdateBo;
import com.sqx.app.welfare.db.service.IWelfareTaskService;
import com.sqx.app.welfare.domain.entity.WelfareTask;
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
class AdminWelfareTaskManageServiceImplTest {

    @Mock
    private IWelfareTaskService iWelfareTaskService;

    @InjectMocks
    private AdminWelfareTaskManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(WelfareTask.class);
    }

    @Test
    void crudAndInfo() {
        AdminWelfareTaskSaveBo saveBo = new AdminWelfareTaskSaveBo();
        saveBo.setTitle("t1");
        service.saveInfo(saveBo);
        verify(iWelfareTaskService).save(any(WelfareTask.class));

        AdminWelfareTaskUpdateBo updateBo = new AdminWelfareTaskUpdateBo();
        updateBo.setId(1L);
        updateBo.setTitle("t2");
        service.updateInfo(updateBo);
        verify(iWelfareTaskService).updateById(any(WelfareTask.class));

        AdminWelfareTaskInfoBo infoBo = new AdminWelfareTaskInfoBo();
        infoBo.setId(1L);
        when(iWelfareTaskService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        WelfareTask task = new WelfareTask();
        task.setId(1L);
        task.setTitle("t2");
        when(iWelfareTaskService.getById(1L)).thenReturn(task);
        assertEquals("t2", service.info(infoBo).getTitle());

        service.delete(infoBo);
        verify(iWelfareTaskService).removeById(1L);
    }

    @Test
    void pageInfoShouldQueryWithFilters() {
        AdminWelfareTaskPageBo pageBo = new AdminWelfareTaskPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setTitle("签到");
        pageBo.setState(1);

        Page<WelfareTask> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new WelfareTask()));
        when(iWelfareTaskService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        verify(iWelfareTaskService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }
}
