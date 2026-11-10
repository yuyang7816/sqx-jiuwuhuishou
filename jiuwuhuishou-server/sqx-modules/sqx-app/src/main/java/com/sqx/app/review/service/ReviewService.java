package com.sqx.app.review.service;

import com.sqx.app.review.domain.bo.ReviewInfoBo;
import com.sqx.app.review.domain.bo.ReviewSaveBo;
import com.sqx.app.review.domain.vo.ReviewInfoVo;

public interface ReviewService {

    ReviewInfoVo info(Long userId, ReviewInfoBo infoBo);

    ReviewInfoVo save(Long userId, ReviewSaveBo saveBo);
}
