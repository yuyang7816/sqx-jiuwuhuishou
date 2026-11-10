package com.sqx.app.base.file.service;

import com.sqx.common.core.domain.Result;
import com.sqx.common.file.domain.UploadRes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author chengming
 * @since 2025/4/29
 */
public interface FileService {
    Result<UploadRes> upload(MultipartFile file);

    /**
     * 代理拉取外部图片（解决 H5 CORS / 小程序 downloadFile 域名限制）
     */
    void proxyImage(String url, HttpServletResponse response);
}
