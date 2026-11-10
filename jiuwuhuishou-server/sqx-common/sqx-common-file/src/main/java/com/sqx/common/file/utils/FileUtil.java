package com.sqx.common.file.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author chengming
 * @since 2025/4/29
 */
public class FileUtil {

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }

        int dotIndex = filename.lastIndexOf(46);
        return -1 == dotIndex ? "" : filename.substring(dotIndex + 1);
    }
}
