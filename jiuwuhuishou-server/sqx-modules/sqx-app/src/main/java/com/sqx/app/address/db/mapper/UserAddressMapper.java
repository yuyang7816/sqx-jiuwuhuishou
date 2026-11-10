package com.sqx.app.address.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.app.address.domain.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户地址 Mapper
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
