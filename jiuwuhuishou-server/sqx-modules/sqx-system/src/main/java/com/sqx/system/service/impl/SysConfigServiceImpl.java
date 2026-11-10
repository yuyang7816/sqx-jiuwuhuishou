package com.sqx.system.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.common.core.domain.Result;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtils;
import com.sqx.common.mybatis.utils.Query;
import com.sqx.common.redis.utils.RedisKeys;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.system.db.entity.SysConfigEntity;
import com.sqx.system.db.service.ISysConfigService;
import com.sqx.system.domain.res.SysConfigListRes;
import com.sqx.system.service.SysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysConfigServiceImpl implements SysConfigService {
	private static final String DEFAULT_PARAM_TYPE = "default";
	private static final String DEFAULT_PARAM_TYPE_DESC = "其他配置";

	@Autowired
	private ISysConfigService sysConfigService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String paramKey = (String)params.get("paramKey");

		IPage<SysConfigEntity> page = sysConfigService.page(
			new Query<SysConfigEntity>().getPage(params),
			new QueryWrapper<SysConfigEntity>()
				.like(StringUtils.isNotBlank(paramKey),"param_key", paramKey)
				.eq("status", 1)
		);

		return new PageUtils(page);
	}

	@Override
	public void saveConfig(SysConfigEntity config) {
		SysConfigEntity sysConfig = sysConfigService.getOne(new LambdaQueryWrapper<SysConfigEntity>().eq(SysConfigEntity::getParamKey, config.getParamKey()));
		if (Objects.nonNull(sysConfig)) {
			throw new BizException("系统配置已存在");
		}

		sysConfigService.save(config);

		RedisUtil.setCacheObject(RedisKeys.SYS_CONFIG_KEY + config.getParamKey(), JSONObject.toJSONString(config));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysConfigEntity config) {
		SysConfigEntity sysConfig = sysConfigService.getOne(new LambdaQueryWrapper<SysConfigEntity>()
				.eq(SysConfigEntity::getParamKey, config.getParamKey())
				.ne(SysConfigEntity::getId, config.getId()));
		if (Objects.nonNull(sysConfig)) {
			throw new BizException("系统配置已存在");
		}

		sysConfigService.updateById(config);

		if (RedisUtil.hasKey(RedisKeys.SYS_CONFIG_KEY + config.getParamKey())) {
			RedisUtil.deleteObject(RedisKeys.SYS_CONFIG_KEY + config.getParamKey());
			RedisUtil.setCacheObject(RedisKeys.SYS_CONFIG_KEY + config.getParamKey(), JSONObject.toJSONString(config));
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] ids) {
		for(Long id : ids){
			SysConfigEntity config = sysConfigService.getById(id);

			if (RedisUtil.hasKey(RedisKeys.SYS_CONFIG_KEY + config.getParamKey())) {
				RedisUtil.deleteObject(RedisKeys.SYS_CONFIG_KEY + config.getParamKey());
			}
		}

		sysConfigService.removeByIds(Arrays.asList(ids));
	}

	@Override
	public Result<List<SysConfigListRes>> listInfo(Map<String, Object> params) {
		List<SysConfigEntity> list = sysConfigService.list(new LambdaQueryWrapper<SysConfigEntity>().eq(SysConfigEntity::getStatus, 1));
		Map<String, List<SysConfigEntity>> map = list.stream().collect(Collectors.groupingBy(
			item -> StringUtils.defaultIfBlank(item.getParamType(), DEFAULT_PARAM_TYPE)
		));

		List<SysConfigListRes> sysConfigListResList = new ArrayList<>();
		map.forEach((key, value) -> {
			SysConfigListRes sysConfigListRes = new SysConfigListRes();
			sysConfigListRes.setParamType(key);
			sysConfigListRes.setParamTypeDesc(resolveParamTypeDesc(value));
			sysConfigListRes.setSysConfigEntityList(value);

			sysConfigListResList.add(sysConfigListRes);
		});

		return Result.success(sysConfigListResList);
	}

	private String resolveParamTypeDesc(List<SysConfigEntity> configs) {
		return configs.stream()
			.map(SysConfigEntity::getParamTypeDesc)
			.filter(StringUtils::isNotBlank)
			.findFirst()
			.orElse(DEFAULT_PARAM_TYPE_DESC);
	}

	@Override
	public Result<Void> refresh() {
		RedisUtil.deleteKeys(RedisKeys.SYS_CONFIG_KEY);

		sysConfigService.loadSysConfig();
		return Result.success();
	}

	@Override
	public Result info(Long id) {
		SysConfigEntity config = sysConfigService.getById(id);
		return Result.success(config);
	}
}
