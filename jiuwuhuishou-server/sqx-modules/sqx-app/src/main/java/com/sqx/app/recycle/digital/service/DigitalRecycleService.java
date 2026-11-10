package com.sqx.app.recycle.digital.service;

import com.sqx.app.recycle.digital.domain.bo.DigitalEstimateBo;
import com.sqx.app.recycle.digital.domain.vo.DigitalBrandVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalEstimateVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalModelVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSearchResultVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSpecPageVo;

import java.util.List;

/**
 * 数码回收业务服务
 */
public interface DigitalRecycleService {

    List<DigitalBrandVo> listBrands(String scene);

    List<DigitalModelVo> listModels(Long brandId, String keyword);

    List<DigitalSearchResultVo> searchModels(String keyword, String scene, int limit);

    List<String> hotKeywords(String scene);

    DigitalEstimateVo estimate(Long modelId, String condition);

    DigitalEstimateVo estimate(DigitalEstimateBo estimateBo);

    DigitalSpecPageVo getSpecPage(Long modelId, String scene, String brandName, String modelName);
}
