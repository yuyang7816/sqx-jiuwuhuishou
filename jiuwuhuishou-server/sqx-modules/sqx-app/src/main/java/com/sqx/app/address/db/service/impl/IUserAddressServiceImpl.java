package com.sqx.app.address.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.address.db.mapper.UserAddressMapper;
import com.sqx.app.address.db.service.IUserAddressService;
import com.sqx.app.address.domain.entity.UserAddress;
import org.springframework.stereotype.Service;

/**
 * 用户地址 DB 服务实现
 */
@Service
public class IUserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService {
}
