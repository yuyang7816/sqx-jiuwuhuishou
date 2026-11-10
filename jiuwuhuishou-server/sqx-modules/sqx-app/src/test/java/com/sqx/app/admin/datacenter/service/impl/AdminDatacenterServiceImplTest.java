package com.sqx.app.admin.datacenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqx.app.admin.datacenter.domain.bo.AdminDatacenterOverviewBo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterBreakdownVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterGrowthVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterOverviewVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterTrendVo;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.brand.db.service.IBrandCooperationService;
import com.sqx.app.brand.domain.entity.BrandCooperation;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderException;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.db.service.IOrderPayoutService;
import com.sqx.app.payout.domain.entity.OrderPayout;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
import com.sqx.app.referral.withdraw.db.service.IWithdrawRecordService;
import com.sqx.app.referral.withdraw.domain.entity.WithdrawRecord;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.entity.OrderReview;
import com.sqx.app.servicechat.db.service.ICustomerServiceSessionService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceSession;
import com.sqx.app.settlement.db.service.IOrderDisputeService;
import com.sqx.app.settlement.domain.entity.OrderDispute;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.welfare.db.service.IUserSignRecordService;
import com.sqx.app.welfare.db.service.IUserWelfareService;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.entity.UserWelfare;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminDatacenterServiceImplTest {

    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IOrderSettlementService iOrderSettlementService;
    @Mock
    private IOrderPayoutService iOrderPayoutService;
    @Mock
    private IUserService iUserService;
    @Mock
    private IPickupTaskService iPickupTaskService;
    @Mock
    private IOrderExceptionService iOrderExceptionService;
    @Mock
    private IOrderDisputeService iOrderDisputeService;
    @Mock
    private IOrderReviewService iOrderReviewService;
    @Mock
    private IWithdrawRecordService iWithdrawRecordService;
    @Mock
    private IReferralCommissionService iReferralCommissionService;
    @Mock
    private IUserSignRecordService iUserSignRecordService;
    @Mock
    private IUserWelfareService iUserWelfareService;
    @Mock
    private ICustomerServiceSessionService iCustomerServiceSessionService;
    @Mock
    private IBrandCooperationService iBrandCooperationService;
    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;

    @InjectMocks
    private AdminDatacenterServiceImpl service;

    private static final LocalDateTime START = LocalDateTime.of(2026, 7, 1, 0, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 7, 3, 23, 59, 59);

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(
                RecycleOrder.class,
                OrderSettlement.class,
                OrderPayout.class,
                UserEntity.class,
                PickupTask.class,
                OrderException.class,
                OrderDispute.class,
                OrderReview.class,
                WithdrawRecord.class,
                ReferralCommission.class,
                UserSignRecord.class,
                UserWelfare.class,
                CustomerServiceSession.class,
                BrandCooperation.class,
                UserWallet.class,
                UserWalletTransaction.class
        );
    }

    @BeforeEach
    void stubDefaults() {
        lenient().when(iRecycleOrderService.count(any(LambdaQueryWrapper.class))).thenReturn(6L);
        lenient().when(iRecycleOrderService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", new BigDecimal("120.50")));
        lenient().when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(defaultOrderMaps());

        lenient().when(iOrderSettlementService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", "15.00"));
        lenient().when(iOrderPayoutService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        lenient().when(iUserService.count(any(LambdaQueryWrapper.class))).thenReturn(4L);

        lenient().when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(3L);
        lenient().when(iPickupTaskService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("avgMinutes", 90));

        lenient().when(iOrderExceptionService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
        lenient().when(iOrderDisputeService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        lenient().when(iOrderReviewService.count(any(LambdaQueryWrapper.class))).thenReturn(5L);
        lenient().when(iOrderReviewService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("avgScore", 4.5));

        lenient().when(iWithdrawRecordService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        lenient().when(iWithdrawRecordService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", 200));
        lenient().when(iWithdrawRecordService.listMaps(any(QueryWrapper.class)))
                .thenReturn(statusRows("status", "cnt", 1, 2L));

        lenient().when(iReferralCommissionService.count(any(LambdaQueryWrapper.class))).thenReturn(3L);
        lenient().when(iReferralCommissionService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", "88.88"));

        lenient().when(iUserSignRecordService.count(any(LambdaQueryWrapper.class))).thenReturn(7L);
        lenient().when(iUserSignRecordService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", 10L));

        lenient().when(iUserWelfareService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", "500"));

        lenient().when(iCustomerServiceSessionService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
        lenient().when(iBrandCooperationService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        lenient().when(iBrandCooperationService.listMaps(any(QueryWrapper.class)))
                .thenReturn(statusRows("status", "cnt", 0, 1L));

        lenient().when(iUserWalletService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", null));
        lenient().when(iUserWalletTransactionService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", 30));
    }

    @Test
    void overviewShouldAggregateCurrentAndPreviousPeriods() {
        AdminDatacenterOverviewBo bo = buildBo(null);

        AdminDatacenterOverviewVo vo = service.overview(bo);

        assertEquals(START, vo.getStartTime());
        assertEquals(END, vo.getEndTime());
        assertNotNull(vo.getPrevStartTime());
        assertNotNull(vo.getPrevEndTime());
        assertEquals(6L, vo.getOrderCount());
        assertEquals(new BigDecimal("120.50"), vo.getGmv());
        assertEquals(6L, vo.getCompletedCount());
        assertEquals(6L, vo.getCancelledCount());
        assertNotNull(vo.getCompletionRate());
        assertNotNull(vo.getCancelRate());
        assertEquals(new BigDecimal("0.00"), vo.getAmountDiff());
        assertEquals(4L, vo.getNewUserCount());
        // orderUserCount 走 getMap COUNT DISTINCT，与默认 stub 的 total=120.50 一致
        assertEquals(120L, vo.getOrderUserCount());
        assertEquals(2L, vo.getPayoutSuccessCount());
        assertEquals(2L, vo.getPayoutFailCount());
        assertNotNull(vo.getPayoutSuccessRate());
        assertFalse(vo.getFunnel().isEmpty());
        assertEquals(6L, vo.getBacklogCount());
        assertEquals(new BigDecimal("15.00"), vo.getPendingSettlementAmount());
        assertEquals(new BigDecimal("120.50"), vo.getPendingPayoutAmount());

        assertNotNull(vo.getCompare());
        assertEquals(6L, vo.getCompare().getOrderCount());
        assertEquals(BigDecimal.ZERO.setScale(4), vo.getCompare().getOrderCountChangeRate());
        assertEquals(new BigDecimal("120.50"), vo.getCompare().getGmv());
        assertEquals(BigDecimal.ZERO.setScale(4), vo.getCompare().getGmvChangeRate());
        assertEquals(4L, vo.getCompare().getNewUserCount());
        assertEquals(BigDecimal.ZERO.setScale(4), vo.getCompare().getNewUserCountChangeRate());
    }

    @Test
    void overviewShouldUseDefaultsWhenBoNull() {
        AdminDatacenterOverviewVo vo = service.overview(null);

        assertNotNull(vo.getStartTime());
        assertNotNull(vo.getEndTime());
        assertTrue(vo.getEndTime().isAfter(vo.getStartTime()) || vo.getEndTime().isEqual(vo.getStartTime()));
    }

    @Test
    void overviewShouldRejectInvalidRange() {
        AdminDatacenterOverviewBo bo = new AdminDatacenterOverviewBo();
        bo.setStartTime(LocalDateTime.of(2026, 7, 10, 0, 0));
        bo.setEndTime(LocalDateTime.of(2026, 7, 1, 0, 0));

        BizException ex = assertThrows(BizException.class, () -> service.overview(bo));
        assertEquals("开始时间不能晚于结束时间", ex.getMessage());
    }

    @Test
    void overviewShouldHandleNullAggregateMaps() {
        when(iRecycleOrderService.getMap(any(QueryWrapper.class))).thenReturn(null);
        when(iOrderSettlementService.getMap(any(QueryWrapper.class))).thenReturn(null);

        AdminDatacenterOverviewVo vo = service.overview(buildBo(null));

        assertEquals(BigDecimal.ZERO.setScale(2), vo.getGmv());
        assertEquals(BigDecimal.ZERO.setScale(2), vo.getPendingSettlementAmount());
        assertEquals(BigDecimal.ZERO.setScale(2), vo.getPendingPayoutAmount());
        assertEquals(0L, vo.getOrderUserCount());
    }

    @Test
    void trendShouldTreatNullOrderRowsAsEmpty() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(null)
                .thenReturn(Collections.emptyList());

        AdminDatacenterTrendVo vo = service.trend(buildBo(null));

        assertEquals(0L, vo.getPoints().get(0).getOrderCount());
    }

    @Test
    void overviewShouldReturnNullChangeRatesWhenPreviousZero() {
        when(iRecycleOrderService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iRecycleOrderService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", BigDecimal.ZERO));
        when(iOrderPayoutService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);

        AdminDatacenterOverviewVo vo = service.overview(buildBo("  book  "));

        assertEquals(BigDecimal.ZERO.setScale(4), vo.getCompletionRate());
        assertEquals(BigDecimal.ZERO.setScale(4), vo.getCancelRate());
        assertEquals(BigDecimal.ZERO.setScale(4), vo.getPayoutSuccessRate());
        assertNull(vo.getCompare().getOrderCountChangeRate());
        assertNull(vo.getCompare().getGmvChangeRate());
        assertNull(vo.getCompare().getNewUserCountChangeRate());
    }

    @Test
    void trendShouldBuildDailyPointsWithMapsAndNullRows() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(trendOrderRows())
                .thenReturn(gmvRowsWithNullKey());

        AdminDatacenterTrendVo vo = service.trend(buildBo("clothes"));

        assertEquals(START, vo.getStartTime());
        assertEquals(END, vo.getEndTime());
        assertEquals(3, vo.getPoints().size());
        assertEquals("2026-07-01", vo.getPoints().get(0).getDate());
        assertEquals(2L, vo.getPoints().get(0).getOrderCount());
        assertEquals(new BigDecimal("50.00"), vo.getPoints().get(1).getGmv());
        assertEquals(0L, vo.getPoints().get(2).getOrderCount());
        assertEquals(BigDecimal.ZERO.setScale(2), vo.getPoints().get(2).getGmv());
    }

    @Test
    void trendShouldTreatNullGmvRowsAsEmpty() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList())
                .thenReturn(null);

        AdminDatacenterTrendVo vo = service.trend(buildBo(null));

        assertEquals(3, vo.getPoints().size());
        assertEquals(BigDecimal.ZERO.setScale(2), vo.getPoints().get(0).getGmv());
    }

    @Test
    void breakdownShouldBuildCategoriesPickupAndQualityWithCategoryFilter() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(categoryCountRows())
                .thenReturn(categoryGmvRows());

        AdminDatacenterBreakdownVo vo = service.breakdown(buildBo("book"));

        assertEquals(START, vo.getStartTime());
        assertEquals(1, vo.getCategories().size());
        assertEquals("book", vo.getCategories().get(0).getOrderType());
        assertEquals("图书", vo.getCategories().get(0).getOrderTypeLabel());
        assertEquals(5L, vo.getCategories().get(0).getOrderCount());
        assertEquals(new BigDecimal("80.00"), vo.getCategories().get(0).getGmv());

        assertEquals(3L, vo.getPickup().getPendingCount());
        assertEquals(new BigDecimal("1.50"), vo.getPickup().getAvgFinishHours());

        assertEquals(1L, vo.getQuality().getExceptionCreatedCount());
        assertEquals(2L, vo.getQuality().getDisputeCreatedCount());
        assertEquals(new BigDecimal("4.50"), vo.getQuality().getReviewAvgScore());
    }

    @Test
    void breakdownShouldUseZeroAvgFinishHoursWhenAvgNotPositive() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(categoryCountRows())
                .thenReturn(categoryGmvRows());
        when(iPickupTaskService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("avgMinutes", 0));

        AdminDatacenterBreakdownVo vo = service.breakdown(buildBo(null));

        assertEquals(BigDecimal.ZERO.setScale(2), vo.getPickup().getAvgFinishHours());
    }

    @Test
    void breakdownShouldListAllCategoriesWhenNoFilter() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList())
                .thenReturn(null);

        AdminDatacenterBreakdownVo vo = service.breakdown(buildBo(null));

        assertEquals(5, vo.getCategories().size());
        assertEquals("clothes", vo.getCategories().get(0).getOrderType());
        assertEquals(0L, vo.getCategories().get(0).getOrderCount());
        assertEquals(BigDecimal.ZERO.setScale(2), vo.getCategories().get(0).getGmv());
    }

    @Test
    void growthShouldAggregateAllSections() {
        when(iWithdrawRecordService.listMaps(any(QueryWrapper.class))).thenReturn(null);
        when(iBrandCooperationService.listMaps(any(QueryWrapper.class))).thenReturn(null);
        when(iUserSignRecordService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", "12"))
                .thenReturn(singleValueMap("total", 99));
        when(iUserWalletTransactionService.getMap(any(QueryWrapper.class)))
                .thenReturn(singleValueMap("total", "40.00"))
                .thenReturn(singleValueMap("total", new BigDecimal("10.00")));

        AdminDatacenterGrowthVo vo = service.growth(buildBo(null));

        assertEquals(START, vo.getStartTime());
        assertNotNull(vo.getWithdraw());
        assertEquals(3, vo.getWithdraw().getFunnel().size());
        assertNotNull(vo.getReferral());
        assertEquals(new BigDecimal("88.88"), vo.getReferral().getCommissionAmount());
        assertNotNull(vo.getWelfare());
        assertEquals(12L, vo.getWelfare().getSignUv());
        assertEquals(99L, vo.getWelfare().getPeriodCoinRewardSum());
        assertNotNull(vo.getServiceChat());
        assertNotNull(vo.getBrand());
        assertEquals(4, vo.getBrand().getFunnel().size());
        assertNotNull(vo.getWallet());
        assertEquals(new BigDecimal("40.00"), vo.getWallet().getInflowAmount());
        assertEquals(new BigDecimal("10.00"), vo.getWallet().getOutflowAmount());
        assertEquals(new BigDecimal("30.00"), vo.getWallet().getNetInflowAmount());
    }

    @Test
    void growthShouldHandleEmptyMapsAndTypeCoercion() {
        when(iUserWalletService.getMap(any(QueryWrapper.class))).thenReturn(Collections.emptyMap());
        when(iUserSignRecordService.getMap(any(QueryWrapper.class)))
                .thenReturn(null)
                .thenReturn(singleValueMap("total", null));

        AdminDatacenterGrowthVo vo = service.growth(buildBo(null));

        assertEquals(BigDecimal.ZERO.setScale(2), vo.getWallet().getBalanceSum());
        assertEquals(0L, vo.getWelfare().getSignUv());
        assertEquals(0L, vo.getWelfare().getPeriodCoinRewardSum());
    }

    @Test
    void breakdownShouldUseFallbackLabelForUnknownCategory() {
        when(iRecycleOrderService.listMaps(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(row("order_type", "unknown", "cnt", null)))
                .thenReturn(Collections.emptyList());

        AdminDatacenterBreakdownVo vo = service.breakdown(buildBo("unknown"));

        assertEquals(1, vo.getCategories().size());
        assertEquals("unknown", vo.getCategories().get(0).getOrderTypeLabel());
        assertEquals(0L, vo.getCategories().get(0).getOrderCount());
    }

    private AdminDatacenterOverviewBo buildBo(String orderType) {
        AdminDatacenterOverviewBo bo = new AdminDatacenterOverviewBo();
        bo.setStartTime(START);
        bo.setEndTime(END);
        bo.setOrderType(orderType);
        return bo;
    }

    private Map<String, Object> singleValueMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private List<Map<String, Object>> defaultOrderMaps() {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RecycleOrderStatus status : RecycleOrderStatus.values()) {
            Map<String, Object> row = new HashMap<>();
            row.put("status", status.getCode());
            row.put("cnt", 1L);
            rows.add(row);
        }
        Map<String, Object> dayRow = new HashMap<>();
        dayRow.put("dayKey", "2026-07-02");
        dayRow.put("cnt", 1L);
        rows.add(dayRow);
        return rows;
    }

    private List<Map<String, Object>> trendOrderRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("dayKey", "2026-07-01", "cnt", 2L));
        rows.add(row("dayKey", null, "cnt", 9L));
        return rows;
    }

    private List<Map<String, Object>> gmvRowsWithNullKey() {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("dayKey", "2026-07-02", "total", "50.00"));
        rows.add(row("dayKey", null, "total", "99.00"));
        return rows;
    }

    private List<Map<String, Object>> categoryCountRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("order_type", "book", "cnt", 5L));
        rows.add(row("order_type", null, "cnt", 1L));
        return rows;
    }

    private List<Map<String, Object>> categoryGmvRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("order_type", "book", "total", new BigDecimal("80.00")));
        rows.add(row("order_type", null, "total", "10.00"));
        return rows;
    }

    private List<Map<String, Object>> statusRows(String statusField, String countField, int status, long count) {
        Map<String, Object> row = new HashMap<>();
        row.put(statusField, status);
        row.put(countField, count);
        return Collections.singletonList(row);
    }

    private Map<String, Object> row(Object... kv) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
