package com.scaffold.file.starter;

import com.scaffold.file.FileAccessWebConfiguration;
import com.scaffold.file.FileController;
import com.scaffold.file.FileStorageProperties;
import com.scaffold.file.FileUploadAutoConfiguration;
import com.scaffold.file.FileUploadService;
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
    public FileController fileController(FileUploadService fileUploadService) {
        return new FileController(fileUploadService);
    }
}
