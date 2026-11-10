package com.sqx.app.pickup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.pickup.domain.bo.PickupTaskCancelBo;
import com.sqx.app.pickup.domain.bo.PickupTaskFinishBo;
import com.sqx.app.pickup.domain.bo.PickupTaskInfoBo;
import com.sqx.app.pickup.domain.bo.PickupTaskPageBo;
import com.sqx.app.pickup.domain.bo.PickupTaskStartBo;
import com.sqx.app.pickup.domain.bo.PickupTaskUpdateActualBo;
import com.sqx.app.pickup.domain.vo.PickupTaskDetailVo;
import com.sqx.app.pickup.domain.vo.PickupTaskFinishVo;
import com.sqx.app.pickup.domain.vo.PickupTaskStatsVo;
import com.sqx.app.pickup.domain.vo.PickupTaskVo;

public interface PickupTaskService {

    PickupTaskStatsVo stats(Long staffId);

    IPage<PickupTaskVo> page(PickupTaskPageBo pageBo);

    PickupTaskDetailVo info(Long staffId, PickupTaskInfoBo infoBo);

    PickupTaskDetailVo start(Long staffId, PickupTaskStartBo startBo);

    PickupTaskFinishVo finish(Long staffId, PickupTaskFinishBo finishBo);

    PickupTaskDetailVo updateActual(Long staffId, PickupTaskUpdateActualBo updateBo);

    PickupTaskDetailVo cancel(Long staffId, PickupTaskCancelBo cancelBo);
}
