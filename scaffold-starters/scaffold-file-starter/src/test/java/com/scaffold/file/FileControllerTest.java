package com.scaffold.file;

import com.scaffold.file.vo.FileAccessInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    @Test
    void shouldReturnLocalAccessPrefixFromAccessPath() {
        FileStorageProperties properties = new FileStorageProperties();
        properties.setType(FileStorageProperties.StorageType.LOCAL);
        properties.getLocal().setAccessPath("/assets/**");
        FileUploadService fileUploadService = mock(FileUploadService.class);
        when(fileUploadService.getStorageType()).thenReturn("LocalFS");
        FileController controller = new FileController(fileUploadService, properties);

        FileAccessInfo accessInfo = controller.access();

        assertThat(accessInfo.storageType()).isEqualTo("LocalFS");
        assertThat(accessInfo.accessPrefix()).isEqualTo("/assets/");
    }

    @Test
    void shouldPreferConfiguredAccessPrefix() {
        FileStorageProperties properties = new FileStorageProperties();
        properties.setType(FileStorageProperties.StorageType.S3);
        properties.setAccessPrefix("https://cdn.example.com/scaffold");
        FileUploadService fileUploadService = mock(FileUploadService.class);
        when(fileUploadService.getStorageType()).thenReturn("s3");
        FileController controller = new FileController(fileUploadService, properties);

        FileAccessInfo accessInfo = controller.access();

        assertThat(accessInfo.storageType()).isEqualTo("s3");
        assertThat(accessInfo.accessPrefix()).isEqualTo("https://cdn.example.com/scaffold/");
    }
}
