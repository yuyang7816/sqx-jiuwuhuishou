package com.sqx.app.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.base.constants.SysConfigConstant;
import com.sqx.app.express.aliyun.AliyunLogisticsService;
import com.sqx.app.express.sf.SfExpressOrderService;
import com.sqx.app.order.db.service.*;
import com.sqx.app.order.domain.bo.*;
import com.sqx.app.order.domain.entity.*;
import com.sqx.app.order.domain.vo.OrderCourierVo;
import com.sqx.app.order.domain.vo.RecycleOrderDetailVo;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.app.order.domain.vo.RecycleOrderLogVo;
import com.sqx.app.order.domain.vo.RecycleOrderVo;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.enums.RecycleFulfillType;
import com.sqx.app.order.enums.RecycleOrderType;
import com.sqx.app.order.service.AppointmentOptionService;
import com.sqx.app.order.service.RecycleOrderService;
import com.sqx.app.order.support.RecycleOrderStateMachine;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.pickup.service.PickupTaskAssignService;
import com.sqx.app.product.domain.entity.ProductBrand;
import com.sqx.app.product.domain.entity.ProductCategory;
import com.sqx.app.product.domain.entity.ProductModel;
import com.sqx.app.product.support.ProductRecycleSupport;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.GenUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqx.common.mybatis.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 回收订单业务服务实现
 */
@Slf4j
@Service
public class RecycleOrderServiceImpl implements RecycleOrderService {

    private static final String ACTION_CREATE = "CREATE";
    private static final String ACTION_CANCEL = "CANCEL";
    private static final String ACTION_RESCHEDULE = "RESCHEDULE";
    private static final String ACTION_EXCEPTION = "EXCEPTION";
    private static final String ACTION_MAIL_SHIP = "MAIL_SHIP";
    private static final String MAIL_STATUS_WAIT_SHIP = "待顺丰寄件取件";
    private static final String MAIL_STATUS_WAIT_RECEIVE = "待收货";
    private static final String DEFAULT_EXPRESS_COMPANY = "顺丰速运";
    private static final String OPERATOR_TYPE_USER = "USER";
    private static final String CONFIG_COURIER_POOL = "order_courier_pool";
    private static final String CONFIG_APPLIANCE_MAIL_ADDRESS = "appliance_mail_address";
    private static final String DEFAULT_PLATFORM_MAIL_RECEIVER = "平台仓储部";
    private static final String DEFAULT_PLATFORM_MAIL_RECEIVER_PHONE = "400-800-6688";
    private static final String DEFAULT_PLATFORM_MAIL_RECEIVER_ADDRESS = "上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心";
    private static final String DEFAULT_APPLIANCE_MAIL_ADDRESS = "上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心 收件人：平台仓储部 电话：400-800-6688";
    private static final DateTimeFormatter APPOINTMENT_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("M月d日 HH:mm");

    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderItemService iRecycleOrderItemService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;
    @Resource
    private IOrderRescheduleService iOrderRescheduleService;
    @Resource
    private IOrderExceptionService iOrderExceptionService;
    @Resource
    private IUserAddressService iUserAddressService;
    @Resource
    private ProductRecycleSupport productRecycleSupport;
    @Resource
    private PickupTaskAssignService pickupTaskAssignService;
    @Resource
    private IPickupTaskService iPickupTaskService;
    @Resource
    private IUserService iUserService;
    @Resource
    private SfExpressOrderService sfExpressOrderService;
    @Resource
    private AliyunLogisticsService aliyunLogisticsService;
    @Resource
    private AppointmentOptionService appointmentOptionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createClothesOrder(Long userId, ClothesOrderSaveBo saveBo) {
        UserAddress address = getUserAddress(userId, saveBo.getAddressId());
        RecycleOrder order = buildBaseOrder(userId, address, saveBo.getAppointmentTime(), saveBo.getRemark());
        String categoryName = StringUtils.defaultIfBlank(saveBo.getCategoryName(), RecycleOrderType.CLOTHES.getLabel());
        order.setCategoryName(categoryName);
        boolean bookOrder = categoryName.contains("书籍");
        order.setOrderType(bookOrder ? RecycleOrderType.BOOK.getCode() : RecycleOrderType.CLOTHES.getCode());
        order.setFulfillType(RecycleFulfillType.VISIT.getCode());
        order.setEstimateAmount(saveBo.getEstimateAmount());
        order.setStatus(RecycleOrderStatus.WAIT_ASSIGN.getCode());
        order.setStatusDesc(RecycleOrderStatus.WAIT_ASSIGN.getLabel());
        iRecycleOrderService.save(order);

        RecycleOrderItem item = new RecycleOrderItem();
        item.setOrderId(order.getId());
        item.setItemType("weight");
        item.setItemName("预估重量");
        item.setItemValue(saveBo.getWeight());
        item.setQuantity(parseWeightQuantity(saveBo.getWeight()));
        item.setUnit("kg");
        item.setAmount(saveBo.getEstimateAmount());
        iRecycleOrderItemService.save(item);

        appendLog(order.getId(), ACTION_CREATE, null, order.getStatus(), "创建" + categoryName + "订单", userId, OPERATOR_TYPE_USER);
        pickupTaskAssignService.assignForOrder(order, StringUtils.defaultIfBlank(saveBo.getWeight(), "约 1 批"));
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createApplianceOrder(Long userId, ApplianceOrderSaveBo saveBo) {
        UserAddress address = getUserAddress(userId, saveBo.getAddressId());
        ProductModel model = productRecycleSupport.requireEnabledModel(saveBo.getModelId());
        ProductCategory category = productRecycleSupport.resolveCategoryByModel(model);
        RecycleFulfillType fulfillType = RecycleFulfillType.of(saveBo.getFulfillType());

        BigDecimal estimateMin = Objects.nonNull(saveBo.getEstimateMinAmount())
                ? saveBo.getEstimateMinAmount()
                : model.resolveMinPrice();
        BigDecimal estimateMax = Objects.nonNull(saveBo.getEstimateMaxAmount())
                ? saveBo.getEstimateMaxAmount()
                : model.resolveMaxPrice();
        if (estimateMax.compareTo(estimateMin) < 0) {
            estimateMax = estimateMin;
        }
        BigDecimal estimateAmount = Objects.nonNull(saveBo.getEstimateAmount())
                ? saveBo.getEstimateAmount()
                : estimateMin.add(estimateMax).divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP);

        RecycleOrder order = buildBaseOrder(userId, address, saveBo.getAppointmentTime(), saveBo.getRemark());
        order.setOrderType(RecycleOrderType.APPLIANCE.getCode());
        order.setFulfillType(fulfillType.getCode());
        order.setCategoryName(category.getName() + " / " + model.getName());
        order.setEstimateAmount(estimateAmount);
        order.setEstimateMinAmount(estimateMin);
        order.setEstimateMaxAmount(estimateMax);
        if (fulfillType.isMail()) {
            order.setMailAddressText(resolvePlatformMailAddress());
            order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
            order.setStatusDesc("待顺丰寄件取件");
        } else {
            order.setStatus(RecycleOrderStatus.WAIT_ASSIGN.getCode());
            order.setStatusDesc(RecycleOrderStatus.WAIT_ASSIGN.getLabel());
        }
        iRecycleOrderService.save(order);

        RecycleOrderItem item = new RecycleOrderItem();
        item.setOrderId(order.getId());
        item.setItemType("model");
        item.setItemName(model.getName());
        item.setItemValue(String.valueOf(model.getId()));
        item.setQuantity(BigDecimal.ONE);
        item.setUnit("台");
        item.setAmount(estimateAmount);
        iRecycleOrderItemService.save(item);

        String createContent = fulfillType.isMail() ? "创建家电邮寄回收订单" : "创建家电上门回收订单";
        appendLog(order.getId(), ACTION_CREATE, null, order.getStatus(), createContent, userId, OPERATOR_TYPE_USER);
        if (fulfillType.isMail()) {
            placeSfExpressAndPersist(order, address);
        } else {
            pickupTaskAssignService.assignForOrder(order, model.getName() + " 1 台");
        }
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDigitalOrder(Long userId, DigitalOrderSaveBo saveBo) {
        UserAddress address = getUserAddress(userId, saveBo.getAddressId());
        ProductModel model = productRecycleSupport.requireEnabledModel(saveBo.getModelId());
        ProductBrand brand = productRecycleSupport.requireEnabledBrand(model.getBrandId());

        BigDecimal estimateAmount = Objects.nonNull(saveBo.getEstimateAmount()) ? saveBo.getEstimateAmount() : model.getBasePrice();
        RecycleOrder order = buildBaseOrder(userId, address, saveBo.getAppointmentTime(), saveBo.getRemark());
        boolean luxuryOrder = "luxury".equalsIgnoreCase(StringUtils.trimToEmpty(saveBo.getScene()));
        order.setOrderType(luxuryOrder ? RecycleOrderType.LUXURY.getCode() : RecycleOrderType.DIGITAL.getCode());
        // 数码/奢侈品「预约免费寄件」固定为邮寄履约，不派单给平台员工
        order.setFulfillType(RecycleFulfillType.MAIL.getCode());
        order.setCategoryName(brand.getName() + " / " + model.getName());
        order.setEstimateAmount(estimateAmount);
        order.setMailAddressText(resolvePlatformMailAddress());
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setStatusDesc("待顺丰寄件取件");
        iRecycleOrderService.save(order);

        RecycleOrderItem item = new RecycleOrderItem();
        item.setOrderId(order.getId());
        item.setItemType("model");
        item.setItemName(model.getName());
        item.setItemValue(String.valueOf(model.getId()));
        item.setQuantity(BigDecimal.ONE);
        item.setUnit("台");
        item.setAmount(estimateAmount);
        iRecycleOrderItemService.save(item);

        appendLog(order.getId(), ACTION_CREATE, null, order.getStatus(),
                luxuryOrder ? "创建奢侈品邮寄回收订单" : "创建数码邮寄回收订单", userId, OPERATOR_TYPE_USER);
        placeSfExpressAndPersist(order, address);
        return order.getId();
    }

    @Override
    public IPage<RecycleOrderVo> pageInfo(Long userId, RecycleOrderPageBo pageBo) {
        Page<RecycleOrder> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        QueryWrapper<RecycleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(RecycleOrder::getUserId, userId)
                .eq(StringUtils.isNotBlank(pageBo.getOrderType()), RecycleOrder::getOrderType, pageBo.getOrderType())
                .in(CollectionUtils.isNotEmpty(resolveStatusCodes(pageBo.getStatusKey())), RecycleOrder::getStatus, resolveStatusCodes(pageBo.getStatusKey()))
                .orderByDesc(RecycleOrder::getCreateTime);

        IPage<RecycleOrder> iPage = iRecycleOrderService.page(initPage, queryWrapper);
        List<RecycleOrderVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (RecycleOrder record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public RecycleOrderDetailVo info(Long userId, RecycleOrderInfoBo infoBo) {
        RecycleOrder order = getOwnedOrder(userId, infoBo.getId());
        RecycleOrderDetailVo detailVo = new RecycleOrderDetailVo();
        BeanUtils.copyProperties(toVo(order), detailVo);

        List<RecycleOrderLog> logs = iRecycleOrderLogService.list(new LambdaQueryWrapper<RecycleOrderLog>()
                .eq(RecycleOrderLog::getOrderId, order.getId())
                .orderByDesc(RecycleOrderLog::getCreateTime));
        List<RecycleOrderLogVo> logVoList = CollectionUtils.isNotEmpty(logs)
                ? logs.stream().map(this::toLogVo).collect(Collectors.toList())
                : new ArrayList<>();
        detailVo.setLogs(logVoList);
        if (RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            detailVo.setExpressCompany(StringUtils.defaultIfBlank(detailVo.getExpressCompany(), "顺丰速运"));
            try {
                detailVo.setExpressTracks(aliyunLogisticsService.listExpressTracks(
                        order.getExpressNo(), resolveExpressPhones(order)));
            } catch (Exception ex) {
                log.warn("用户订单详情加载物流轨迹失败: orderId={}, err={}", order.getId(), ex.getMessage());
                detailVo.setExpressTracks(new ArrayList<>());
            }
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, CancelOrderBo cancelBo) {
        RecycleOrder order = getOwnedOrder(userId, cancelBo.getOrderId());
        if (!RecycleOrderStateMachine.canCancel(order.getStatus())) {
            throw new BizException("当前订单状态不允许取消");
        }

        String fromStatus = order.getStatus();
        if (RecycleFulfillType.of(order.getFulfillType()).isMail() && StringUtils.isNotBlank(order.getExpressNo())) {
            sfExpressOrderService.cancelMailOrder(order);
        }
        order.setStatus(RecycleOrderStatus.CANCELLED.getCode());
        order.setStatusDesc(RecycleOrderStatus.CANCELLED.getLabel());
        iRecycleOrderService.updateById(order);
        appendLog(order.getId(), ACTION_CANCEL, fromStatus, order.getStatus(), "用户取消订单", userId, OPERATOR_TYPE_USER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reschedule(Long userId, RescheduleBo rescheduleBo) {
        RecycleOrder order = getOwnedOrder(userId, rescheduleBo.getOrderId());
        String fromStatus = order.getStatus();
        if (!RecycleOrderStateMachine.canReschedule(fromStatus)) {
            throw new BizException("当前订单状态不允许改约");
        }

        OrderReschedule reschedule = new OrderReschedule();
        reschedule.setOrderId(order.getId());
        reschedule.setReason(rescheduleBo.getReason());
        reschedule.setNewAppointmentTime(rescheduleBo.getNewAppointmentTime());
        reschedule.setRemark(rescheduleBo.getRemark());
        iOrderRescheduleService.save(reschedule);

        RecycleOrderStatus currentStatus = RecycleOrderStatus.of(fromStatus);
        String appointmentText = formatAppointmentRange(
                rescheduleBo.getNewAppointmentTime(),
                appointmentOptionService.resolveSlotEndTime(rescheduleBo.getNewAppointmentTime())
        );
        String statusDesc = currentStatus.getLabel() + " · 已改约至 " + appointmentText;
        if (StringUtils.isNotBlank(rescheduleBo.getReason())) {
            statusDesc = statusDesc + "（" + rescheduleBo.getReason() + "）";
        }

        // 使用 updateById，确保 appointment_time / status_desc 一并落库（避免仅 Wrapper 更新偶发未生效）
        order.setAppointmentTime(rescheduleBo.getNewAppointmentTime());
        order.setStatusDesc(statusDesc);
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        syncPickupTaskAppointment(order.getId(), rescheduleBo.getNewAppointmentTime());

        appendLog(order.getId(), ACTION_RESCHEDULE, fromStatus, fromStatus, statusDesc, userId, OPERATOR_TYPE_USER);
    }

    private String formatAppointmentRange(LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(start)) {
            return "";
        }
        String startText = start.format(APPOINTMENT_DISPLAY_FORMATTER);
        if (Objects.isNull(end)) {
            return startText;
        }
        return startText + " - " + end.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void syncPickupTaskAppointment(Long orderId, java.time.LocalDateTime appointmentTime) {
        PickupTask task = iPickupTaskService.getOne(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getOrderId, orderId)
                .in(PickupTask::getStatus, PickupTaskStatus.PENDING.name(), PickupTaskStatus.RUNNING.name())
                .orderByDesc(PickupTask::getCreateTime)
                .last("LIMIT 1"));
        if (Objects.isNull(task)) {
            return;
        }
        task.setAppointmentTime(appointmentTime);
        iPickupTaskService.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitException(Long userId, ExceptionBo exceptionBo) {
        RecycleOrder order = getOwnedOrder(userId, exceptionBo.getOrderId());
        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.EXCEPTION_PENDING);

        OrderException orderException = new OrderException();
        orderException.setOrderId(order.getId());
        orderException.setExceptionType(exceptionBo.getExceptionType());
        orderException.setExpectAction(exceptionBo.getExpectAction());
        orderException.setDescription(exceptionBo.getDescription());
        iOrderExceptionService.save(orderException);

        order.setStatus(RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        order.setStatusDesc(RecycleOrderStatus.EXCEPTION_PENDING.getLabel());
        iRecycleOrderService.updateById(order);

        String content = "用户提交异常反馈：" + exceptionBo.getExceptionType();
        appendLog(order.getId(), ACTION_EXCEPTION, fromStatus, order.getStatus(), content, userId, OPERATOR_TYPE_USER);
    }

    @Override
    public OrderCourierVo getCourierInfo(Long userId, RecycleOrderInfoBo infoBo) {
        RecycleOrder order = getOwnedOrder(userId, infoBo.getId());
        OrderCourierVo courierVo = resolveCourierFromPickupTask(order.getId());
        if (Objects.isNull(courierVo)) {
            courierVo = resolveCourierFromConfig(order.getId());
        }
        if (Objects.isNull(courierVo)) {
            throw new BizException("暂未分配回收员，请稍后再试");
        }
        return courierVo;
    }

    @Override
    public List<ExpressTrackVo> listExpressTracks(Long userId, RecycleOrderInfoBo infoBo) {
        RecycleOrder order = getOwnedOrder(userId, infoBo.getId());
        if (!RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            throw new BizException("仅邮寄订单支持查询物流轨迹");
        }
        if (StringUtils.isBlank(order.getExpressNo())) {
            throw new BizException("订单尚未填写快递单号");
        }
        return aliyunLogisticsService.listExpressTracks(order.getExpressNo(), resolveExpressPhones(order));
    }

    private String[] resolveExpressPhones(RecycleOrder order) {
        return new String[]{
                order.getContactMobile(),
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_PHONE)
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipMail(Long userId, ShipMailOrderBo shipBo) {
        RecycleOrder order = getOwnedOrder(userId, shipBo.getOrderId());
        if (!RecycleFulfillType.of(order.getFulfillType()).isMail()) {
            throw new BizException("仅邮寄订单支持发货");
        }
        if (!RecycleOrderStatus.IN_PROGRESS.getCode().equals(order.getStatus())) {
            throw new BizException("当前订单状态不允许发货");
        }
        if (MAIL_STATUS_WAIT_RECEIVE.equals(order.getStatusDesc())) {
            throw new BizException("订单已发货，请勿重复操作");
        }
        if (StringUtils.isNotBlank(order.getStatusDesc())
                && !MAIL_STATUS_WAIT_SHIP.equals(order.getStatusDesc())
                && !order.getStatusDesc().contains("待顺丰")) {
            throw new BizException("当前物流状态不允许发货");
        }
        String expressNo = StringUtils.trimToEmpty(shipBo.getExpressNo());
        if (StringUtils.isBlank(expressNo)) {
            throw new BizException("快递单号不能为空");
        }

        String fromStatus = order.getStatus();
        order.setExpressNo(expressNo);
        order.setStatusDesc(MAIL_STATUS_WAIT_RECEIVE);
        order.setUpdateTime(LocalDateTime.now());
        iRecycleOrderService.updateById(order);

        String company = StringUtils.defaultIfBlank(StringUtils.trimToEmpty(shipBo.getExpressCompany()), DEFAULT_EXPRESS_COMPANY);
        appendLog(order.getId(), ACTION_MAIL_SHIP, fromStatus, order.getStatus(),
                "用户发货，快递：" + company + "，单号：" + expressNo, userId, OPERATOR_TYPE_USER);
    }

    private OrderCourierVo resolveCourierFromPickupTask(Long orderId) {
        PickupTask task = iPickupTaskService.getOne(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getOrderId, orderId)
                .orderByDesc(PickupTask::getCreateTime)
                .last("LIMIT 1"));
        if (Objects.isNull(task) || Objects.isNull(task.getStaffId())) {
            return null;
        }
        UserEntity staff = iUserService.getById(task.getStaffId());
        if (Objects.isNull(staff)) {
            return null;
        }
        String mobile = staff.getPhone();
        if (StringUtils.isBlank(staff.getUserName()) || StringUtils.isBlank(mobile)) {
            return null;
        }
        OrderCourierVo vo = new OrderCourierVo();
        vo.setName(staff.getUserName());
        vo.setMobile(mobile);
        vo.setMobileMask(maskMobile(mobile));
        return vo;
    }

    private OrderCourierVo resolveCourierFromConfig(Long orderId) {
        String configValue = ConfigUtil.getConfigValueOrDefault(CONFIG_COURIER_POOL);
        if (StringUtils.isBlank(configValue)) {
            return null;
        }
        try {
            JSONArray pool = JSON.parseArray(configValue);
            if (CollectionUtils.isEmpty(pool)) {
                return null;
            }
            JSONObject item = pool.getJSONObject((int) (orderId % pool.size()));
            if (Objects.isNull(item)) {
                return null;
            }
            String name = item.getString("name");
            String mobile = item.getString("mobile");
            if (StringUtils.isAnyBlank(name, mobile)) {
                return null;
            }
            OrderCourierVo vo = new OrderCourierVo();
            vo.setName(name);
            vo.setMobile(mobile);
            vo.setMobileMask(maskMobile(mobile));
            return vo;
        } catch (Exception ex) {
            return null;
        }
    }

    private RecycleOrder buildBaseOrder(Long userId, UserAddress address, java.time.LocalDateTime appointmentTime, String remark) {
        RecycleOrder order = new RecycleOrder();
        order.setOrderNo(GenUtil.genRecycleOrderNo());
        order.setUserId(userId);
        order.setFulfillType(RecycleFulfillType.VISIT.getCode());
        order.setAppointmentTime(appointmentTime);
        order.setAddressId(address.getId());
        order.setContactName(address.getContactName());
        order.setContactMobile(address.getContactMobile());
        order.setAddressText(buildAddressText(address));
        order.setRemark(remark);
        return order;
    }

    private UserAddress getUserAddress(Long userId, Long addressId) {
        UserAddress userAddress = iUserAddressService.getOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getId, addressId));
        if (Objects.isNull(userAddress)) {
            throw new BizException("地址不存在");
        }
        return userAddress;
    }

    private RecycleOrder getOwnedOrder(Long userId, Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order) || !Objects.equals(order.getUserId(), userId)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private List<String> resolveStatusCodes(String statusKey) {
        if (StringUtils.isBlank(statusKey)) {
            return null;
        }
        List<String> statusCodes = Arrays.stream(RecycleOrderStatus.values())
                .filter(status -> statusKey.equals(status.getStatusKey()))
                .map(RecycleOrderStatus::getCode)
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(statusCodes) ? null : statusCodes;
    }

    private RecycleOrderVo toVo(RecycleOrder order) {
        RecycleOrderVo vo = new RecycleOrderVo();
        BeanUtils.copyProperties(order, vo);
        vo.setContactMobile(maskMobile(order.getContactMobile()));
        RecycleOrderStatus status = RecycleOrderStatus.of(order.getStatus());
        vo.setStatusKey(status.getStatusKey());
        RecycleFulfillType fulfillType = RecycleFulfillType.of(order.getFulfillType());
        vo.setFulfillType(fulfillType.getCode());
        vo.setFulfillTypeLabel(fulfillType.getLabel());
        vo.setPickupAddressText(order.getAddressText());
        vo.setMailAddressText(order.getMailAddressText());
        vo.setExpressNo(order.getExpressNo());
        if (fulfillType.isMail()) {
            vo.setExpressCompany(StringUtils.defaultIfBlank(vo.getExpressCompany(), "顺丰速运"));
        }
        vo.setEstimateAmountConfirmed(resolveEstimateAmountConfirmed(order));
        vo.setAppointmentEndTime(appointmentOptionService.resolveSlotEndTime(order.getAppointmentTime()));
        return vo;
    }

    private void placeSfExpressAndPersist(RecycleOrder order, UserAddress address) {
        // 凭证未配置时不阻断邮寄订单创建（运单可在后台补配后另行下单）
        if (!sfExpressOrderService.isConfigured()) {
            log.warn("顺丰管家凭证未配置，跳过下单: orderNo={}，请在后台系统配置填写 sf_butler_app_id / sf_butler_sk / sf_butler_company_id",
                    order.getOrderNo());
            appendLog(order.getId(), "EXPRESS_SKIP", order.getStatus(), order.getStatus(),
                    "待顺丰取件",
                    order.getUserId(), OPERATOR_TYPE_USER);
            return;
        }
        String mailNo = sfExpressOrderService.placeMailOrder(order, address);
        order.setExpressNo(mailNo);
        iRecycleOrderService.updateById(order);
        appendLog(order.getId(), "EXPRESS_PLACE", order.getStatus(), order.getStatus(),
                "顺丰下单成功，运单号：" + mailNo, order.getUserId(), OPERATOR_TYPE_USER);
    }

    private String resolvePlatformMailAddress() {
        String receiver = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER));
        String phone = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_PHONE));
        String address = StringUtils.trimToEmpty(
                ConfigUtil.getConfigValueOrDefault(SysConfigConstant.PlatformMail.RECEIVER_ADDRESS));
        if (StringUtils.isNotBlank(receiver) || StringUtils.isNotBlank(phone) || StringUtils.isNotBlank(address)) {
            String resolvedReceiver = StringUtils.defaultIfBlank(receiver, DEFAULT_PLATFORM_MAIL_RECEIVER);
            String resolvedPhone = StringUtils.defaultIfBlank(phone, DEFAULT_PLATFORM_MAIL_RECEIVER_PHONE);
            String resolvedAddress = StringUtils.defaultIfBlank(address, DEFAULT_PLATFORM_MAIL_RECEIVER_ADDRESS);
            return resolvedAddress + " 收件人：" + resolvedReceiver + " 电话：" + resolvedPhone;
        }
        String legacy = ConfigUtil.getConfigValueOrDefault(CONFIG_APPLIANCE_MAIL_ADDRESS);
        return StringUtils.defaultIfBlank(legacy, DEFAULT_APPLIANCE_MAIL_ADDRESS);
    }

    private boolean resolveEstimateAmountConfirmed(RecycleOrder order) {
        if (Objects.equals(order.getEstimateAmountConfirmed(), 1)) {
            return true;
        }
        String status = order.getStatus();
        return RecycleOrderStatus.WAIT_PAYOUT.getCode().equals(status)
                || RecycleOrderStatus.PAYOUT_PROCESSING.getCode().equals(status)
                || RecycleOrderStatus.COMPLETED.getCode().equals(status);
    }

    private RecycleOrderLogVo toLogVo(RecycleOrderLog log) {
        RecycleOrderLogVo vo = new RecycleOrderLogVo();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    private String buildAddressText(UserAddress address) {
        StringBuilder builder = new StringBuilder();
        appendPart(builder, address.getProvince());
        appendPart(builder, address.getCity());
        appendPart(builder, address.getDistrict());
        appendPart(builder, address.getStreet());
        appendPart(builder, address.getDetailAddress());
        return builder.toString().trim();
    }

    private void appendPart(StringBuilder builder, String part) {
        if (StringUtils.isNotBlank(part)) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(part);
        }
    }

    private BigDecimal parseWeightQuantity(String weight) {
        if (StringUtils.isBlank(weight)) {
            return null;
        }
        String digits = weight.replaceAll("[^0-9.-]", " ").trim();
        if (StringUtils.isBlank(digits)) {
            return null;
        }
        String[] parts = digits.split("\\s+");
        try {
            if (parts.length == 1) {
                return new BigDecimal(parts[0]);
            }
            BigDecimal first = new BigDecimal(parts[0]);
            BigDecimal second = new BigDecimal(parts[1]);
            return first.add(second).divide(BigDecimal.valueOf(2));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    private void appendLog(Long orderId, String actionType, String fromStatus, String toStatus,
                           String content, Long operatorId, String operatorType) {
        RecycleOrderLog log = new RecycleOrderLog();
        log.setOrderId(orderId);
        log.setActionType(actionType);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setContent(content);
        log.setOperatorId(operatorId);
        log.setOperatorType(operatorType);
        iRecycleOrderLogService.save(log);
    }
}
