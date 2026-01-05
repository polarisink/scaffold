// src/main/java/.../properties/FileUploadProperties.java
package com.scaffold.file;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import static com.scaffold.file.FileUploadProperties.FILE_STORAGE_PREFIX;

@Data
@ConfigurationProperties(prefix = FILE_STORAGE_PREFIX)
public class FileUploadProperties implements InitializingBean {

    public static final String FILE_STORAGE_PREFIX = "file-storage";

    /**
     * 存储类型: local, s3
     */
    private StorageType type = StorageType.LOCAL;
    // --- Local Storage Configuration ---
    private Local local = new Local();
    // --- s3 Configuration ---
    private S3 s3 = new S3();

    @Override
    public void afterPropertiesSet() throws Exception {
        switch (type) {
            case LOCAL -> {
            }
            case S3 -> {
                Assert.hasText(s3.endpoint, "endpoint不能为空");
                Assert.hasText(s3.accessKey, "accessKey不能为空");
                Assert.hasText(s3.secretKey, "secretKey不能为空");
                Assert.hasText(s3.bucketName, "bucketName不能为空");
            }
        }
    }

    public enum StorageType {
        LOCAL,
        S3,
    }

    @Data
    public static class Local {
        private String accessPath = "/files/**";
        /**
         * 文件上传的本地存储路径
         */
        private String basePath = "/data/uploads/";

        // Getters and Setters
    }

    @Data
    public static class S3 {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucketName;
        private String region = "us-east-1"; // S3 兼容存储通常需要一个 region
    }
}