package com.sqx.app.recycle.book.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.book.db.mapper.BookWeightOptionMapper;
import com.sqx.app.recycle.book.db.service.IBookWeightOptionService;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
import org.springframework.stereotype.Service;

/**
 * 书籍预约预估重量选项 DB 服务实现
 */
@Service
public class IBookWeightOptionServiceImpl extends ServiceImpl<BookWeightOptionMapper, BookWeightOption>
        implements IBookWeightOptionService {
}
