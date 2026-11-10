package com.sqx.app.help.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.help.domain.bo.HelpArticleInfoBo;
import com.sqx.app.help.domain.bo.HelpArticlePageBo;
import com.sqx.app.help.domain.vo.HelpArticleDetailVo;
import com.sqx.app.help.domain.vo.HelpArticleVo;

public interface HelpArticleService {

    IPage<HelpArticleVo> page(HelpArticlePageBo pageBo);

    HelpArticleDetailVo info(HelpArticleInfoBo infoBo);
}
