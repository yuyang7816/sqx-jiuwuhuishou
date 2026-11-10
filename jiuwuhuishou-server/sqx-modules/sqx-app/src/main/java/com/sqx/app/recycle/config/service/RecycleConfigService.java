package com.sqx.app.recycle.config.service;

import com.sqx.app.recycle.config.domain.bo.ServiceAreaCheckBo;
import com.sqx.app.recycle.config.domain.vo.RecycleCategoryVo;
import com.sqx.app.recycle.config.domain.vo.RecycleRuleVo;
import com.sqx.app.recycle.config.domain.vo.ServiceAreaCheckVo;

import java.util.List;

/**
 * 回收配置业务服务
 */
public interface RecycleConfigService {

    List<RecycleCategoryVo> listCategories();

    List<RecycleRuleVo> listRules();

    ServiceAreaCheckVo checkServiceArea(ServiceAreaCheckBo checkBo);
}
