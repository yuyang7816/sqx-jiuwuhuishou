package com.sqx.app.recycle.book.service;

import com.sqx.app.recycle.book.domain.vo.BookCategoryVo;
import com.sqx.app.recycle.book.domain.vo.BookEstimateConfigVo;
import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;

import java.util.List;

/**
 * 书籍回收业务服务
 */
public interface BookRecycleService {

    List<BookCategoryVo> listCategories();

    /**
     * 书籍预约预估重量选项列表（上架）
     */
    List<ClothesWeightOptionVo> listWeightOptions();

    BookEstimateConfigVo getEstimateConfig();
}
