package com.sqx.app.recycle.book.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍回收分类 Mapper
 */
@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {
}
