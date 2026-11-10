package com.sqx.app.help.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.help.db.mapper.HelpCategoryMapper;
import com.sqx.app.help.db.service.IHelpCategoryService;
import com.sqx.app.help.domain.entity.HelpCategory;
import org.springframework.stereotype.Service;

@Service
public class IHelpCategoryServiceImpl extends ServiceImpl<HelpCategoryMapper, HelpCategory>
        implements IHelpCategoryService {
}
