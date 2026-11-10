package com.sqx.app.admin.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminLookupSupportTest {

    @Mock
    private IUserService iUserService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;

    @InjectMocks
    private AdminLookupSupport adminLookupSupport;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserEntity.class, RecycleOrder.class);
    }

    @Test
    void resolveUserIdsShouldHandleBlankEmptyAndResults() {
        assertNull(adminLookupSupport.resolveUserIds(null, null));
        assertNull(adminLookupSupport.resolveUserIds(" ", " "));

        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(adminLookupSupport.resolveUserIds("张三", null).isEmpty());

        UserEntity user1 = buildUser(1L, "张三", "13800001111");
        UserEntity user2 = buildUser(2L, "李四", "13800002222");
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(user1, user2));

        List<Long> ids = adminLookupSupport.resolveUserIds("张", "138");
        assertEquals(Arrays.asList(1L, 2L), ids);
    }

    @Test
    void getUserShouldReturnNullForMissingId() {
        assertNull(adminLookupSupport.getUser(null));

        UserEntity user = buildUser(9L, "王五", "13900000000");
        when(iUserService.getById(9L)).thenReturn(user);
        assertEquals(user, adminLookupSupport.getUser(9L));
    }

    @Test
    void fillUserFieldsShouldSkipOrPopulate() {
        AdminLookupSupport.UserDisplayFields target = new AdminLookupSupport.UserDisplayFields() {
            private String userName;
            private String phone;

            @Override
            public void setUserName(String userName) {
                this.userName = userName;
            }

            @Override
            public void setPhone(String phone) {
                this.phone = phone;
            }
        };

        adminLookupSupport.fillUserFields(null, target);

        when(iUserService.getById(5L)).thenReturn(null);
        adminLookupSupport.fillUserFields(5L, target);

        UserEntity user = buildUser(5L, "赵六", "13700000000");
        when(iUserService.getById(5L)).thenReturn(user);
        adminLookupSupport.fillUserFields(5L, new AdminLookupSupport.UserDisplayFields() {
            @Override
            public void setUserName(String userName) {
                assertEquals("赵六", userName);
            }

            @Override
            public void setPhone(String phone) {
                assertEquals("13700000000", phone);
            }
        });
    }

    @Test
    void resolveOrderNoShouldHandleNullAndExisting() {
        assertNull(adminLookupSupport.resolveOrderNo(null));

        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        assertNull(adminLookupSupport.resolveOrderNo(10L));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setOrderNo("RO20260716001");
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        assertEquals("RO20260716001", adminLookupSupport.resolveOrderNo(10L));
    }

    @Test
    void resolveOrderIdsShouldHandleBlankEmptyAndResults() {
        assertNull(adminLookupSupport.resolveOrderIds(null));
        assertNull(adminLookupSupport.resolveOrderIds(" "));

        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(adminLookupSupport.resolveOrderIds("RO").isEmpty());

        RecycleOrder order1 = new RecycleOrder();
        order1.setId(11L);
        order1.setOrderNo("RO001");
        RecycleOrder order2 = new RecycleOrder();
        order2.setId(12L);
        order2.setOrderNo("RO002");
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(order1, order2));

        assertEquals(Arrays.asList(11L, 12L), adminLookupSupport.resolveOrderIds("RO"));
    }

    private UserEntity buildUser(Long userId, String userName, String phone) {
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPhone(phone);
        return user;
    }
}
