package com.sqx.app.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderRescheduleInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderReschedulePageBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderRescheduleVo;
import com.sqx.app.admin.order.service.AdminOrderRescheduleManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IOrderRescheduleService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderReschedule;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminOrderRescheduleManageServiceImpl implements AdminOrderRescheduleManageService {

    @Resource
    private IOrderRescheduleService iOrderRescheduleService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private AdminLookupSupport adminLookupSupport;

    @Override
    public IPage<AdminOrderRescheduleVo> pageInfo(AdminOrderReschedulePageBo pageBo) {
        Page<OrderReschedule> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        List<Long> orderIds = resolveFilteredOrderIds(pageBo.getOrderNo(), pageBo.getUserName(), pageBo.getPhone(), pageBo.getOrderId());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<OrderReschedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(orderIds != null, OrderReschedule::getOrderId, orderIds)
                .orderByDesc(OrderReschedule::getCreateTime);
        IPage<OrderReschedule> iPage = iOrderRescheduleService.page(initPage, wrapper);
        return PageUtil.copyPage(iPage, toVoList(iPage.getRecords()));
    }

    @Override
    public AdminOrderRescheduleVo info(AdminOrderRescheduleInfoBo infoBo) {
        OrderReschedule record = requireReschedule(infoBo.getId());
        return toVo(record, loadOrderMap(Collections.singletonList(record)));
    }

    @Override
    public List<AdminOrderRescheduleVo> listByOrderId(Long orderId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        List<OrderReschedule> records = iOrderRescheduleService.list(new LambdaQueryWrapper<OrderReschedule>()
                .eq(OrderReschedule::getOrderId, orderId)
                .orderByDesc(OrderReschedule::getCreateTime));
        return toVoList(records);
    }

    private List<AdminOrderRescheduleVo> toVoList(List<OrderReschedule> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        Map<Long, RecycleOrder> orderMap = loadOrderMap(records);
        List<AdminOrderRescheduleVo> voList = new ArrayList<>();
        for (OrderReschedule record : records) {
            voList.add(toVo(record, orderMap));
        }
        return voList;
    }

    private AdminOrderRescheduleVo toVo(OrderReschedule record, Map<Long, RecycleOrder> orderMap) {
        AdminOrderRescheduleVo vo = new AdminOrderRescheduleVo();
        BeanUtils.copyProperties(record, vo);
        RecycleOrder order = orderMap.get(record.getOrderId());
        if (Objects.nonNull(order)) {
            vo.setOrderNo(order.getOrderNo());
            adminLookupSupport.fillUserFields(order.getUserId(), vo);
        }
        return vo;
    }

    private Map<Long, RecycleOrder> loadOrderMap(List<OrderReschedule> records) {
        List<Long> orderIds = records.stream()
                .map(OrderReschedule::getOrderId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        return iRecycleOrderService.listByIds(orderIds).stream()
                .collect(Collectors.toMap(RecycleOrder::getId, Function.identity(), (left, right) -> left));
    }

    private List<Long> resolveFilteredOrderIds(String orderNo, String userName, String phone, Long orderId) {
        if (orderId != null) {
            return Collections.singletonList(orderId);
        }
        List<Long> byOrderNo = adminLookupSupport.resolveOrderIds(orderNo);
        List<Long> userIds = adminLookupSupport.resolveUserIds(userName, phone);
        List<Long> byUser = null;
        if (userIds != null) {
            if (userIds.isEmpty()) {
                return Collections.emptyList();
            }
            byUser = iRecycleOrderService.list(new LambdaQueryWrapper<RecycleOrder>()
                            .in(RecycleOrder::getUserId, userIds))
                    .stream()
                    .map(RecycleOrder::getId)
                    .collect(Collectors.toList());
            if (byUser.isEmpty()) {
                return Collections.emptyList();
            }
        }
        if (byOrderNo == null && byUser == null) {
            return null;
        }
        if (byOrderNo == null) {
            return byUser;
        }
        if (byUser == null) {
            return byOrderNo;
        }
        return byOrderNo.stream().filter(byUser::contains).collect(Collectors.toList());
    }

    private OrderReschedule requireReschedule(Long id) {
        OrderReschedule record = iOrderRescheduleService.getById(id);
        if (Objects.isNull(record)) {
            throw new BizException("改约记录不存在");
        }
        return record;
    }
}
