package com.sqx.system.db.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.system.db.entity.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与菜单对应关系
 *
 */
@Mapper
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenuEntity> {
	
	/**
	 * 根据角色ID，获取菜单ID列表
	 */
	List<Long> queryMenuIdList(Long roleId);

	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);

	/**
	 * 批量插入角色菜单关系
	 */
	int insertBatch(@Param("list") List<SysRoleMenuEntity> list);
}
