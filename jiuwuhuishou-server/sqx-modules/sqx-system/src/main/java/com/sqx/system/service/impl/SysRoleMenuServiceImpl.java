package com.sqx.system.service.impl;


import com.sqx.system.db.entity.SysRoleMenuEntity;
import com.sqx.system.db.mapper.SysRoleMenuDao;
import com.sqx.system.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * 角色与菜单对应关系
 *
 */
@Service("sysRoleMenuService")
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

	/** 前端历史分隔符，禁止写入数据库 */
	private static final long MENU_ID_SEPARATOR = -666666L;

	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(Long roleId, List<Long> menuIdList) {
		//先删除角色与菜单关系
		deleteBatch(new Long[]{roleId});

		if (menuIdList == null || menuIdList.isEmpty()) {
			return;
		}

		Set<Long> menuIds = new LinkedHashSet<>();
		for (Long menuId : menuIdList) {
			if (menuId == null || menuId <= 0 || menuId == MENU_ID_SEPARATOR) {
				continue;
			}
			menuIds.add(menuId);
		}
		if (menuIds.isEmpty()) {
			return;
		}

		List<SysRoleMenuEntity> entities = new ArrayList<>(menuIds.size());
		for (Long menuId : menuIds) {
			SysRoleMenuEntity entity = new SysRoleMenuEntity();
			entity.setRoleId(roleId);
			entity.setMenuId(menuId);
			entities.add(entity);
		}
		// 批量写入，避免远程库逐条 insert 导致前端 12s 超时
		sysRoleMenuDao.insertBatch(entities);
	}

	@Override
	public List<Long> queryMenuIdList(Long roleId) {
		return sysRoleMenuDao.queryMenuIdList(roleId);
	}

	@Override
	public int deleteBatch(Long[] roleIds){
		return sysRoleMenuDao.deleteBatch(roleIds);
	}

}
