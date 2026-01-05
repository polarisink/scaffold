// src/main/java/.../FileUploadAutoConfiguration.java
package com.scaffold.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

import static com.scaffold.file.FileUploadProperties.FILE_STORAGE_PREFIX;

/**
 * 文件上传服务的自动配置类
 */
@Configuration
@EnableConfigurationProperties(FileUploadProperties.class)
@ConditionalOnMissingBean(FileUploadService.class) // 如果用户应用中已经定义了 FileUploadService bean，则不自动配置
public class FileUploadAutoConfiguration {

    private final FileUploadProperties properties;

    public FileUploadAutoConfiguration(FileUploadProperties properties) {
        this.properties = properties;
    }

    // --- Local Storage Configuration ---
    @Bean
    @ConditionalOnProperty(prefix = FILE_STORAGE_PREFIX, name = "type", havingValue = "local", matchIfMissing = true)
    public FileUploadService localFileService() {
        return new LocalFileService(properties);
    }

    // --- MinIO Configuration ---
    // 只有 MinioClient 类存在于 classpath 且配置类型为 minio 时才生效
    @Bean
    @ConditionalOnClass(S3Client.class)
    @ConditionalOnProperty(prefix = FILE_STORAGE_PREFIX, name = "type", havingValue = "s3")
    public FileUploadService s3FileService() {
        return new S3FileService(properties);
    }

}