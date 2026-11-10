package com.sqx.app.brand.service;

import com.sqx.app.brand.domain.bo.BrandCooperationSaveBo;

public interface BrandCooperationService {

    void save(Long userId, BrandCooperationSaveBo saveBo);
}
