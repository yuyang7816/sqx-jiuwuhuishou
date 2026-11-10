package com.sqx.app.admin.datacenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqx.app.admin.datacenter.domain.bo.AdminDatacenterOverviewBo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterBrandVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterBreakdownVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterCategoryVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterCompareVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterGrowthVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterOverviewVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterPickupVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterQualityVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterReferralVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterServiceChatVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterStatusCountVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterTrendPointVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterTrendVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterWalletVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterWelfareVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterWithdrawVo;
import com.sqx.app.admin.datacenter.service.AdminDatacenterService;
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
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.referral.commission.db.service.IReferralCommissionService;
import com.sqx.app.referral.commission.domain.entity.ReferralCommission;
import com.sqx.app.referral.commission.enums.Direction;
import com.sqx.app.referral.commission.enums.ReferralCommissionType;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AdminDatacenterServiceImpl implements AdminDatacenterService {

    private static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    private static final String PAYOUT_STATUS_SUCCESS = "SUCCESS";
    private static final String PAYOUT_STATUS_FAILED = "FAILED";
    private static final String DISPUTE_STATUS_PENDING = "PENDING";
    private static final String DISPUTE_STATUS_RESOLVED = "RESOLVED";
    private static final int WITHDRAW_STATUS_PENDING = 1;
    private static final int WITHDRAW_STATUS_SUCCESS = 2;
    private static final int WITHDRAW_STATUS_REJECT = 3;
    private static final int SESSION_STATUS_OPEN = 1;
    private static final int SESSION_STATUS_CLOSED = 2;
    private static final int WALLET_DIRECTION_IN = 1;
    private static final int WALLET_DIRECTION_OUT = 2;
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final List<String> BACKLOG_STATUSES = Arrays.asList(
            RecycleOrderStatus.WAIT_AUDIT.getCode(),
            RecycleOrderStatus.WAIT_ASSIGN.getCode(),
            RecycleOrderStatus.EXCEPTION_PENDING.getCode(),
            RecycleOrderStatus.DISPUTE_PENDING.getCode()
    );
    private static final List<String> PENDING_PAYOUT_ORDER_STATUSES = Arrays.asList(
            RecycleOrderStatus.WAIT_PAYOUT.getCode(),
            RecycleOrderStatus.PAYOUT_PROCESSING.getCode()
    );
    private static final List<String> ORDER_TYPES = Arrays.asList(
            "clothes", "book", "appliance", "digital", "luxury"
    );
    private static final Map<String, String> ORDER_TYPE_LABELS = new LinkedHashMap<>();
    private static final Map<Integer, String> WITHDRAW_STATUS_LABELS = new LinkedHashMap<>();
    private static final Map<Integer, String> BRAND_STATUS_LABELS = new LinkedHashMap<>();

    static {
        ORDER_TYPE_LABELS.put("clothes", "衣物");
        ORDER_TYPE_LABELS.put("book", "图书");
        ORDER_TYPE_LABELS.put("appliance", "家电");
        ORDER_TYPE_LABELS.put("digital", "数码");
        ORDER_TYPE_LABELS.put("luxury", "奢侈品");

        WITHDRAW_STATUS_LABELS.put(WITHDRAW_STATUS_PENDING, "待审");
        WITHDRAW_STATUS_LABELS.put(WITHDRAW_STATUS_SUCCESS, "成功");
        WITHDRAW_STATUS_LABELS.put(WITHDRAW_STATUS_REJECT, "拒绝");

        BRAND_STATUS_LABELS.put(0, "待跟进");
        BRAND_STATUS_LABELS.put(1, "已联系");
        BRAND_STATUS_LABELS.put(2, "已合作");
        BRAND_STATUS_LABELS.put(3, "已拒绝");
    }

    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IOrderSettlementService iOrderSettlementService;
    @Resource
    private IOrderPayoutService iOrderPayoutService;
    @Resource
    private IUserService iUserService;
    @Resource
    private IPickupTaskService iPickupTaskService;
    @Resource
    private IOrderExceptionService iOrderExceptionService;
    @Resource
    private IOrderDisputeService iOrderDisputeService;
    @Resource
    private IOrderReviewService iOrderReviewService;
    @Resource
    private IWithdrawRecordService iWithdrawRecordService;
    @Resource
    private IReferralCommissionService iReferralCommissionService;
    @Resource
    private IUserSignRecordService iUserSignRecordService;
    @Resource
    private IUserWelfareService iUserWelfareService;
    @Resource
    private ICustomerServiceSessionService iCustomerServiceSessionService;
    @Resource
    private IBrandCooperationService iBrandCooperationService;
    @Resource
    private IUserWalletService iUserWalletService;
    @Resource
    private IUserWalletTransactionService iUserWalletTransactionService;

    @Override
    public AdminDatacenterOverviewVo overview(AdminDatacenterOverviewBo bo) {
        QueryRange range = resolveRange(bo);

        Duration window = Duration.between(range.startTime, range.endTime);
        LocalDateTime prevEndTime = range.startTime;
        LocalDateTime prevStartTime = range.startTime.minus(window);

        PeriodMetrics current = loadPeriodMetrics(range.startTime, range.endTime, range.orderType);
        PeriodMetrics previous = loadPeriodMetrics(prevStartTime, prevEndTime, range.orderType);

        AdminDatacenterOverviewVo vo = new AdminDatacenterOverviewVo();
        vo.setStartTime(range.startTime);
        vo.setEndTime(range.endTime);
        vo.setPrevStartTime(prevStartTime);
        vo.setPrevEndTime(prevEndTime);

        vo.setOrderCount(current.orderCount);
        vo.setGmv(current.gmv);
        vo.setCompletedCount(current.completedCount);
        vo.setCompletionRate(current.completionRate);
        vo.setCancelledCount(current.cancelledCount);
        vo.setCancelRate(current.cancelRate);
        vo.setEstimateAmountSum(current.estimateAmountSum);
        vo.setActualAmountSum(current.actualAmountSum);
        vo.setAmountDiff(current.amountDiff);
        vo.setNewUserCount(current.newUserCount);
        vo.setOrderUserCount(current.orderUserCount);
        vo.setPayoutSuccessCount(current.payoutSuccessCount);
        vo.setPayoutFailCount(current.payoutFailCount);
        vo.setPayoutSuccessRate(current.payoutSuccessRate);
        vo.setFunnel(current.funnel);

        vo.setBacklogCount(countBacklog(range.orderType));
        vo.setPendingSettlementAmount(sumPendingSettlementAmount());
        vo.setPendingPayoutAmount(sumPendingPayoutAmount(range.orderType));
        vo.setCompare(buildCompare(current, previous));
        return vo;
    }

    @Override
    public AdminDatacenterTrendVo trend(AdminDatacenterOverviewBo bo) {
        QueryRange range = resolveRange(bo);
        Map<String, Long> orderByDay = groupOrderCountByDay(range.startTime, range.endTime, range.orderType);
        Map<String, BigDecimal> gmvByDay = groupGmvByDay(range.startTime, range.endTime, range.orderType);

        List<AdminDatacenterTrendPointVo> points = new ArrayList<>();
        LocalDate cursor = range.startTime.toLocalDate();
        LocalDate last = range.endTime.toLocalDate();
        while (!cursor.isAfter(last)) {
            String day = cursor.format(DAY_FMT);
            AdminDatacenterTrendPointVo point = new AdminDatacenterTrendPointVo();
            point.setDate(day);
            point.setOrderCount(orderByDay.getOrDefault(day, 0L));
            point.setGmv(gmvByDay.getOrDefault(day, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)));
            points.add(point);
            cursor = cursor.plusDays(1);
        }

        AdminDatacenterTrendVo vo = new AdminDatacenterTrendVo();
        vo.setStartTime(range.startTime);
        vo.setEndTime(range.endTime);
        vo.setPoints(points);
        return vo;
    }

    @Override
    public AdminDatacenterBreakdownVo breakdown(AdminDatacenterOverviewBo bo) {
        QueryRange range = resolveRange(bo);
        AdminDatacenterBreakdownVo vo = new AdminDatacenterBreakdownVo();
        vo.setStartTime(range.startTime);
        vo.setEndTime(range.endTime);
        vo.setCategories(buildCategories(range.startTime, range.endTime, range.orderType));
        vo.setPickup(buildPickup(range.startTime, range.endTime));
        vo.setQuality(buildQuality(range.startTime, range.endTime));
        return vo;
    }

    @Override
    public AdminDatacenterGrowthVo growth(AdminDatacenterOverviewBo bo) {
        QueryRange range = resolveRange(bo);
        AdminDatacenterGrowthVo vo = new AdminDatacenterGrowthVo();
        vo.setStartTime(range.startTime);
        vo.setEndTime(range.endTime);
        vo.setWithdraw(buildWithdraw(range.startTime, range.endTime));
        vo.setReferral(buildReferral(range.startTime, range.endTime));
        vo.setWelfare(buildWelfare(range.startTime, range.endTime));
        vo.setServiceChat(buildServiceChat(range.startTime, range.endTime));
        vo.setBrand(buildBrand(range.startTime, range.endTime));
        vo.setWallet(buildWallet(range.startTime, range.endTime));
        return vo;
    }

    private QueryRange resolveRange(AdminDatacenterOverviewBo bo) {
        AdminDatacenterOverviewBo safe = bo == null ? new AdminDatacenterOverviewBo() : bo;
        LocalDateTime endTime = Objects.nonNull(safe.getEndTime()) ? safe.getEndTime() : LocalDateTime.now();
        LocalDateTime startTime = Objects.nonNull(safe.getStartTime()) ? safe.getStartTime() : endTime.minusDays(7);
        if (startTime.isAfter(endTime)) {
            throw new BizException("开始时间不能晚于结束时间");
        }
        QueryRange range = new QueryRange();
        range.startTime = startTime;
        range.endTime = endTime;
        range.orderType = StringUtils.trimToNull(safe.getOrderType());
        return range;
    }

    private PeriodMetrics loadPeriodMetrics(LocalDateTime start, LocalDateTime end, String orderType) {
        PeriodMetrics metrics = new PeriodMetrics();
        metrics.orderCount = countOrdersCreated(start, end, orderType, null);
        metrics.completedCount = countOrdersCreated(start, end, orderType, RecycleOrderStatus.COMPLETED.getCode());
        metrics.cancelledCount = countOrdersCreated(start, end, orderType, RecycleOrderStatus.CANCELLED.getCode());
        metrics.completionRate = rate(metrics.completedCount, metrics.orderCount);
        metrics.cancelRate = rate(metrics.cancelledCount, metrics.orderCount);
        metrics.gmv = sumCompletedGmv(start, end, orderType);
        metrics.estimateAmountSum = sumOrderAmount(start, end, orderType, "estimate_amount");
        metrics.actualAmountSum = sumOrderAmount(start, end, orderType, "actual_amount");
        metrics.amountDiff = metrics.actualAmountSum.subtract(metrics.estimateAmountSum);
        metrics.newUserCount = countNewUsers(start, end);
        metrics.orderUserCount = countDistinctOrderUsers(start, end, orderType);
        metrics.payoutSuccessCount = countPayouts(start, end, PAYOUT_STATUS_SUCCESS);
        metrics.payoutFailCount = countPayouts(start, end, PAYOUT_STATUS_FAILED);
        metrics.payoutSuccessRate = rate(metrics.payoutSuccessCount, metrics.payoutSuccessCount + metrics.payoutFailCount);
        metrics.funnel = buildFunnel(start, end, orderType);
        return metrics;
    }

    private Map<String, Long> groupOrderCountByDay(LocalDateTime start, LocalDateTime end, String orderType) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(create_time,'%Y-%m-%d') AS dayKey", "COUNT(*) AS cnt")
                .ge("create_time", start)
                .le("create_time", end)
                .groupBy("DATE_FORMAT(create_time,'%Y-%m-%d')");
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        return toLongMap(iRecycleOrderService.listMaps(wrapper), "dayKey", "cnt");
    }

    private Map<String, BigDecimal> groupGmvByDay(LocalDateTime start, LocalDateTime end, String orderType) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(update_time,'%Y-%m-%d') AS dayKey", "IFNULL(SUM(actual_amount),0) AS total")
                .eq("status", RecycleOrderStatus.COMPLETED.getCode())
                .ge("update_time", start)
                .le("update_time", end)
                .groupBy("DATE_FORMAT(update_time,'%Y-%m-%d')");
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        Map<String, BigDecimal> result = new HashMap<>();
        List<Map<String, Object>> rows = iRecycleOrderService.listMaps(wrapper);
        if (rows == null) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Object key = row.get("dayKey");
            if (key == null) {
                continue;
            }
            result.put(String.valueOf(key), toDecimal(row.get("total")));
        }
        return result;
    }

    private List<AdminDatacenterCategoryVo> buildCategories(LocalDateTime start, LocalDateTime end, String orderTypeFilter) {
        Map<String, Long> countByType = new HashMap<>();
        QueryWrapper<RecycleOrder> countWrapper = new QueryWrapper<>();
        countWrapper.select("order_type", "COUNT(*) AS cnt")
                .ge("create_time", start)
                .le("create_time", end)
                .groupBy("order_type");
        if (StringUtils.isNotBlank(orderTypeFilter)) {
            countWrapper.eq("order_type", orderTypeFilter);
        }
        List<Map<String, Object>> countRows = iRecycleOrderService.listMaps(countWrapper);
        if (countRows != null) {
            for (Map<String, Object> row : countRows) {
                Object type = row.get("order_type");
                if (type == null) {
                    continue;
                }
                countByType.put(String.valueOf(type), toLong(row.get("cnt")));
            }
        }

        Map<String, BigDecimal> gmvByType = new HashMap<>();
        QueryWrapper<RecycleOrder> gmvWrapper = new QueryWrapper<>();
        gmvWrapper.select("order_type", "IFNULL(SUM(actual_amount),0) AS total")
                .eq("status", RecycleOrderStatus.COMPLETED.getCode())
                .ge("update_time", start)
                .le("update_time", end)
                .groupBy("order_type");
        if (StringUtils.isNotBlank(orderTypeFilter)) {
            gmvWrapper.eq("order_type", orderTypeFilter);
        }
        List<Map<String, Object>> gmvRows = iRecycleOrderService.listMaps(gmvWrapper);
        if (gmvRows != null) {
            for (Map<String, Object> row : gmvRows) {
                Object type = row.get("order_type");
                if (type == null) {
                    continue;
                }
                gmvByType.put(String.valueOf(type), toDecimal(row.get("total")));
            }
        }

        List<String> types = StringUtils.isNotBlank(orderTypeFilter)
                ? Arrays.asList(orderTypeFilter)
                : ORDER_TYPES;
        List<AdminDatacenterCategoryVo> list = new ArrayList<>();
        for (String type : types) {
            AdminDatacenterCategoryVo item = new AdminDatacenterCategoryVo();
            item.setOrderType(type);
            item.setOrderTypeLabel(ORDER_TYPE_LABELS.getOrDefault(type, type));
            item.setOrderCount(countByType.getOrDefault(type, 0L));
            item.setGmv(gmvByType.getOrDefault(type, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)));
            list.add(item);
        }
        return list;
    }

    private AdminDatacenterPickupVo buildPickup(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterPickupVo vo = new AdminDatacenterPickupVo();
        vo.setPendingCount(countPickupByStatus(PickupTaskStatus.PENDING.name()));
        vo.setRunningCount(countPickupByStatus(PickupTaskStatus.RUNNING.name()));
        vo.setDoneCount(countPickupByStatus(PickupTaskStatus.DONE.name()));
        vo.setOverdueCount(iPickupTaskService.count(new LambdaQueryWrapper<PickupTask>()
                .in(PickupTask::getStatus, PickupTaskStatus.PENDING.name(), PickupTaskStatus.RUNNING.name())
                .lt(PickupTask::getAppointmentTime, LocalDateTime.now())));
        vo.setPeriodDoneCount(iPickupTaskService.count(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getStatus, PickupTaskStatus.DONE.name())
                .ge(PickupTask::getFinishedAt, start)
                .le(PickupTask::getFinishedAt, end)));

        QueryWrapper<PickupTask> avgWrapper = new QueryWrapper<>();
        avgWrapper.select("AVG(TIMESTAMPDIFF(MINUTE, create_time, finished_at)) AS avgMinutes")
                .eq("status", PickupTaskStatus.DONE.name())
                .isNotNull("finished_at")
                .ge("finished_at", start)
                .le("finished_at", end);
        BigDecimal avgMinutes = firstDecimal(iPickupTaskService.getMap(avgWrapper));
        if (avgMinutes.compareTo(BigDecimal.ZERO) <= 0) {
            vo.setAvgFinishHours(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        } else {
            vo.setAvgFinishHours(avgMinutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
        }
        return vo;
    }

    private long countPickupByStatus(String status) {
        return iPickupTaskService.count(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getStatus, status));
    }

    private AdminDatacenterQualityVo buildQuality(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterQualityVo vo = new AdminDatacenterQualityVo();
        vo.setExceptionCreatedCount(iOrderExceptionService.count(new LambdaQueryWrapper<OrderException>()
                .ge(OrderException::getCreateTime, start)
                .le(OrderException::getCreateTime, end)));
        vo.setExceptionOpenCount(iRecycleOrderService.count(new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getStatus, RecycleOrderStatus.EXCEPTION_PENDING.getCode())));

        long disputeCreated = iOrderDisputeService.count(new LambdaQueryWrapper<OrderDispute>()
                .ge(OrderDispute::getCreateTime, start)
                .le(OrderDispute::getCreateTime, end));
        long disputeResolved = iOrderDisputeService.count(new LambdaQueryWrapper<OrderDispute>()
                .ge(OrderDispute::getCreateTime, start)
                .le(OrderDispute::getCreateTime, end)
                .eq(OrderDispute::getStatus, DISPUTE_STATUS_RESOLVED));
        vo.setDisputeCreatedCount(disputeCreated);
        vo.setDisputeResolvedCount(disputeResolved);
        vo.setDisputePendingCount(iOrderDisputeService.count(new LambdaQueryWrapper<OrderDispute>()
                .eq(OrderDispute::getStatus, DISPUTE_STATUS_PENDING)));
        vo.setDisputeResolveRate(rate(disputeResolved, disputeCreated));

        long reviewCount = iOrderReviewService.count(new LambdaQueryWrapper<OrderReview>()
                .ge(OrderReview::getCreateTime, start)
                .le(OrderReview::getCreateTime, end));
        vo.setReviewCount(reviewCount);

        QueryWrapper<OrderReview> avgScoreWrapper = new QueryWrapper<>();
        avgScoreWrapper.select("IFNULL(AVG(score),0) AS avgScore")
                .ge("create_time", start)
                .le("create_time", end);
        vo.setReviewAvgScore(firstDecimal(iOrderReviewService.getMap(avgScoreWrapper)).setScale(2, RoundingMode.HALF_UP));

        long replied = iOrderReviewService.count(new LambdaQueryWrapper<OrderReview>()
                .ge(OrderReview::getCreateTime, start)
                .le(OrderReview::getCreateTime, end)
                .isNotNull(OrderReview::getAdminReply)
                .ne(OrderReview::getAdminReply, ""));
        vo.setReviewReplyRate(rate(replied, reviewCount));
        return vo;
    }

    private AdminDatacenterWithdrawVo buildWithdraw(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterWithdrawVo vo = new AdminDatacenterWithdrawVo();
        vo.setPendingCount(iWithdrawRecordService.count(new LambdaQueryWrapper<WithdrawRecord>()
                .eq(WithdrawRecord::getStatus, WITHDRAW_STATUS_PENDING)));

        QueryWrapper<WithdrawRecord> pendingAmountWrapper = new QueryWrapper<>();
        pendingAmountWrapper.select("IFNULL(SUM(amount),0) AS total")
                .eq("status", WITHDRAW_STATUS_PENDING);
        vo.setPendingAmount(firstDecimal(iWithdrawRecordService.getMap(pendingAmountWrapper)));

        vo.setPeriodCreatedCount(iWithdrawRecordService.count(new LambdaQueryWrapper<WithdrawRecord>()
                .ge(WithdrawRecord::getCreateTime, start)
                .le(WithdrawRecord::getCreateTime, end)));
        vo.setPeriodSuccessCount(countWithdrawCreated(start, end, WITHDRAW_STATUS_SUCCESS));
        vo.setPeriodRejectCount(countWithdrawCreated(start, end, WITHDRAW_STATUS_REJECT));

        QueryWrapper<WithdrawRecord> successAmountWrapper = new QueryWrapper<>();
        successAmountWrapper.select("IFNULL(SUM(IFNULL(actual_amount, amount)),0) AS total")
                .eq("status", WITHDRAW_STATUS_SUCCESS)
                .ge("create_time", start)
                .le("create_time", end);
        vo.setPeriodSuccessAmount(firstDecimal(iWithdrawRecordService.getMap(successAmountWrapper)));

        QueryWrapper<WithdrawRecord> funnelWrapper = new QueryWrapper<>();
        funnelWrapper.select("status", "COUNT(*) AS cnt")
                .ge("create_time", start)
                .le("create_time", end)
                .groupBy("status");
        Map<String, Long> countByStatus = toLongMap(iWithdrawRecordService.listMaps(funnelWrapper), "status", "cnt");
        List<AdminDatacenterStatusCountVo> funnel = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : WITHDRAW_STATUS_LABELS.entrySet()) {
            AdminDatacenterStatusCountVo item = new AdminDatacenterStatusCountVo();
            item.setStatus(String.valueOf(entry.getKey()));
            item.setStatusLabel(entry.getValue());
            item.setCount(countByStatus.getOrDefault(String.valueOf(entry.getKey()), 0L));
            funnel.add(item);
        }
        vo.setFunnel(funnel);
        return vo;
    }

    private long countWithdrawCreated(LocalDateTime start, LocalDateTime end, int status) {
        return iWithdrawRecordService.count(new LambdaQueryWrapper<WithdrawRecord>()
                .ge(WithdrawRecord::getCreateTime, start)
                .le(WithdrawRecord::getCreateTime, end)
                .eq(WithdrawRecord::getStatus, status));
    }

    private AdminDatacenterReferralVo buildReferral(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterReferralVo vo = new AdminDatacenterReferralVo();
        long commissionCount = iReferralCommissionService.count(new LambdaQueryWrapper<ReferralCommission>()
                .eq(ReferralCommission::getType, ReferralCommissionType.ORDER.getValue())
                .eq(ReferralCommission::getDirection, Direction.INCR.getValue())
                .ge(ReferralCommission::getCreateTime, start)
                .le(ReferralCommission::getCreateTime, end));
        vo.setCommissionCount(commissionCount);

        QueryWrapper<ReferralCommission> amountWrapper = new QueryWrapper<>();
        amountWrapper.select("IFNULL(SUM(amount),0) AS total")
                .eq("type", ReferralCommissionType.ORDER.getValue())
                .eq("direction", Direction.INCR.getValue())
                .ge("create_time", start)
                .le("create_time", end);
        vo.setCommissionAmount(firstDecimal(iReferralCommissionService.getMap(amountWrapper)));

        vo.setInviteNewUserCount(iUserService.count(new LambdaQueryWrapper<UserEntity>()
                .ge(UserEntity::getCreateTime, start)
                .le(UserEntity::getCreateTime, end)
                .isNotNull(UserEntity::getSuperiorInvitationCode)
                .ne(UserEntity::getSuperiorInvitationCode, "")));
        return vo;
    }

    private AdminDatacenterWelfareVo buildWelfare(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterWelfareVo vo = new AdminDatacenterWelfareVo();
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        QueryWrapper<UserSignRecord> uvWrapper = new QueryWrapper<>();
        uvWrapper.select("COUNT(DISTINCT user_id) AS total")
                .ge("sign_date", startDate)
                .le("sign_date", endDate);
        vo.setSignUv(firstLong(iUserSignRecordService.getMap(uvWrapper)));

        vo.setSignCount(iUserSignRecordService.count(new LambdaQueryWrapper<UserSignRecord>()
                .ge(UserSignRecord::getSignDate, startDate)
                .le(UserSignRecord::getSignDate, endDate)));

        QueryWrapper<UserSignRecord> rewardWrapper = new QueryWrapper<>();
        rewardWrapper.select("IFNULL(SUM(coin_reward),0) AS total")
                .ge("sign_date", startDate)
                .le("sign_date", endDate);
        vo.setPeriodCoinRewardSum(firstLong(iUserSignRecordService.getMap(rewardWrapper)));

        QueryWrapper<UserWelfare> poolWrapper = new QueryWrapper<>();
        poolWrapper.select("IFNULL(SUM(coin_balance),0) AS total");
        vo.setCoinPoolBalance(firstLong(iUserWelfareService.getMap(poolWrapper)));
        return vo;
    }

    private AdminDatacenterServiceChatVo buildServiceChat(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterServiceChatVo vo = new AdminDatacenterServiceChatVo();
        vo.setOpenSessionCount(iCustomerServiceSessionService.count(new LambdaQueryWrapper<CustomerServiceSession>()
                .eq(CustomerServiceSession::getStatus, SESSION_STATUS_OPEN)));
        vo.setClosedSessionCount(iCustomerServiceSessionService.count(new LambdaQueryWrapper<CustomerServiceSession>()
                .eq(CustomerServiceSession::getStatus, SESSION_STATUS_CLOSED)));
        vo.setPeriodCreatedCount(iCustomerServiceSessionService.count(new LambdaQueryWrapper<CustomerServiceSession>()
                .ge(CustomerServiceSession::getCreateTime, start)
                .le(CustomerServiceSession::getCreateTime, end)));
        return vo;
    }

    private AdminDatacenterBrandVo buildBrand(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterBrandVo vo = new AdminDatacenterBrandVo();
        vo.setPeriodCreatedCount(iBrandCooperationService.count(new LambdaQueryWrapper<BrandCooperation>()
                .ge(BrandCooperation::getCreateTime, start)
                .le(BrandCooperation::getCreateTime, end)));
        vo.setPendingFollowCount(iBrandCooperationService.count(new LambdaQueryWrapper<BrandCooperation>()
                .eq(BrandCooperation::getStatus, 0)));

        QueryWrapper<BrandCooperation> funnelWrapper = new QueryWrapper<>();
        funnelWrapper.select("status", "COUNT(*) AS cnt").groupBy("status");
        Map<String, Long> countByStatus = toLongMap(iBrandCooperationService.listMaps(funnelWrapper), "status", "cnt");
        List<AdminDatacenterStatusCountVo> funnel = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : BRAND_STATUS_LABELS.entrySet()) {
            AdminDatacenterStatusCountVo item = new AdminDatacenterStatusCountVo();
            item.setStatus(String.valueOf(entry.getKey()));
            item.setStatusLabel(entry.getValue());
            item.setCount(countByStatus.getOrDefault(String.valueOf(entry.getKey()), 0L));
            funnel.add(item);
        }
        vo.setFunnel(funnel);
        return vo;
    }

    private AdminDatacenterWalletVo buildWallet(LocalDateTime start, LocalDateTime end) {
        AdminDatacenterWalletVo vo = new AdminDatacenterWalletVo();

        QueryWrapper<UserWallet> balanceWrapper = new QueryWrapper<>();
        balanceWrapper.select("IFNULL(SUM(amount),0) AS total");
        vo.setBalanceSum(firstDecimal(iUserWalletService.getMap(balanceWrapper)));

        BigDecimal inflow = sumWalletFlow(start, end, WALLET_DIRECTION_IN);
        BigDecimal outflow = sumWalletFlow(start, end, WALLET_DIRECTION_OUT);
        vo.setInflowAmount(inflow);
        vo.setOutflowAmount(outflow);
        vo.setNetInflowAmount(inflow.subtract(outflow));
        return vo;
    }

    private BigDecimal sumWalletFlow(LocalDateTime start, LocalDateTime end, int direction) {
        QueryWrapper<UserWalletTransaction> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(amount),0) AS total")
                .eq("direction", direction)
                .ge("create_time", start)
                .le("create_time", end);
        return firstDecimal(iUserWalletTransactionService.getMap(wrapper));
    }

    private long countOrdersCreated(LocalDateTime start, LocalDateTime end, String orderType, String status) {
        return iRecycleOrderService.count(new LambdaQueryWrapper<RecycleOrder>()
                .ge(RecycleOrder::getCreateTime, start)
                .le(RecycleOrder::getCreateTime, end)
                .eq(StringUtils.isNotBlank(orderType), RecycleOrder::getOrderType, orderType)
                .eq(StringUtils.isNotBlank(status), RecycleOrder::getStatus, status));
    }

    private BigDecimal sumCompletedGmv(LocalDateTime start, LocalDateTime end, String orderType) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(actual_amount),0) AS total")
                .eq("status", RecycleOrderStatus.COMPLETED.getCode())
                .ge("update_time", start)
                .le("update_time", end);
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        return firstDecimal(iRecycleOrderService.getMap(wrapper));
    }

    private BigDecimal sumOrderAmount(LocalDateTime start, LocalDateTime end, String orderType, String column) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(" + column + "),0) AS total")
                .ge("create_time", start)
                .le("create_time", end);
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        return firstDecimal(iRecycleOrderService.getMap(wrapper));
    }

    private long countNewUsers(LocalDateTime start, LocalDateTime end) {
        return iUserService.count(new LambdaQueryWrapper<UserEntity>()
                .ge(UserEntity::getCreateTime, start)
                .le(UserEntity::getCreateTime, end));
    }

    private long countDistinctOrderUsers(LocalDateTime start, LocalDateTime end, String orderType) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(DISTINCT user_id) AS total")
                .ge("create_time", start)
                .le("create_time", end);
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        return firstLong(iRecycleOrderService.getMap(wrapper));
    }

    private long countPayouts(LocalDateTime start, LocalDateTime end, String status) {
        return iOrderPayoutService.count(new LambdaQueryWrapper<OrderPayout>()
                .ge(OrderPayout::getCreateTime, start)
                .le(OrderPayout::getCreateTime, end)
                .eq(OrderPayout::getStatus, status));
    }

    private long countBacklog(String orderType) {
        return iRecycleOrderService.count(new LambdaQueryWrapper<RecycleOrder>()
                .in(RecycleOrder::getStatus, BACKLOG_STATUSES)
                .eq(StringUtils.isNotBlank(orderType), RecycleOrder::getOrderType, orderType));
    }

    private BigDecimal sumPendingSettlementAmount() {
        QueryWrapper<OrderSettlement> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(IFNULL(actual_amount, estimate_amount)),0) AS total")
                .eq("status", SETTLEMENT_STATUS_PENDING);
        return firstDecimal(iOrderSettlementService.getMap(wrapper));
    }

    private BigDecimal sumPendingPayoutAmount(String orderType) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(IFNULL(actual_amount, estimate_amount)),0) AS total")
                .in("status", PENDING_PAYOUT_ORDER_STATUSES);
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        return firstDecimal(iRecycleOrderService.getMap(wrapper));
    }

    private List<AdminDatacenterStatusCountVo> buildFunnel(LocalDateTime start, LocalDateTime end, String orderType) {
        QueryWrapper<RecycleOrder> wrapper = new QueryWrapper<>();
        wrapper.select("status", "COUNT(*) AS cnt")
                .ge("create_time", start)
                .le("create_time", end)
                .groupBy("status");
        if (StringUtils.isNotBlank(orderType)) {
            wrapper.eq("order_type", orderType);
        }
        Map<String, Long> countByStatus = toLongMap(iRecycleOrderService.listMaps(wrapper), "status", "cnt");

        List<AdminDatacenterStatusCountVo> funnel = new ArrayList<>();
        for (RecycleOrderStatus status : RecycleOrderStatus.values()) {
            AdminDatacenterStatusCountVo item = new AdminDatacenterStatusCountVo();
            item.setStatus(status.getCode());
            item.setStatusLabel(status.getLabel());
            item.setCount(countByStatus.getOrDefault(status.getCode(), 0L));
            funnel.add(item);
        }
        return funnel;
    }

    private AdminDatacenterCompareVo buildCompare(PeriodMetrics current, PeriodMetrics previous) {
        AdminDatacenterCompareVo compare = new AdminDatacenterCompareVo();
        compare.setOrderCount(previous.orderCount);
        compare.setOrderCountChangeRate(changeRate(current.orderCount, previous.orderCount));
        compare.setGmv(previous.gmv);
        compare.setGmvChangeRate(changeRate(current.gmv, previous.gmv));
        compare.setCompletionRate(previous.completionRate);
        compare.setCancelRate(previous.cancelRate);
        compare.setNewUserCount(previous.newUserCount);
        compare.setNewUserCountChangeRate(changeRate(current.newUserCount, previous.newUserCount));
        compare.setPayoutSuccessRate(previous.payoutSuccessRate);
        return compare;
    }

    private Map<String, Long> toLongMap(List<Map<String, Object>> rows, String keyField, String valueField) {
        Map<String, Long> result = new HashMap<>();
        if (rows == null) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Object key = row.get(keyField);
            if (key == null) {
                continue;
            }
            result.put(String.valueOf(key), toLong(row.get(valueField)));
        }
        return result;
    }

    private BigDecimal rate(long numerator, long denominator) {
        if (denominator <= 0L) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal changeRate(long current, long previous) {
        if (previous <= 0L) {
            return null;
        }
        return BigDecimal.valueOf(current - previous)
                .divide(BigDecimal.valueOf(previous), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal changeRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return current.subtract(previous).divide(previous, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal firstDecimal(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return toDecimal(map.values().iterator().next());
    }

    private BigDecimal toDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }

    private long firstLong(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return 0L;
        }
        return toLong(map.values().iterator().next());
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private static class QueryRange {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String orderType;
    }

    private static class PeriodMetrics {
        private long orderCount;
        private long completedCount;
        private long cancelledCount;
        private BigDecimal completionRate = BigDecimal.ZERO;
        private BigDecimal cancelRate = BigDecimal.ZERO;
        private BigDecimal gmv = BigDecimal.ZERO;
        private BigDecimal estimateAmountSum = BigDecimal.ZERO;
        private BigDecimal actualAmountSum = BigDecimal.ZERO;
        private BigDecimal amountDiff = BigDecimal.ZERO;
        private long newUserCount;
        private long orderUserCount;
        private long payoutSuccessCount;
        private long payoutFailCount;
        private BigDecimal payoutSuccessRate = BigDecimal.ZERO;
        private List<AdminDatacenterStatusCountVo> funnel = new ArrayList<>();
    }
}
