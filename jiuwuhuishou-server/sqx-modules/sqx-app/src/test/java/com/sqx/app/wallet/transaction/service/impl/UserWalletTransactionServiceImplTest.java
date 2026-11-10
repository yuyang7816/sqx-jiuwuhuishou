package com.sqx.app.wallet.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionInfoBo;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionPageBo;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionSaveBo;
import com.sqx.app.wallet.transaction.domain.bo.UserWalletTransactionUpdateBo;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.transaction.domain.vo.UserWalletTransactionVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserWalletTransactionServiceImplTest {

    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private UserWalletTransactionServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserWalletTransaction.class);
    }

    @Test
    void crudAndInfo() {
        service.saveInfo(new UserWalletTransactionSaveBo());
        verify(iUserWalletTransactionService).save(any(UserWalletTransaction.class));

        UserWalletTransactionUpdateBo updateBo = new UserWalletTransactionUpdateBo();
        updateBo.setId(1L);
        service.updateInfo(updateBo);
        verify(iUserWalletTransactionService).updateById(any(UserWalletTransaction.class));

        UserWalletTransactionInfoBo infoBo = new UserWalletTransactionInfoBo();
        infoBo.setId(1L);
        when(iUserWalletTransactionService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));

        UserWalletTransaction tx = new UserWalletTransaction();
        tx.setId(1L);
        tx.setUserId(9L);
        when(iUserWalletTransactionService.getById(1L)).thenReturn(tx);
        assertEquals(9L, service.info(infoBo).getUserId());

        service.delete(infoBo);
        verify(iUserWalletTransactionService).removeById(1L);
    }

    @Test
    void pageInfoBranches() {
        when(adminLookupSupport.resolveUserIds("x", "y")).thenReturn(Collections.emptyList());
        UserWalletTransactionPageBo pageBo = new UserWalletTransactionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("x");
        pageBo.setPhone("y");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<UserWalletTransaction> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iUserWalletTransactionService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        pageBo.setUserId(1L);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        UserWalletTransaction tx = new UserWalletTransaction();
        tx.setUserId(1L);
        page.setRecords(Collections.singletonList(tx));
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        verify(adminLookupSupport).fillUserFields(eq(1L), any(UserWalletTransactionVo.class));
    }
}
