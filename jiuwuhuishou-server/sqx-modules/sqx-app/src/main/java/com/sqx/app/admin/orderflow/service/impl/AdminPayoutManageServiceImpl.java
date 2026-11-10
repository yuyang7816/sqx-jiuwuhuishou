package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutCreateBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutPageBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminPayoutLogVo;
import com.sqx.app.admin.orderflow.domain.vo.AdminPayoutVo;
import com.sqx.app.admin.orderflow.service.AdminPayoutManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.payout.db.service.IOrderPayoutLogService;
import com.sqx.app.payout.db.service.IOrderPayoutService;
import com.sqx.app.payout.domain.entity.OrderPayout;
import com.sqx.app.payout.domain.entity.OrderPayoutLog;
import com.sqx.app.payout.service.PayoutService;
import com.sqx.app.pickup.db.service.IOrderSettlementService;
import com.sqx.app.pickup.domain.entity.OrderSettlement;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AdminPayoutManageServiceImpl implements AdminPayoutManageService {

    private static final String STATUS_SUCCESS = "SUCCESS";

    @Resource
    private IOrderPayoutService iOrderPayoutService;
    @Resource
    private IOrderPayoutLogService iOrderPayoutLogService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IOrderSettlementService iOrderSettlementService;
    @Resource
    private PayoutService payoutService;

    @Override
    public IPage<AdminPayoutVo> pageInfo(AdminPayoutPageBo pageBo) {
        Page<OrderPayout> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<Long> orderIds = adminLookupSupport.resolveOrderIds(pageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<OrderPayout> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, OrderPayout::getUserId, userIds)
                .in(orderIds != null, OrderPayout::getOrderId, orderIds)
                .eq(StringUtils.isNotBlank(pageBo.getStatus()), OrderPayout::getStatus, pageBo.getStatus())
                .orderByDesc(OrderPayout::getCreateTime);

        IPage<OrderPayout> iPage = iOrderPayoutService.page(initPage, wrapper);
        List<AdminPayoutVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (OrderPayout record : iPage.getRecords()) {
                voList.add(toVo(record, false));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminPayoutVo info(AdminPayoutInfoBo infoBo) {
        OrderPayout entity = iOrderPayoutService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("打款单不存在");
        }
        return toVo(entity, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AdminPayoutCreateBo createBo) {
        RecycleOrder order = resolveOrder(createBo);
        if (!RecycleOrderStatus.WAIT_PAYOUT.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许创建打款单");
        }
        BigDecimal amount = resolvePayoutAmount(order);
        payoutService.startPayout(order, amount, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(AdminPayoutConfirmBo confirmBo) {
        OrderPayout payout = iOrderPayoutService.getById(confirmBo.getId());
        if (Objects.isNull(payout)) {
            throw new BizException("打款单不存在");
        }
        if (STATUS_SUCCESS.equals(payout.getStatus())) {
            throw new BizException("打款已完成");
        }
        RecycleOrder order = requireOrder(payout.getOrderId());
        payoutService.finishPayout(order, null);
    }

    private RecycleOrder resolveOrder(AdminPayoutCreateBo createBo) {
        if (Objects.nonNull(createBo.getOrderId())) {
            return requireOrder(createBo.getOrderId());
        }
        if (StringUtils.isBlank(createBo.getOrderNo())) {
            throw new BizException("请填写订单号");
        }
        RecycleOrder order = iRecycleOrderService.getOne(new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getOrderNo, createBo.getOrderNo().trim())
                .last("LIMIT 1"));
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private BigDecimal resolvePayoutAmount(RecycleOrder order) {
        if (Objects.nonNull(order.getActualAmount())) {
            return order.getActualAmount();
        }
        OrderSettlement settlement = iOrderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderId, order.getId())
                .last("LIMIT 1"));
        if (Objects.nonNull(settlement) && Objects.nonNull(settlement.getActualAmount())) {
            return settlement.getActualAmount();
        }
        if (Objects.nonNull(settlement) && Objects.nonNull(settlement.getEstimateAmount())) {
            return settlement.getEstimateAmount();
        }
        return order.getEstimateAmount();
    }

    private RecycleOrder requireOrder(Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private AdminPayoutVo toVo(OrderPayout entity, boolean withDetail) {
        AdminPayoutVo vo = new AdminPayoutVo();
        BeanUtils.copyProperties(entity, vo);
        vo.setOrderNo(adminLookupSupport.resolveOrderNo(entity.getOrderId()));
        adminLookupSupport.fillUserFields(entity.getUserId(), vo);
        if (withDetail) {
            RecycleOrder order = iRecycleOrderService.getById(entity.getOrderId());
            if (Objects.nonNull(order)) {
                vo.setOrderStatus(order.getStatus());
                vo.setOrderStatusDesc(order.getStatusDesc());
            }
            vo.setLogs(loadLogs(entity.getId()));
        }
        return vo;
    }

    private List<AdminPayoutLogVo> loadLogs(Long payoutId) {
        List<OrderPayoutLog> logs = iOrderPayoutLogService.list(new LambdaQueryWrapper<OrderPayoutLog>()
                .eq(OrderPayoutLog::getPayoutId, payoutId)
                .orderByAsc(OrderPayoutLog::getCreateTime));
        List<AdminPayoutLogVo> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(logs)) {
            return voList;
        }
        for (OrderPayoutLog log : logs) {
            AdminPayoutLogVo item = new AdminPayoutLogVo();
            item.setStatus(log.getStatus());
            item.setContent(log.getContent());
            item.setCreateTime(log.getCreateTime());
            voList.add(item);
        }
        return voList;
    }
}
