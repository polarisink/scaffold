package com.scaffold.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import static com.scaffold.file.FileStorageProperties.FILE_STORAGE_PREFIX;

@ConfigurationProperties(prefix = FILE_STORAGE_PREFIX)
public record FileStorageProperties(Boolean enabled, StorageType type, String accessPrefix,
                                    Local local, S3 s3) {

    public static final String FILE_STORAGE_PREFIX = "scaffold.file-storage";

    public FileStorageProperties {
        enabled = enabled != null && enabled;
        type = type == null ? StorageType.LOCAL : type;
        local = local == null ? new Local() : local;
        s3 = s3 == null ? new S3() : s3;
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

    public Boolean getEnabled() { return enabled; }
    public StorageType getType() { return type; }
    public String getAccessPrefix() { return accessPrefix; }
    public Local getLocal() { return local; }
    public S3 getS3() { return s3; }



    @Data
    public static class Local {
        // note 需要手动把这个路径加到权限和日志忽略列表中
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
