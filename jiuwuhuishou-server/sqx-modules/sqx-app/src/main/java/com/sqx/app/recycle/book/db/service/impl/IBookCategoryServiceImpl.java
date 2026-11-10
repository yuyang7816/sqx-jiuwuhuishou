package com.sqx.app.recycle.book.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.recycle.book.db.mapper.BookCategoryMapper;
import com.sqx.app.recycle.book.db.service.IBookCategoryService;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import org.springframework.stereotype.Service;

/**
 * 书籍回收分类 DB 服务实现
 */
@Service
public class IBookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements IBookCategoryService {
}
