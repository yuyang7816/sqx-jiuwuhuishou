package com.sqx.app.admin.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理端关联查询辅助类：按用户名/手机号解析用户，填充关联展示字段。
 */
@Component
public class AdminLookupSupport {

    @Resource
    private IUserService iUserService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;

    public List<Long> resolveUserIds(String userName, String phone) {
        if (StringUtils.isBlank(userName) && StringUtils.isBlank(phone)) {
            return null;
        }
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(userName), UserEntity::getUserName, userName)
                .like(StringUtils.isNotBlank(phone), UserEntity::getPhone, phone);
        List<UserEntity> users = iUserService.list(wrapper);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(UserEntity::getUserId).collect(Collectors.toList());
    }

    public UserEntity getUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return iUserService.getById(userId);
    }

    public void fillUserFields(Long userId, UserDisplayFields target) {
        UserEntity user = getUser(userId);
        if (user != null) {
            target.setUserName(user.getUserName());
            target.setPhone(user.getPhone());
        }
    }

    public String resolveOrderNo(Long orderId) {
        if (orderId == null) {
            return null;
        }
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        return order == null ? null : order.getOrderNo();
    }

    public List<Long> resolveOrderIds(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(RecycleOrder::getOrderNo, orderNo);
        List<RecycleOrder> orders = iRecycleOrderService.list(wrapper);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        return orders.stream().map(RecycleOrder::getId).collect(Collectors.toList());
    }

    public interface UserDisplayFields {
        void setUserName(String userName);

        void setPhone(String phone);
    }
}
