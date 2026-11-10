package com.sqx.app.wallet.wallet.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.wallet.wallet.db.mapper.UserWalletMapper;
import com.sqx.app.wallet.wallet.db.service.IUserWalletService;
import com.sqx.app.wallet.wallet.domain.entity.UserWallet;
import com.sqx.common.core.exception.BizException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * <p>
 * 用户钱包主表 服务实现类
 * </p>
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-13 15:09:58
 */
@Service
public class IUserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements IUserWalletService {

    @Resource
    private UserWalletMapper userWalletMapper;

    @Override
    public void incr(Long userId, BigDecimal amount) {
        ensureWalletExists(userId);
        userWalletMapper.incr(userId, amount);
    }

    @Override
    public void decr(Long userId, BigDecimal amount) {
        UserWallet wallet = ensureWalletExists(userId);
        BigDecimal balance = wallet.getAmount() == null ? BigDecimal.ZERO : wallet.getAmount();
        if (balance.compareTo(amount) < 0) {
            throw new BizException("余额不足");
        }
        int affected = userWalletMapper.decr(userId, amount);
        if (affected <= 0) {
            throw new BizException("余额不足");
        }
    }

    private UserWallet ensureWalletExists(Long userId) {
        UserWallet wallet = getOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));
        if (Objects.nonNull(wallet)) {
            return wallet;
        }
        wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setAmount(BigDecimal.ZERO);
        save(wallet);
        return wallet;
    }
}
