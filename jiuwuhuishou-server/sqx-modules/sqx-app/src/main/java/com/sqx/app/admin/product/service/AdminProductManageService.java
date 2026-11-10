package com.sqx.app.admin.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.product.domain.bo.*;
import com.sqx.app.admin.product.domain.vo.AdminProductBrandVo;
import com.sqx.app.admin.product.domain.vo.AdminProductCategoryVo;
import com.sqx.app.admin.product.domain.vo.AdminProductModelVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecTemplateVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecVo;

public interface AdminProductManageService {

    void saveCategory(AdminProductCategorySaveBo saveBo);

    IPage<AdminProductCategoryVo> pageCategory(AdminProductCategoryPageBo pageBo);

    AdminProductCategoryVo infoCategory(AdminProductIdBo infoBo);

    void updateCategory(AdminProductCategoryUpdateBo updateBo);

    void deleteCategory(AdminProductIdBo infoBo);

    void saveBrand(AdminProductBrandSaveBo saveBo);

    IPage<AdminProductBrandVo> pageBrand(AdminProductBrandPageBo pageBo);

    AdminProductBrandVo infoBrand(AdminProductIdBo infoBo);

    void updateBrand(AdminProductBrandUpdateBo updateBo);

    void deleteBrand(AdminProductIdBo infoBo);

    void saveModel(AdminProductModelSaveBo saveBo);

    IPage<AdminProductModelVo> pageModel(AdminProductModelPageBo pageBo);

    AdminProductModelVo infoModel(AdminProductIdBo infoBo);

    void updateModel(AdminProductModelUpdateBo updateBo);

    void deleteModel(AdminProductIdBo infoBo);

    void saveSpec(AdminProductSpecSaveBo saveBo);

    IPage<AdminProductSpecVo> pageSpec(AdminProductSpecPageBo pageBo);

    AdminProductSpecVo infoSpec(AdminProductIdBo infoBo);

    void updateSpec(AdminProductSpecUpdateBo updateBo);

    void deleteSpec(AdminProductIdBo infoBo);

    AdminProductSpecTemplateVo loadSpecTemplate(AdminProductSpecTemplateBo templateBo);
}
