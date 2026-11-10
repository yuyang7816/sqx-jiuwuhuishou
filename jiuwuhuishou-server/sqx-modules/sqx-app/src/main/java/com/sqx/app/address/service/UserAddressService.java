package com.sqx.app.address.service;

import com.sqx.app.address.domain.bo.UserAddressDeleteBo;
import com.sqx.app.address.domain.bo.UserAddressSaveBo;
import com.sqx.app.address.domain.bo.UserAddressSetDefaultBo;
import com.sqx.app.address.domain.bo.UserAddressUpdateBo;
import com.sqx.app.address.domain.vo.UserAddressVo;

import java.util.List;

/**
 * 用户地址业务服务
 */
public interface UserAddressService {

    List<UserAddressVo> list(Long userId);

    void save(Long userId, UserAddressSaveBo saveBo);

    void update(Long userId, UserAddressUpdateBo updateBo);

    void delete(Long userId, UserAddressDeleteBo deleteBo);

    void setDefault(Long userId, UserAddressSetDefaultBo setDefaultBo);
}
