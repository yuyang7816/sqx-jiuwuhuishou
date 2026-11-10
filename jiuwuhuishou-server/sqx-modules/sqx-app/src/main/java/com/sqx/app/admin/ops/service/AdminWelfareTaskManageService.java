package com.sqx.app.admin.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.welfare.domain.entity.WelfareTask;

public interface AdminWelfareTaskManageService {

    void saveInfo(AdminWelfareTaskSaveBo saveBo);

    IPage<WelfareTask> pageInfo(AdminWelfareTaskPageBo pageBo);

    WelfareTask info(AdminWelfareTaskInfoBo infoBo);

    void updateInfo(AdminWelfareTaskUpdateBo updateBo);

    void delete(AdminWelfareTaskInfoBo infoBo);
}
