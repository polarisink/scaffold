package com.scaffold.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = FileStorageProperties.FILE_STORAGE_PREFIX, name = "enabled", havingValue = "true")
public class FileAccessWebConfiguration implements WebMvcConfigurer {

    private final FileStorageProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (properties.getType() == FileStorageProperties.StorageType.LOCAL) {
            FileStorageProperties.Local local = properties.getLocal();
            String basePath = local.getBasePath();
            String location = "file:" + (basePath.endsWith("/") ? basePath : basePath + "/");
            registry.addResourceHandler(local.getAccessPath()).addResourceLocations(location);
            log.info("✅ 本地文件存储访问映射已启用， HTTP 访问路径：{}，本地存储路径：{}", local.getAccessPath(), basePath);
        }
    }
}
