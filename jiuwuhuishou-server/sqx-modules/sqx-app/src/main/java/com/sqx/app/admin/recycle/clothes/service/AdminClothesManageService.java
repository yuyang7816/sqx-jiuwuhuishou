package com.sqx.app.admin.recycle.clothes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryPageBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategorySaveBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryUpdateBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;

public interface AdminClothesManageService {

    void saveCategory(AdminClothesCategorySaveBo saveBo);

    IPage<ClothesCategory> pageCategory(AdminClothesCategoryPageBo pageBo);

    ClothesCategory infoCategory(AdminRecycleIdBo infoBo);

    void updateCategory(AdminClothesCategoryUpdateBo updateBo);

    void deleteCategory(AdminRecycleIdBo infoBo);
}
