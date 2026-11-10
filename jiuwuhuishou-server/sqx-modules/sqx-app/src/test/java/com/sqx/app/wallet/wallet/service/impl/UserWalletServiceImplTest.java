package com.sqx.app.wallet.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.wallet.transaction.db.service.IUserWalletTransactionService;
import com.sqx.app.wallet.transaction.domain.entity.UserWalletTransaction;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletInfoBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletPageBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletSaveBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletUpdateAmountBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletUpdateBo;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.app.wallet.wallet.domain.vo.UserWalletVo;
import com.sqx.app.wallet.wallet.enums.Operator;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.shiro.utils.UserContextHolder;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserWalletServiceImplTest {

    @Mock
    private IUserWalletService iUserWalletService;
    @Mock
    private IUserWalletTransactionService iUserWalletTransactionService;
    @Mock
    private AdminLookupSupport adminLookupSupport;

    @InjectMocks
    private UserWalletServiceImpl userWalletService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(UserWallet.class, UserWalletTransaction.class);
    }

    @Test
    void crudShouldDelegate() {
        UserWalletSaveBo saveBo = new UserWalletSaveBo();
        saveBo.setUserId(1L);
        userWalletService.saveInfo(saveBo);
        verify(iUserWalletService).save(any(UserWallet.class));

        UserWalletUpdateBo updateBo = new UserWalletUpdateBo();
        updateBo.setId(1L);
        userWalletService.updateInfo(updateBo);
        verify(iUserWalletService).updateById(any(UserWallet.class));

        UserWalletInfoBo infoBo = new UserWalletInfoBo();
        infoBo.setId(1L);
        userWalletService.delete(infoBo);
        verify(iUserWalletService).removeById(1L);
    }

    @Test
    void pageInfoShouldShortCircuitAndMapRecords() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        UserWalletPageBo pageBo = new UserWalletPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");
        assertTrue(userWalletService.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<UserWallet> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iUserWalletService.page(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        pageBo.setUserName(null);
        pageBo.setPhone(null);
        assertTrue(userWalletService.pageInfo(pageBo).getRecords().isEmpty());

        UserWallet wallet = new UserWallet();
        wallet.setId(1L);
        wallet.setUserId(9L);
        page.setRecords(Collections.singletonList(wallet));
        IPage<UserWalletVo> voPage = userWalletService.pageInfo(pageBo);
        assertEquals(1, voPage.getRecords().size());
        verify(adminLookupSupport).fillUserFields(eq(9L), any(UserWalletVo.class));
    }

    @Test
    void infoAndWalletShouldValidate() {
        when(iUserWalletService.getById(1L)).thenReturn(null);
        UserWalletInfoBo infoBo = new UserWalletInfoBo();
        infoBo.setId(1L);
        assertThrows(BizException.class, () -> userWalletService.info(infoBo));

        UserWallet wallet = new UserWallet();
        wallet.setId(1L);
        wallet.setUserId(9L);
        when(iUserWalletService.getById(1L)).thenReturn(wallet);
        assertEquals(9L, userWalletService.info(infoBo).getUserId());

        try (MockedStatic<UserContextHolder> ctx = mockStatic(UserContextHolder.class)) {
            ctx.when(UserContextHolder::getAppUserId).thenReturn(9L);
            when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            assertThrows(BizException.class, () -> userWalletService.wallet());

            when(iUserWalletService.getOne(any(LambdaQueryWrapper.class))).thenReturn(wallet);
            assertEquals(9L, userWalletService.wallet().getUserId());
        }
    }

    @Test
    void updateAmountShouldCoverOperatorsAndTxFailure() {
        UserWalletUpdateAmountBo bo = new UserWalletUpdateAmountBo();
        assertThrows(BizException.class, () -> userWalletService.updateAmount(bo));

        bo.setUserId(1L);
        assertThrows(BizException.class, () -> userWalletService.updateAmount(bo));
        bo.setAmount(BigDecimal.ZERO);
        assertThrows(BizException.class, () -> userWalletService.updateAmount(bo));

        bo.setAmount(new BigDecimal("10"));
        bo.setOperator(Operator.INCR.getValue());
        doThrow(new RuntimeException("fail")).when(iUserWalletTransactionService).save(any(UserWalletTransaction.class));
        userWalletService.updateAmount(bo);
        verify(iUserWalletService).incr(1L, new BigDecimal("10"));

        reset(iUserWalletTransactionService);
        bo.setOperator(Operator.DECR.getValue());
        userWalletService.updateAmount(bo);
        verify(iUserWalletService).decr(1L, new BigDecimal("10"));
        verify(iUserWalletTransactionService).save(any(UserWalletTransaction.class));

        bo.setOperator("other");
        assertThrows(BizException.class, () -> userWalletService.updateAmount(bo));
    }
}
