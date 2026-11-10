package com.sqx.app.recycle.book.support;

import com.sqx.app.recycle.book.domain.vo.BookCategoryVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 书籍回收分类默认数据
 */
public final class BookCategoryDefaults {

    private BookCategoryDefaults() {
    }

    public static List<BookCategoryVo> defaultCategories() {
        List<BookCategoryVo> list = new ArrayList<>();
        list.add(build(1L, "文学小说", "file-text", 1));
        list.add(build(2L, "教材教辅", "order", 2));
        list.add(build(3L, "儿童绘本", "gift", 3));
        list.add(build(4L, "杂志期刊", "bookmark", 4));
        return list;
    }

    private static BookCategoryVo build(Long id, String name, String icon, int sort) {
        BookCategoryVo vo = new BookCategoryVo();
        vo.setId(id);
        vo.setName(name);
        vo.setIcon(icon);
        vo.setSort(sort);
        vo.setState(1);
        return vo;
    }
}
