package com.sqx.app.help.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.help.db.mapper.HelpArticleMapper;
import com.sqx.app.help.db.service.IHelpArticleService;
import com.sqx.app.help.domain.entity.HelpArticle;
import org.springframework.stereotype.Service;

@Service
public class IHelpArticleServiceImpl extends ServiceImpl<HelpArticleMapper, HelpArticle>
        implements IHelpArticleService {
}
