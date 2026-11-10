package com.sqx.app.admin.orderflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewReplyBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminReviewVo;

public interface AdminReviewManageService {

    IPage<AdminReviewVo> pageInfo(AdminReviewPageBo pageBo);

    AdminReviewVo info(AdminReviewInfoBo infoBo);

    void reply(AdminReviewReplyBo replyBo);
}
