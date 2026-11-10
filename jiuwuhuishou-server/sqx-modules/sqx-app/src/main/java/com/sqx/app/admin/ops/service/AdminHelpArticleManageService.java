package com.sqx.app.admin.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.help.domain.entity.HelpArticle;

public interface AdminHelpArticleManageService {

    void saveInfo(AdminHelpArticleSaveBo saveBo);

    IPage<HelpArticle> pageInfo(AdminHelpArticlePageBo pageBo);

    HelpArticle info(AdminHelpArticleInfoBo infoBo);

    void updateInfo(AdminHelpArticleUpdateBo updateBo);

    void delete(AdminHelpArticleInfoBo infoBo);
}
