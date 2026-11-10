package com.sqx.app.recycle.book.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍预约预估重量选项 Mapper
 */
@Mapper
public interface BookWeightOptionMapper extends BaseMapper<BookWeightOption> {
}
