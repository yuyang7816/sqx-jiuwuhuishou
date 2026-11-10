package com.sqx.app.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.db.service.IOrderRescheduleService;
import com.sqx.app.order.db.service.IRecycleOrderItemService;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.bo.ApplianceOrderSaveBo;
import com.sqx.app.order.domain.bo.CancelOrderBo;
import com.sqx.app.order.domain.bo.ClothesOrderSaveBo;
import com.sqx.app.order.domain.bo.DigitalOrderSaveBo;
import com.sqx.app.order.domain.bo.ExceptionBo;
import com.sqx.app.order.domain.bo.RecycleOrderInfoBo;
import com.sqx.app.order.domain.bo.RecycleOrderPageBo;
import com.sqx.app.order.domain.bo.RescheduleBo;
import com.sqx.app.order.domain.bo.ShipMailOrderBo;
import com.sqx.app.order.domain.entity.OrderException;
import com.sqx.app.order.domain.entity.OrderReschedule;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderItem;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.domain.vo.OrderCourierVo;
import com.sqx.app.order.domain.vo.RecycleOrderDetailVo;
import com.sqx.app.order.domain.vo.RecycleOrderVo;
import com.sqx.app.order.enums.RecycleFulfillType;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.enums.RecycleOrderType;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.pickup.service.PickupTaskAssignService;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductCategory;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.support.ProductRecycleSupport;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecycleOrderServiceTest {

    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderItemService iRecycleOrderItemService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Mock
    private IUserAddressService iUserAddressService;
    @Mock
    private IOrderRescheduleService iOrderRescheduleService;
    @Mock
    private IOrderExceptionService iOrderExceptionService;
    @Mock
    private IPickupTaskService iPickupTaskService;
    @Mock
    private PickupTaskAssignService pickupTaskAssignService;
    @Mock
    private ProductRecycleSupport productRecycleSupport;
    @Mock
    private IUserService iUserService;
    @Mock
    private com.sqx.app.express.sf.SfExpressOrderService sfExpressOrderService;
    @Mock
    private com.sqx.app.express.aliyun.AliyunLogisticsService aliyunLogisticsService;
    @Mock
    private com.sqx.app.order.service.AppointmentOptionService appointmentOptionService;

    @InjectMocks
    private RecycleOrderServiceImpl recycleOrderService;

    @BeforeAll
    static void initLambdaCache() {
        MybatisPlusLambdaInit.init(
                RecycleOrder.class,
                UserAddress.class,
                RecycleOrderLog.class,
                PickupTask.class
        );
    }

    @Test
    void createClothesOrderShouldPersistOrderItemAndLog() {
        stubAddress();
        stubSaveOrder(100L);

        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("约18kg");
        saveBo.setEstimateAmount(new BigDecimal("18.00"));

        Long orderId = recycleOrderService.createClothesOrder(200L, saveBo);

        assertEquals(100L, orderId);
        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).save(orderCaptor.capture());
        assertEquals(RecycleOrderType.CLOTHES.getCode(), orderCaptor.getValue().getOrderType());
        verify(iRecycleOrderItemService).save(any(RecycleOrderItem.class));
        verify(iRecycleOrderLogService).save(argThat(log -> "CREATE".equals(log.getActionType())));
        verify(pickupTaskAssignService).assignForOrder(any(RecycleOrder.class), anyString());
    }

    @Test
    void createClothesOrderShouldMarkBookWhenCategoryContainsBook() {
        stubAddress();
        stubSaveOrder(101L);

        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setCategoryName("书籍回收");
        saveBo.setWeight("约 10 20 kg");
        saveBo.setEstimateAmount(new BigDecimal("12.00"));

        recycleOrderService.createClothesOrder(200L, saveBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).save(orderCaptor.capture());
        assertEquals(RecycleOrderType.BOOK.getCode(), orderCaptor.getValue().getOrderType());
        ArgumentCaptor<RecycleOrderItem> itemCaptor = ArgumentCaptor.forClass(RecycleOrderItem.class);
        verify(iRecycleOrderItemService).save(itemCaptor.capture());
        assertEquals(0, new BigDecimal("15").compareTo(itemCaptor.getValue().getQuantity()));
    }

    @Test
    void createClothesOrderShouldIgnoreMalformedWeightRange() {
        stubAddress();
        stubSaveOrder(104L);

        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("10-20kg");

        recycleOrderService.createClothesOrder(200L, saveBo);

        ArgumentCaptor<RecycleOrderItem> itemCaptor = ArgumentCaptor.forClass(RecycleOrderItem.class);
        verify(iRecycleOrderItemService).save(itemCaptor.capture());
        assertNull(itemCaptor.getValue().getQuantity());
    }

    @Test
    void createClothesOrderShouldAcceptBlankWeight() {
        stubAddress();
        stubSaveOrder(102L);

        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("  ");

        recycleOrderService.createClothesOrder(200L, saveBo);

        ArgumentCaptor<RecycleOrderItem> itemCaptor = ArgumentCaptor.forClass(RecycleOrderItem.class);
        verify(iRecycleOrderItemService).save(itemCaptor.capture());
        assertNull(itemCaptor.getValue().getQuantity());
        verify(pickupTaskAssignService).assignForOrder(any(RecycleOrder.class), eq("约 1 批"));
    }

    @Test
    void createClothesOrderShouldIgnoreNonNumericWeight() {
        stubAddress();
        stubSaveOrder(103L);

        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setWeight("大概一批");

        recycleOrderService.createClothesOrder(200L, saveBo);

        ArgumentCaptor<RecycleOrderItem> itemCaptor = ArgumentCaptor.forClass(RecycleOrderItem.class);
        verify(iRecycleOrderItemService).save(itemCaptor.capture());
        assertNull(itemCaptor.getValue().getQuantity());
    }

    @Test
    void createApplianceVisitOrderShouldAssignPickup() {
        stubAddress();
        stubSaveOrder(201L);
        ProductModel model = buildModel(9L, "洗衣机", new BigDecimal("100"));
        ProductCategory category = new ProductCategory();
        category.setName("大家电");
        when(productRecycleSupport.requireEnabledModel(9L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);

        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setModelId(9L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setFulfillType(RecycleFulfillType.VISIT.getCode());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            Long id = recycleOrderService.createApplianceOrder(200L, saveBo);
            assertEquals(201L, id);
        }

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).save(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.WAIT_ASSIGN.getCode(), orderCaptor.getValue().getStatus());
        verify(pickupTaskAssignService).assignForOrder(any(RecycleOrder.class), contains("洗衣机"));
    }

    @Test
    void createApplianceMailOrderShouldSkipAssignAndUseMailAddress() {
        stubAddress();
        stubSaveOrder(202L);
        ProductModel model = buildModel(9L, "冰箱", new BigDecimal("200"));
        ProductCategory category = new ProductCategory();
        category.setName("大家电");
        when(productRecycleSupport.requireEnabledModel(9L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);

        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setModelId(9L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setFulfillType(RecycleFulfillType.MAIL.getCode());
        saveBo.setEstimateAmount(new BigDecimal("188"));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver")))
                    .thenReturn("");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver_phone")))
                    .thenReturn("");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver_address")))
                    .thenReturn("");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("appliance_mail_address")))
                    .thenReturn(" 自定义邮寄地址 ");
            when(sfExpressOrderService.isConfigured()).thenReturn(true);
            when(sfExpressOrderService.placeMailOrder(any(RecycleOrder.class), any(UserAddress.class)))
                    .thenReturn("SF10001");
            recycleOrderService.createApplianceOrder(200L, saveBo);
        }

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).save(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.IN_PROGRESS.getCode(), orderCaptor.getValue().getStatus());
        assertEquals("自定义邮寄地址", orderCaptor.getValue().getMailAddressText().trim());
        verify(sfExpressOrderService).placeMailOrder(any(RecycleOrder.class), any(UserAddress.class));
        verify(iRecycleOrderService, atLeastOnce()).updateById(argThat(order -> "SF10001".equals(order.getExpressNo())));
        verify(pickupTaskAssignService, never()).assignForOrder(any(), anyString());
    }

    @Test
    void createApplianceMailOrderShouldComposePlatformReceiverConfig() {
        stubAddress();
        stubSaveOrder(203L);
        ProductModel model = buildModel(9L, "冰箱", new BigDecimal("200"));
        ProductCategory category = new ProductCategory();
        category.setName("大家电");
        when(productRecycleSupport.requireEnabledModel(9L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);

        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setModelId(9L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setFulfillType(RecycleFulfillType.MAIL.getCode());
        saveBo.setEstimateAmount(new BigDecimal("188"));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver")))
                    .thenReturn("仓储一部");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver_phone")))
                    .thenReturn("021-88886666");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver_address")))
                    .thenReturn("上海市浦东新区测试路1号");
            when(sfExpressOrderService.isConfigured()).thenReturn(true);
            when(sfExpressOrderService.placeMailOrder(any(RecycleOrder.class), any(UserAddress.class)))
                    .thenReturn("SF10002");
            recycleOrderService.createApplianceOrder(200L, saveBo);
        }

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).save(orderCaptor.capture());
        assertEquals("上海市浦东新区测试路1号 收件人：仓储一部 电话：021-88886666",
                orderCaptor.getValue().getMailAddressText());
        verify(sfExpressOrderService).placeMailOrder(any(RecycleOrder.class), any(UserAddress.class));
        verify(pickupTaskAssignService, never()).assignForOrder(any(), anyString());
    }

    @Test
    void createApplianceMailOrderShouldSkipSfWhenNotConfigured() {
        stubAddress();
        stubSaveOrder(204L);
        ProductModel model = buildModel(9L, "冰箱", new BigDecimal("200"));
        ProductCategory category = new ProductCategory();
        category.setName("大家电");
        when(productRecycleSupport.requireEnabledModel(9L)).thenReturn(model);
        when(productRecycleSupport.resolveCategoryByModel(model)).thenReturn(category);
        when(sfExpressOrderService.isConfigured()).thenReturn(false);

        ApplianceOrderSaveBo saveBo = new ApplianceOrderSaveBo();
        saveBo.setAddressId(1L);
        saveBo.setModelId(9L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        saveBo.setFulfillType(RecycleFulfillType.MAIL.getCode());
        saveBo.setEstimateAmount(new BigDecimal("188"));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            Long id = recycleOrderService.createApplianceOrder(200L, saveBo);
            assertEquals(204L, id);
        }

        verify(sfExpressOrderService, never()).placeMailOrder(any(), any());
        verify(pickupTaskAssignService, never()).assignForOrder(any(), anyString());
        verify(iRecycleOrderLogService, atLeastOnce()).save(argThat(orderLog ->
                "EXPRESS_SKIP".equals(orderLog.getActionType())
                        && "待顺丰取件".equals(orderLog.getContent())));
    }

    @Test
    void createDigitalAndLuxuryOrdersShouldPersistAsMailWithoutAssign() {
        stubAddress();
        ProductModel model = buildModel(3L, "iPhone", new BigDecimal("500"));
        model.setBrandId(7L);
        ProductBrand brand = new ProductBrand();
        brand.setName("Apple");
        when(productRecycleSupport.requireEnabledModel(3L)).thenReturn(model);
        when(productRecycleSupport.requireEnabledBrand(7L)).thenReturn(brand);

        stubSaveOrder(301L);
        DigitalOrderSaveBo digitalBo = new DigitalOrderSaveBo();
        digitalBo.setAddressId(1L);
        digitalBo.setModelId(3L);
        digitalBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver")))
                    .thenReturn("仓储一部");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver_phone")))
                    .thenReturn("021-88886666");
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("platform_mail_receiver_address")))
                    .thenReturn("上海市浦东新区测试路1号");
            when(sfExpressOrderService.isConfigured()).thenReturn(true);
            when(sfExpressOrderService.placeMailOrder(any(RecycleOrder.class), any(UserAddress.class)))
                    .thenReturn("SF20001");
            recycleOrderService.createDigitalOrder(200L, digitalBo);
        }
        ArgumentCaptor<RecycleOrder> digitalCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).save(digitalCaptor.capture());
        RecycleOrder digitalOrder = digitalCaptor.getValue();
        assertEquals(RecycleOrderType.DIGITAL.getCode(), digitalOrder.getOrderType());
        assertEquals(RecycleFulfillType.MAIL.getCode(), digitalOrder.getFulfillType());
        assertEquals(RecycleOrderStatus.IN_PROGRESS.getCode(), digitalOrder.getStatus());
        assertEquals("待顺丰寄件取件", digitalOrder.getStatusDesc());
        assertEquals("上海市浦东新区测试路1号 收件人：仓储一部 电话：021-88886666", digitalOrder.getMailAddressText());

        stubSaveOrder(302L);
        DigitalOrderSaveBo luxuryBo = new DigitalOrderSaveBo();
        luxuryBo.setAddressId(1L);
        luxuryBo.setModelId(3L);
        luxuryBo.setScene("luxury");
        luxuryBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("");
            when(sfExpressOrderService.isConfigured()).thenReturn(true);
            when(sfExpressOrderService.placeMailOrder(any(RecycleOrder.class), any(UserAddress.class)))
                    .thenReturn("SF20002");
            recycleOrderService.createDigitalOrder(200L, luxuryBo);
        }
        verify(iRecycleOrderService, atLeastOnce())
                .save(argThat(o -> RecycleOrderType.LUXURY.getCode().equals(o.getOrderType())
                        && RecycleFulfillType.MAIL.getCode().equals(o.getFulfillType())));
        verify(pickupTaskAssignService, never()).assignForOrder(any(), anyString());
    }

    @Test
    void pageInfoShouldMapRecordsAndStatusKey() {
        RecycleOrder order = ownedOrder(1L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        order.setContactMobile("13812345678");
        order.setEstimateAmountConfirmed(0);
        LocalDateTime appointmentStart = LocalDateTime.of(2026, 9, 24, 14, 0, 0);
        order.setAppointmentTime(appointmentStart);
        when(appointmentOptionService.resolveSlotEndTime(appointmentStart))
                .thenReturn(appointmentStart.withHour(16));
        Page<RecycleOrder> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(order));
        page.setTotal(1);
        when(iRecycleOrderService.page(any(Page.class), any())).thenReturn(page);

        RecycleOrderPageBo pageBo = new RecycleOrderPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setStatusKey("paying");
        pageBo.setOrderType("clothes");

        IPage<RecycleOrderVo> result = recycleOrderService.pageInfo(200L, pageBo);

        assertEquals(1, result.getRecords().size());
        assertEquals("138****5678", result.getRecords().get(0).getContactMobile());
        assertTrue(Boolean.TRUE.equals(result.getRecords().get(0).getEstimateAmountConfirmed()));
        assertEquals(LocalDateTime.of(2026, 9, 24, 16, 0, 0), result.getRecords().get(0).getAppointmentEndTime());
    }

    @Test
    void pageInfoShouldReturnEmptyWhenNoRecords() {
        Page<RecycleOrder> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iRecycleOrderService.page(any(Page.class), any())).thenReturn(page);

        RecycleOrderPageBo pageBo = new RecycleOrderPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        IPage<RecycleOrderVo> result = recycleOrderService.pageInfo(200L, pageBo);
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    void infoShouldIncludeLogsAndConfirmedFlag() {
        RecycleOrder order = ownedOrder(5L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        order.setEstimateAmountConfirmed(1);
        order.setContactMobile("123");
        when(iRecycleOrderService.getById(5L)).thenReturn(order);
        RecycleOrderLog log = new RecycleOrderLog();
        log.setOrderId(5L);
        log.setActionType("CREATE");
        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(log));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(5L);
        RecycleOrderDetailVo detail = recycleOrderService.info(200L, infoBo);

        assertEquals(1, detail.getLogs().size());
        assertTrue(Boolean.TRUE.equals(detail.getEstimateAmountConfirmed()));
        assertEquals("123", detail.getContactMobile());
    }

    @Test
    void infoShouldAllowEmptyLogs() {
        when(iRecycleOrderService.getById(6L)).thenReturn(ownedOrder(6L, RecycleOrderStatus.COMPLETED.getCode()));
        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(6L);
        RecycleOrderDetailVo detail = recycleOrderService.info(200L, infoBo);
        assertNotNull(detail.getLogs());
        assertTrue(detail.getLogs().isEmpty());
        assertTrue(Boolean.TRUE.equals(detail.getEstimateAmountConfirmed()));
    }

    @Test
    void cancelShouldRejectCompletedOrder() {
        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, RecycleOrderStatus.COMPLETED.getCode()));
        CancelOrderBo cancelBo = new CancelOrderBo();
        cancelBo.setOrderId(1L);
        assertThrows(BizException.class, () -> recycleOrderService.cancel(200L, cancelBo));
    }

    @Test
    void cancelShouldSucceedWhenWaitSettlementConfirm() {
        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode()));
        CancelOrderBo cancelBo = new CancelOrderBo();
        cancelBo.setOrderId(1L);
        recycleOrderService.cancel(200L, cancelBo);
        verify(iRecycleOrderService).updateById(argThat(o -> RecycleOrderStatus.CANCELLED.getCode().equals(o.getStatus())));
    }

    @Test
    void cancelShouldSucceedWhenWaitAssign() {
        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode()));
        CancelOrderBo cancelBo = new CancelOrderBo();
        cancelBo.setOrderId(1L);
        recycleOrderService.cancel(200L, cancelBo);
        verify(iRecycleOrderService).updateById(argThat(o -> RecycleOrderStatus.CANCELLED.getCode().equals(o.getStatus())));
        verify(iRecycleOrderLogService).save(argThat(log -> "CANCEL".equals(log.getActionType())));
    }

    @Test
    void cancelShouldCancelSfMailOrderWhenExpressNoPresent() {
        RecycleOrder mailOrder = ownedOrder(23L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        when(iRecycleOrderService.getById(23L)).thenReturn(mailOrder);
        CancelOrderBo cancelBo = new CancelOrderBo();
        cancelBo.setOrderId(23L);
        recycleOrderService.cancel(200L, cancelBo);
        verify(sfExpressOrderService).cancelMailOrder(mailOrder);
    }

    @Test
    void rescheduleShouldPersistRescheduleRecordAndLog() {
        when(iRecycleOrderService.getById(10L)).thenReturn(ownedOrder(10L, RecycleOrderStatus.WAIT_ASSIGN.getCode()));
        when(iPickupTaskService.getOne(any())).thenReturn(null);

        RescheduleBo rescheduleBo = new RescheduleBo();
        rescheduleBo.setOrderId(10L);
        rescheduleBo.setReason("时间冲突");
        rescheduleBo.setNewAppointmentTime(LocalDateTime.now().plusDays(2));
        rescheduleBo.setRemark("改到周末");

        recycleOrderService.reschedule(200L, rescheduleBo);

        verify(iOrderRescheduleService).save(any(OrderReschedule.class));
        verify(iRecycleOrderService).updateById(argThat(order ->
                order.getAppointmentTime() != null
                        && order.getStatusDesc() != null
                        && order.getStatusDesc().contains("已改约至")));
        verify(iRecycleOrderLogService).save(argThat(log -> "RESCHEDULE".equals(log.getActionType())));
    }

    @Test
    void rescheduleShouldSyncPickupTaskWhenPresent() {
        when(iRecycleOrderService.getById(10L)).thenReturn(ownedOrder(10L, RecycleOrderStatus.WAIT_ASSIGN.getCode()));
        PickupTask task = new PickupTask();
        task.setId(88L);
        task.setOrderId(10L);
        task.setStatus(PickupTaskStatus.PENDING.name());
        when(iPickupTaskService.getOne(any())).thenReturn(task);

        RescheduleBo rescheduleBo = new RescheduleBo();
        rescheduleBo.setOrderId(10L);
        rescheduleBo.setNewAppointmentTime(LocalDateTime.of(2026, 8, 1, 10, 0));

        recycleOrderService.reschedule(200L, rescheduleBo);

        verify(iPickupTaskService).updateById(argThat(t -> t.getAppointmentTime() != null));
    }

    @Test
    void rescheduleShouldRejectCancelledOrder() {
        when(iRecycleOrderService.getById(10L)).thenReturn(ownedOrder(10L, RecycleOrderStatus.CANCELLED.getCode()));
        RescheduleBo rescheduleBo = new RescheduleBo();
        rescheduleBo.setOrderId(10L);
        rescheduleBo.setReason("时间冲突");
        rescheduleBo.setNewAppointmentTime(LocalDateTime.now().plusDays(2));
        assertThrows(BizException.class, () -> recycleOrderService.reschedule(200L, rescheduleBo));
    }

    @Test
    void submitExceptionShouldMoveToExceptionPending() {
        when(iRecycleOrderService.getById(11L)).thenReturn(ownedOrder(11L, RecycleOrderStatus.WAIT_ASSIGN.getCode()));

        ExceptionBo exceptionBo = new ExceptionBo();
        exceptionBo.setOrderId(11L);
        exceptionBo.setExceptionType("回收员迟到");
        exceptionBo.setExpectAction("客服回电");
        exceptionBo.setDescription("超过1小时未上门");

        recycleOrderService.submitException(200L, exceptionBo);

        verify(iOrderExceptionService).save(any(OrderException.class));
        verify(iRecycleOrderService).updateById(argThat(updated ->
                RecycleOrderStatus.EXCEPTION_PENDING.getCode().equals(updated.getStatus())));
        verify(iRecycleOrderLogService).save(argThat(log -> "EXCEPTION".equals(log.getActionType())));
    }

    @Test
    void getCourierInfoShouldResolveFromPickupStaff() {
        when(iRecycleOrderService.getById(12L)).thenReturn(ownedOrder(12L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        PickupTask task = new PickupTask();
        task.setStaffId(99L);
        when(iPickupTaskService.getOne(any())).thenReturn(task);
        UserEntity staff = new UserEntity();
        staff.setUserName("小李");
        staff.setPhone("13900001111");
        when(iUserService.getById(99L)).thenReturn(staff);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(12L);
        OrderCourierVo vo = recycleOrderService.getCourierInfo(200L, infoBo);

        assertEquals("小李", vo.getName());
        assertEquals("139****1111", vo.getMobileMask());
    }

    @Test
    void getCourierInfoShouldFallbackToConfigPool() {
        when(iRecycleOrderService.getById(13L)).thenReturn(ownedOrder(13L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        when(iPickupTaskService.getOne(any())).thenReturn(null);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(13L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("order_courier_pool")))
                    .thenReturn("[{\"name\":\"配置员\",\"mobile\":\"13700002222\"}]");
            OrderCourierVo vo = recycleOrderService.getCourierInfo(200L, infoBo);
            assertEquals("配置员", vo.getName());
            assertEquals("137****2222", vo.getMobileMask());
        }
    }

    @Test
    void getCourierInfoShouldThrowWhenUnavailable() {
        when(iRecycleOrderService.getById(14L)).thenReturn(ownedOrder(14L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        when(iPickupTaskService.getOne(any())).thenReturn(null);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(14L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("not-json");
            assertThrows(BizException.class, () -> recycleOrderService.getCourierInfo(200L, infoBo));
        }
    }

    @Test
    void getCourierInfoShouldIgnoreInvalidPickupStaff() {
        when(iRecycleOrderService.getById(15L)).thenReturn(ownedOrder(15L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        PickupTask task = new PickupTask();
        task.setStaffId(1L);
        when(iPickupTaskService.getOne(any())).thenReturn(task);
        when(iUserService.getById(1L)).thenReturn(null);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(15L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("[]");
            assertThrows(BizException.class, () -> recycleOrderService.getCourierInfo(200L, infoBo));
        }
    }

    @Test
    void getCourierInfoShouldIgnoreStaffWithoutNameOrMobile() {
        when(iRecycleOrderService.getById(16L)).thenReturn(ownedOrder(16L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        PickupTask task = new PickupTask();
        task.setStaffId(2L);
        when(iPickupTaskService.getOne(any())).thenReturn(task);
        UserEntity staff = new UserEntity();
        staff.setUserName(" ");
        staff.setPhone("13900001111");
        when(iUserService.getById(2L)).thenReturn(staff);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(16L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            assertThrows(BizException.class, () -> recycleOrderService.getCourierInfo(200L, infoBo));
        }
    }

    @Test
    void getCourierInfoShouldIgnoreBlankCourierPoolConfig() {
        when(iRecycleOrderService.getById(17L)).thenReturn(ownedOrder(17L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        when(iPickupTaskService.getOne(any())).thenReturn(null);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(17L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("   ");
            assertThrows(BizException.class, () -> recycleOrderService.getCourierInfo(200L, infoBo));
        }
    }

    @Test
    void getCourierInfoShouldIgnoreNullPoolItem() {
        when(iRecycleOrderService.getById(18L)).thenReturn(ownedOrder(18L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        when(iPickupTaskService.getOne(any())).thenReturn(null);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(18L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("order_courier_pool")))
                    .thenReturn("[null]");
            assertThrows(BizException.class, () -> recycleOrderService.getCourierInfo(200L, infoBo));
        }
    }

    @Test
    void getCourierInfoShouldIgnorePoolItemMissingFields() {
        when(iRecycleOrderService.getById(19L)).thenReturn(ownedOrder(19L, RecycleOrderStatus.IN_PROGRESS.getCode()));
        when(iPickupTaskService.getOne(any())).thenReturn(null);

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(19L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("order_courier_pool")))
                    .thenReturn("[{\"name\":\"只名没手机\"}]");
            assertThrows(BizException.class, () -> recycleOrderService.getCourierInfo(200L, infoBo));
        }
    }

    @Test
    void createShouldRejectMissingAddress() {
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        ClothesOrderSaveBo saveBo = new ClothesOrderSaveBo();
        saveBo.setAddressId(999L);
        saveBo.setAppointmentTime(LocalDateTime.now().plusDays(1));
        assertThrows(BizException.class, () -> recycleOrderService.createClothesOrder(200L, saveBo));
    }

    @Test
    void infoShouldRejectForeignOrder() {
        RecycleOrder order = ownedOrder(1L, RecycleOrderStatus.WAIT_ASSIGN.getCode());
        order.setUserId(999L);
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(1L);
        assertThrows(BizException.class, () -> recycleOrderService.info(200L, infoBo));
    }

    @Test
    void listExpressTracksShouldValidateAndQueryByExpressNo() {
        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(21L);

        RecycleOrder visitOrder = ownedOrder(21L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(21L)).thenReturn(visitOrder);
        assertThrows(BizException.class, () -> recycleOrderService.listExpressTracks(200L, infoBo));

        RecycleOrder mailNoExpress = ownedOrder(21L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailNoExpress.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailNoExpress.setExpressNo("");
        when(iRecycleOrderService.getById(21L)).thenReturn(mailNoExpress);
        assertThrows(BizException.class, () -> recycleOrderService.listExpressTracks(200L, infoBo));

        RecycleOrder mailOrder = ownedOrder(21L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        mailOrder.setContactMobile("15200000002");
        when(iRecycleOrderService.getById(21L)).thenReturn(mailOrder);
        when(aliyunLogisticsService.listExpressTracks(eq("SF0217121758386"), any(), any()))
                .thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("4008006688");
            assertTrue(recycleOrderService.listExpressTracks(200L, infoBo).isEmpty());
            verify(aliyunLogisticsService).listExpressTracks(eq("SF0217121758386"), any(), any());
        }
    }

    @Test
    void infoShouldLoadExpressTracksForMailOrder() {
        RecycleOrder mailOrder = ownedOrder(24L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        mailOrder.setContactMobile("15200000002");
        when(iRecycleOrderService.getById(24L)).thenReturn(mailOrder);
        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(aliyunLogisticsService.listExpressTracks(eq("SF0217121758386"), any(), any()))
                .thenReturn(Collections.emptyList());

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(24L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("4008006688");
            RecycleOrderDetailVo detail = recycleOrderService.info(200L, infoBo);
            assertNotNull(detail.getExpressTracks());
            assertEquals("顺丰速运", detail.getExpressCompany());
            verify(aliyunLogisticsService).listExpressTracks(eq("SF0217121758386"), any(), any());
        }
    }

    @Test
    void infoShouldSwallowExpressTrackErrorsForMailOrder() {
        RecycleOrder mailOrder = ownedOrder(22L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setExpressNo("SF0217121758386");
        mailOrder.setContactMobile("15200000002");
        when(iRecycleOrderService.getById(22L)).thenReturn(mailOrder);
        when(iRecycleOrderLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(aliyunLogisticsService.listExpressTracks(eq("SF0217121758386"), any(), any()))
                .thenThrow(new BizException("凭证未配置"));

        RecycleOrderInfoBo infoBo = new RecycleOrderInfoBo();
        infoBo.setId(22L);
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn("4008006688");
            RecycleOrderDetailVo detail = recycleOrderService.info(200L, infoBo);
            assertNotNull(detail.getExpressTracks());
            assertTrue(detail.getExpressTracks().isEmpty());
        }
    }

    @Test
    void shipMailShouldValidateAndMarkWaitReceive() {
        ShipMailOrderBo shipBo = new ShipMailOrderBo();
        shipBo.setOrderId(30L);
        shipBo.setExpressNo(" SF1234567890 ");

        RecycleOrder visitOrder = ownedOrder(30L, RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(30L)).thenReturn(visitOrder);
        assertThrows(BizException.class, () -> recycleOrderService.shipMail(200L, shipBo));

        RecycleOrder mailOrder = ownedOrder(30L, RecycleOrderStatus.IN_PROGRESS.getCode());
        mailOrder.setFulfillType(RecycleFulfillType.MAIL.getCode());
        mailOrder.setStatusDesc("待顺丰寄件取件");
        when(iRecycleOrderService.getById(30L)).thenReturn(mailOrder);
        recycleOrderService.shipMail(200L, shipBo);

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals("SF1234567890", orderCaptor.getValue().getExpressNo());
        assertEquals("待收货", orderCaptor.getValue().getStatusDesc());
        verify(iRecycleOrderLogService).save(argThat(log ->
                "MAIL_SHIP".equals(log.getActionType()) && log.getContent().contains("SF1234567890")));

        mailOrder.setStatusDesc("待收货");
        assertThrows(BizException.class, () -> recycleOrderService.shipMail(200L, shipBo));
    }

    private void stubAddress() {
        when(iUserAddressService.getOne(any(LambdaQueryWrapper.class))).thenReturn(buildAddress());
    }

    private void stubSaveOrder(Long id) {
        doAnswer(invocation -> {
            RecycleOrder order = invocation.getArgument(0);
            order.setId(id);
            return true;
        }).when(iRecycleOrderService).save(any(RecycleOrder.class));
    }

    private RecycleOrder ownedOrder(Long id, String status) {
        RecycleOrder order = new RecycleOrder();
        order.setId(id);
        order.setUserId(200L);
        order.setStatus(status);
        order.setFulfillType(RecycleFulfillType.VISIT.getCode());
        order.setAddressText("上海市 浦东新区");
        return order;
    }

    private ProductModel buildModel(Long id, String name, BigDecimal price) {
        ProductModel model = new ProductModel();
        model.setId(id);
        model.setName(name);
        model.setBasePrice(price);
        return model;
    }

    private UserAddress buildAddress() {
        UserAddress address = new UserAddress();
        address.setId(1L);
        address.setUserId(200L);
        address.setContactName("张伟");
        address.setContactMobile("13812348888");
        address.setProvince("上海市");
        address.setCity("上海市");
        address.setDistrict("浦东新区");
        address.setStreet("测试街道");
        address.setDetailAddress("测试路1号");
        return address;
    }
}
