package com.sqx.app.base.file.service.impl;

import com.sqx.app.base.file.service.FileService;
import com.sqx.common.core.domain.Result;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.http.OkHttpUtil;
import com.sqx.common.file.domain.UploadRes;
import com.sqx.common.file.factory.FileStorageFactory;
import com.sqx.common.file.storage.IFileStorage;
import com.sqx.common.redis.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author chengming
 * @since 2025/4/29
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final int MAX_PROXY_BYTES = 5 * 1024 * 1024;

    /**
     * 允许代理的图片域名后缀（OSS / 微信头像等）
     */
    private static final List<String> ALLOWED_HOST_SUFFIXES = Arrays.asList(
            ".aliyuncs.com",
            ".aliyun.com",
            ".qlogo.cn",
            "qlogo.cn",
            ".qpic.cn",
            "qpic.cn"
    );

    @Override
    public Result<UploadRes> upload(MultipartFile file) {
        String fileStorageType = ConfigUtil.getConfigValue("file_storage_type");
        IFileStorage fileStorage = FileStorageFactory.getFileStorage(fileStorageType);
        UploadRes uploadRes = fileStorage.upload(file);

        return Result.success(uploadRes);
    }

    @Override
    public void proxyImage(String url, HttpServletResponse response) {
        String safeUrl = validateProxyUrl(url);
        try {
            byte[] bytes = OkHttpUtil.doGetBytes(safeUrl);
            if (bytes == null || bytes.length == 0) {
                throw new BizException("图片内容为空");
            }
            if (bytes.length > MAX_PROXY_BYTES) {
                throw new BizException("图片过大，无法代理");
            }
            String contentType = guessContentType(safeUrl);
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "public, max-age=86400");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (BizException e) {
            throw e;
        } catch (IOException e) {
            log.warn("图片代理写出失败: {}", e.getMessage());
            throw new BizException("图片代理失败");
        } catch (Exception e) {
            log.warn("图片代理失败 url={}: {}", safeUrl, e.getMessage());
            throw new BizException("图片代理失败");
        }
    }

    private String validateProxyUrl(String rawUrl) {
        if (StringUtils.isBlank(rawUrl)) {
            throw new BizException("图片地址不能为空");
        }
        String url = rawUrl.trim();
        URI uri;
        try {
            uri = URI.create(url);
        } catch (Exception e) {
            throw new BizException("非法图片地址");
        }
        String scheme = StringUtils.lowerCase(uri.getScheme());
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new BizException("仅支持 http/https 图片地址");
        }
        String host = StringUtils.lowerCase(uri.getHost());
        if (StringUtils.isBlank(host)) {
            throw new BizException("非法图片域名");
        }
        if (!isAllowedHost(host)) {
            throw new BizException("不支持的图片域名");
        }
        try {
            InetAddress address = InetAddress.getByName(host);
            if (address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress()
                    || address.isMulticastAddress()) {
                throw new BizException("非法图片地址");
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException("图片域名解析失败");
        }
        return url;
    }

    private boolean isAllowedHost(String host) {
        for (String suffix : ALLOWED_HOST_SUFFIXES) {
            if (host.equals(suffix) || host.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private String guessContentType(String url) {
        String lower = url.toLowerCase(Locale.ROOT);
        if (lower.contains(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        }
        if (lower.contains(".gif")) {
            return MediaType.IMAGE_GIF_VALUE;
        }
        if (lower.contains(".webp")) {
            return "image/webp";
        }
        if (lower.contains(".jpg") || lower.contains(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        }
        try {
            String path = new URL(url).getPath();
            String probed = java.net.URLConnection.guessContentTypeFromName(path);
            if (StringUtils.isNotBlank(probed)) {
                return probed;
            }
        } catch (Exception ignored) {
            /* ignore */
        }
        return MediaType.IMAGE_JPEG_VALUE;
    }
}
