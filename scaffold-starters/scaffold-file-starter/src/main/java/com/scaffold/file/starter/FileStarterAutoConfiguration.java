package com.scaffold.file.starter;

import com.scaffold.file.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({FileUploadAutoConfiguration.class, FileAccessWebConfiguration.class})
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStarterAutoConfiguration {

    @Bean
    @ConditionalOnBean(FileUploadService.class)
    public FileStorageController fileController(FileUploadService fileUploadService) {
        return new FileStorageController(fileUploadService);
    }
}
