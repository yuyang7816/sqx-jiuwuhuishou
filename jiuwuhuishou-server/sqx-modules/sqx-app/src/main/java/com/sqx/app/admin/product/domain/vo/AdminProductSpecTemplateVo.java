package com.sqx.app.admin.product.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminProductSpecTemplateVo {

    private String configKey;
    private String recycleCategoryCode;
    private String categoryName;
    private List<AdminProductSpecTemplateSectionVo> sections;

    @Data
    public static class AdminProductSpecTemplateSectionVo {
        private String key;
        private String label;
        private List<String> options;
    }
}
