package com.sqx.app.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderRescheduleInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderReschedulePageBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderRescheduleVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IOrderRescheduleService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderReschedule;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminOrderRescheduleManageServiceImplTest {

    @Mock
    private IOrderRescheduleService iOrderRescheduleService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private AdminOrderRescheduleManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderReschedule.class, RecycleOrder.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminOrderRescheduleVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminOrderRescheduleVo.class));
    }

    @Test
    void pageInfoShouldReturnEmptyWhenFilteredOrderIdsEmpty() {
        when(adminLookupSupport.resolveOrderIds("x")).thenReturn(Collections.emptyList());
        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("x");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
        verify(iOrderRescheduleService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageInfoShouldFilterByOrderIdDirectly() {
        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderId(99L);

        Page<OrderReschedule> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iOrderRescheduleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
        verify(iOrderRescheduleService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageInfoShouldIntersectOrderNoAndUserFilters() {
        when(adminLookupSupport.resolveOrderIds("RO")).thenReturn(Arrays.asList(1L, 2L));
        when(adminLookupSupport.resolveUserIds("u", null)).thenReturn(Collections.singletonList(10L));

        RecycleOrder order = new RecycleOrder();
        order.setId(1L);
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(order));

        OrderReschedule record = new OrderReschedule();
        record.setId(5L);
        record.setOrderId(1L);
        Page<OrderReschedule> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(record));
        when(iOrderRescheduleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        RecycleOrder linked = new RecycleOrder();
        linked.setId(1L);
        linked.setOrderNo("RO001");
        linked.setUserId(10L);
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.singletonList(linked));

        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("RO");
        pageBo.setUserName("u");

        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        assertEquals("RO001", service.pageInfo(pageBo).getRecords().get(0).getOrderNo());
    }

    @Test
    void pageInfoShouldReturnByUserWhenOrderNoNull() {
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        when(adminLookupSupport.resolveUserIds("u", null)).thenReturn(Collections.singletonList(10L));

        RecycleOrder order = new RecycleOrder();
        order.setId(3L);
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(order));

        Page<OrderReschedule> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iOrderRescheduleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("u");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldReturnByOrderNoWhenUserNull() {
        when(adminLookupSupport.resolveOrderIds("RO")).thenReturn(Collections.singletonList(1L));
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        Page<OrderReschedule> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iOrderRescheduleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("RO");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldReturnEmptyWhenUserHasNoOrders() {
        when(adminLookupSupport.resolveUserIds("u", null)).thenReturn(Collections.singletonList(10L));
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("u");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminOrderRescheduleInfoBo infoBo = new AdminOrderRescheduleInfoBo();
        infoBo.setId(1L);
        when(iOrderRescheduleService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void infoShouldReturnVoWithAndWithoutOrder() {
        AdminOrderRescheduleInfoBo infoBo = new AdminOrderRescheduleInfoBo();
        infoBo.setId(1L);

        OrderReschedule record = new OrderReschedule();
        record.setId(1L);
        record.setOrderId(10L);
        when(iOrderRescheduleService.getById(1L)).thenReturn(record);
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.emptyList());

        assertNull(service.info(infoBo).getOrderNo());

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setOrderNo("RO001");
        order.setUserId(100L);
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.singletonList(order));

        assertEquals("RO001", service.info(infoBo).getOrderNo());
    }

    @Test
    void listByOrderIdShouldHandleNullEmptyAndRecords() {
        assertTrue(service.listByOrderId(null).isEmpty());

        when(iOrderRescheduleService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.listByOrderId(1L).isEmpty());

        OrderReschedule record = new OrderReschedule();
        record.setId(1L);
        record.setOrderId(10L);
        when(iOrderRescheduleService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(record));
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.emptyList());

        List<AdminOrderRescheduleVo> list = service.listByOrderId(10L);
        assertEquals(1, list.size());
    }

    @Test
    void pageInfoShouldReturnNullFilterWhenNoCriteria() {
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        Page<OrderReschedule> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iOrderRescheduleService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminOrderReschedulePageBo pageBo = new AdminOrderReschedulePageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void listByOrderIdShouldHandleRecordsWithoutOrderIds() {
        OrderReschedule record = new OrderReschedule();
        record.setId(1L);
        record.setOrderId(null);
        when(iOrderRescheduleService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(record));

        assertEquals(1, service.listByOrderId(10L).size());
    }
}
