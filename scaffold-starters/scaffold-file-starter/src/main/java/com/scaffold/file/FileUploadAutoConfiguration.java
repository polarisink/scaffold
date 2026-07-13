package com.scaffold.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

import static com.scaffold.file.FileStorageProperties.FILE_STORAGE_PREFIX;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
@ConditionalOnMissingBean(FileUploadService.class)
@ConditionalOnProperty(prefix = FILE_STORAGE_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class FileUploadAutoConfiguration {

    private final FileStorageProperties properties;

    public FileUploadAutoConfiguration(FileStorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnProperty(prefix = FILE_STORAGE_PREFIX, name = "type", havingValue = "local")
    public FileUploadService localFileService() {
        return new LocalFileService(properties);
    }

    @Bean
    @ConditionalOnClass(S3Client.class)
    @ConditionalOnProperty(prefix = FILE_STORAGE_PREFIX, name = "type", havingValue = "s3")
    public FileUploadService s3FileService() {
        return new S3FileService(properties);
    }
}
