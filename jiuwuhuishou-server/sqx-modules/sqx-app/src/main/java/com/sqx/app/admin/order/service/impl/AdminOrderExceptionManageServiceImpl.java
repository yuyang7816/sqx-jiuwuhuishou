package com.sqx.app.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionHandleBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionPageBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderHandleBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderExceptionVo;
import com.sqx.app.admin.order.service.AdminOrderExceptionManageService;
import com.sqx.app.admin.order.service.AdminOrderManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderException;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminOrderExceptionManageServiceImpl implements AdminOrderExceptionManageService {

    @Resource
    private IOrderExceptionService iOrderExceptionService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private AdminOrderManageService adminOrderManageService;

    @Override
    public IPage<AdminOrderExceptionVo> pageInfo(AdminOrderExceptionPageBo pageBo) {
        Page<OrderException> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        List<Long> orderIds = resolveFilteredOrderIds(pageBo.getOrderNo(), pageBo.getUserName(), pageBo.getPhone(), pageBo.getOrderId());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<OrderException> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(orderIds != null, OrderException::getOrderId, orderIds)
                .orderByDesc(OrderException::getCreateTime);
        IPage<OrderException> iPage = iOrderExceptionService.page(initPage, wrapper);
        return PageUtil.copyPage(iPage, toVoList(iPage.getRecords()));
    }

    @Override
    public AdminOrderExceptionVo info(AdminOrderExceptionInfoBo infoBo) {
        OrderException record = requireException(infoBo.getId());
        return toVo(record, loadOrderMap(Collections.singletonList(record)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(AdminOrderExceptionHandleBo handleBo) {
        OrderException record = requireException(handleBo.getId());
        RecycleOrder order = iRecycleOrderService.getById(record.getOrderId());
        if (Objects.isNull(order)) {
            throw new BizException("关联订单不存在");
        }
        if (!RecycleOrderStatus.EXCEPTION_PENDING.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单不在异常处理中，无法处理该反馈");
        }

        AdminOrderHandleBo orderHandleBo = new AdminOrderHandleBo();
        orderHandleBo.setId(order.getId());
        orderHandleBo.setHandleType(normalizeHandleType(handleBo.getHandleType()));
        orderHandleBo.setRemark(buildHandleRemark(record, handleBo.getRemark()));
        adminOrderManageService.handle(orderHandleBo);
    }

    @Override
    public List<AdminOrderExceptionVo> listByOrderId(Long orderId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        List<OrderException> records = iOrderExceptionService.list(new LambdaQueryWrapper<OrderException>()
                .eq(OrderException::getOrderId, orderId)
                .orderByDesc(OrderException::getCreateTime));
        return toVoList(records);
    }

    private String normalizeHandleType(String handleType) {
        if (StringUtils.isBlank(handleType)) {
            throw new BizException("处理类型不能为空");
        }
        if ("RESOLVED".equalsIgnoreCase(handleType) || "已处理".equals(handleType)) {
            return "RESOLVED";
        }
        if ("UNRESOLVED".equalsIgnoreCase(handleType) || "未处理".equals(handleType)) {
            return "UNRESOLVED";
        }
        throw new BizException("不支持的处理类型：" + handleType);
    }

    private String buildHandleRemark(OrderException record, String remark) {
        String prefix = "异常反馈[" + record.getExceptionType() + "]";
        if (StringUtils.isBlank(remark)) {
            return prefix;
        }
        return prefix + "：" + remark;
    }

    private List<AdminOrderExceptionVo> toVoList(List<OrderException> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        Map<Long, RecycleOrder> orderMap = loadOrderMap(records);
        List<AdminOrderExceptionVo> voList = new ArrayList<>();
        for (OrderException record : records) {
            voList.add(toVo(record, orderMap));
        }
        return voList;
    }

    private AdminOrderExceptionVo toVo(OrderException record, Map<Long, RecycleOrder> orderMap) {
        AdminOrderExceptionVo vo = new AdminOrderExceptionVo();
        BeanUtils.copyProperties(record, vo);
        RecycleOrder order = orderMap.get(record.getOrderId());
        if (Objects.nonNull(order)) {
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getStatus());
            vo.setOrderStatusDesc(order.getStatusDesc());
            adminLookupSupport.fillUserFields(order.getUserId(), vo);
        }
        return vo;
    }

    private Map<Long, RecycleOrder> loadOrderMap(List<OrderException> records) {
        List<Long> orderIds = records.stream()
                .map(OrderException::getOrderId)
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

    private OrderException requireException(Long id) {
        OrderException record = iOrderExceptionService.getById(id);
        if (Objects.isNull(record)) {
            throw new BizException("异常记录不存在");
        }
        return record;
    }
}
