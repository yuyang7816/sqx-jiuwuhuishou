package com.sqx.app.admin.pickup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskAssignBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskFinishBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskInfoBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskPageBo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskDetailVo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskVo;

public interface AdminPickupManageService {

    IPage<AdminPickupTaskVo> pageInfo(AdminPickupTaskPageBo pageBo);

    AdminPickupTaskDetailVo info(AdminPickupTaskInfoBo infoBo);

    void assign(AdminPickupTaskAssignBo assignBo);

    void reassign(AdminPickupTaskAssignBo assignBo);

    void finish(AdminPickupTaskFinishBo finishBo);
}
