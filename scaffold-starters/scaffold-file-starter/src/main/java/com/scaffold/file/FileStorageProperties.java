package com.scaffold.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import static com.scaffold.file.FileStorageProperties.FILE_STORAGE_PREFIX;

/**
 * 文件存储配置
 *
 * @param enabled      是否启用
 * @param type         存储类型
 * @param accessPrefix 访问前缀
 * @param local        本地存储配置
 * @param s3           s3配置
 */
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

    @Data
    public static class Local {
        // note 需要手动把这个路径加到权限和日志忽略列表中
        private String accessPath = "/files/**";
        /**
         * 本地存储路径
         */
        private String basePath = "/data/uploads/";
    }

    /**
     * s3配置
     */
    @Data
    public static class S3 {
        /**
         * 路径
         */
        private String endpoint;
        /**
         * 用户名
         */
        private String accessKey;
        /**
         * 密码
         */
        private String secretKey;
        /**
         * 存储桶名
         */
        private String bucketName;
        /**
         * 区域
         */
        private String region = "us-east-1";
    }
}
