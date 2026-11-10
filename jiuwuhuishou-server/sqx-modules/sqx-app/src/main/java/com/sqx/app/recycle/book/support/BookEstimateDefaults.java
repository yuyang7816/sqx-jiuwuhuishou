package com.sqx.app.recycle.book.support;

import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 书籍预估重量默认配置（表 book_weight_option 为空时回退）
 */
public final class BookEstimateDefaults {

    private BookEstimateDefaults() {
    }

    public static List<ClothesWeightOptionVo> defaultWeightOptions() {
        List<ClothesWeightOptionVo> options = new ArrayList<>();
        options.add(buildOption("5-15kg", "日常整理", new BigDecimal("5.50"), 1));
        options.add(buildOption("15-30kg", "搬家清理", new BigDecimal("12.00"), 2));
        options.add(buildOption(">30kg", "大批量回收", new BigDecimal("22.00"), 3));
        return options;
    }

    private static ClothesWeightOptionVo buildOption(String title, String desc, BigDecimal estimateAmount, int sort) {
        ClothesWeightOptionVo option = new ClothesWeightOptionVo();
        option.setTitle(title);
        option.setDesc(desc);
        option.setEstimateAmount(estimateAmount);
        option.setSort(sort);
        return option;
    }
}
