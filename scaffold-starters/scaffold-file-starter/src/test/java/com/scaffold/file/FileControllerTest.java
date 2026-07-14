package com.scaffold.file;

import com.scaffold.file.vo.FileAccessInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    @Test
    void shouldReturnLocalAccessPrefixFromAccessPath() {
        FileStorageProperties.Local local = new FileStorageProperties.Local();
        local.setAccessPath("/assets/**");
        FileStorageProperties properties = new FileStorageProperties(
                false, FileStorageProperties.StorageType.LOCAL, null, local, null);
        FileUploadService fileUploadService = mock(FileUploadService.class);
        when(fileUploadService.getStorageType()).thenReturn("LocalFS");
        FileController controller = new FileController(fileUploadService, properties);

        FileAccessInfo accessInfo = controller.access();

        assertThat(accessInfo.storageType()).isEqualTo("LocalFS");
        assertThat(accessInfo.accessPrefix()).isEqualTo("/assets/");
    }

    @Test
    void shouldPreferConfiguredAccessPrefix() {
        FileStorageProperties.S3 s3 = new FileStorageProperties.S3();
        s3.setEndpoint("https://s3.example.com");
        s3.setAccessKey("access-key");
        s3.setSecretKey("secret-key");
        s3.setBucketName("bucket");
        FileStorageProperties properties = new FileStorageProperties(
                false, FileStorageProperties.StorageType.S3,
                "https://cdn.example.com/scaffold", null, s3);
        FileUploadService fileUploadService = mock(FileUploadService.class);
        when(fileUploadService.getStorageType()).thenReturn("s3");
        FileController controller = new FileController(fileUploadService, properties);

        FileAccessInfo accessInfo = controller.access();

        assertThat(accessInfo.storageType()).isEqualTo("s3");
        assertThat(accessInfo.accessPrefix()).isEqualTo("https://cdn.example.com/scaffold/");
    }
}
