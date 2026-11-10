package com.sqx.app.base.file.service.impl;

import com.sqx.common.core.domain.Result;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.file.domain.UploadRes;
import com.sqx.common.file.factory.FileStorageFactory;
import com.sqx.common.file.storage.IFileStorage;
import com.sqx.common.redis.utils.ConfigUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void uploadShouldResolveStorageAndReturnSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        IFileStorage fileStorage = mock(IFileStorage.class);
        UploadRes uploadRes = new UploadRes();
        uploadRes.setPath("https://cdn.example.com/a.png");

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class);
             MockedStatic<FileStorageFactory> factory = mockStatic(FileStorageFactory.class)) {
            config.when(() -> ConfigUtil.getConfigValue("file_storage_type")).thenReturn("local");
            factory.when(() -> FileStorageFactory.getFileStorage("local")).thenReturn(fileStorage);
            when(fileStorage.upload(file)).thenReturn(uploadRes);

            Result<UploadRes> result = fileService.upload(file);

            assertNotNull(result);
            assertEquals("https://cdn.example.com/a.png", result.getData().getPath());
            verify(fileStorage).upload(file);
        }
    }

    @Test
    void proxyImageShouldRejectUnsupportedHost() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        assertThrows(BizException.class, () -> fileService.proxyImage("https://evil.example.com/a.png", response));
    }

    @Test
    void proxyImageShouldRejectBlankUrl() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        assertThrows(BizException.class, () -> fileService.proxyImage(" ", response));
    }
}
