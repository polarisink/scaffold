package com.scaffold.file;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import static com.scaffold.file.FileStorageProperties.FILE_STORAGE_PREFIX;

@Data
@ConfigurationProperties(prefix = FILE_STORAGE_PREFIX)
public class FileStorageProperties implements InitializingBean {

    public static final String FILE_STORAGE_PREFIX = "file-storage";

    private Boolean enabled = false;
    private StorageType type = StorageType.LOCAL;
    private Local local = new Local();
    private S3 s3 = new S3();

    @Override
    public void afterPropertiesSet() {
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
        private String basePath = "/data/uploads/";
    }

    @Data
    public static class S3 {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucketName;
        private String region = "us-east-1";
    }
}
