package com.sqx.app.admin.recycle.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategorySaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryUpdateBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionSaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionUpdateBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;

public interface AdminBookManageService {

    void saveCategory(AdminBookCategorySaveBo saveBo);

    IPage<BookCategory> pageCategory(AdminBookCategoryPageBo pageBo);

    BookCategory infoCategory(AdminRecycleIdBo infoBo);

    void updateCategory(AdminBookCategoryUpdateBo updateBo);

    void deleteCategory(AdminRecycleIdBo infoBo);

    void saveWeightOption(AdminBookWeightOptionSaveBo saveBo);

    IPage<BookWeightOption> pageWeightOption(AdminBookWeightOptionPageBo pageBo);

    BookWeightOption infoWeightOption(AdminRecycleIdBo infoBo);

    void updateWeightOption(AdminBookWeightOptionUpdateBo updateBo);

    void deleteWeightOption(AdminRecycleIdBo infoBo);
}
