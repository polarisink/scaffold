// src/main/java/.../config/FileAccessWebConfiguration.java
package com.scaffold.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.scaffold.file.FileUploadProperties.FILE_STORAGE_PREFIX;

@Slf4j
@Configuration
// 只有当配置为 LOCAL 存储类型时，才激活这个 Web 映射配置
@ConditionalOnProperty(prefix = FILE_STORAGE_PREFIX, name = "type", havingValue = "local", matchIfMissing = true)
public class FileAccessWebConfiguration implements WebMvcConfigurer {

    private final FileUploadProperties properties;

    public FileAccessWebConfiguration(FileUploadProperties properties) {
        this.properties = properties;
    }

    /**
     * 将本地存储路径映射为 HTTP 访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (properties.getType() == FileUploadProperties.StorageType.LOCAL) {
            FileUploadProperties.Local local = properties.getLocal();
            String basePath = local.getBasePath();

            // 确保路径以斜杠结束，并且是 file: 协议
            String location = "file:" + (basePath.endsWith("/") ? basePath : basePath + "/");

            // 访问前缀 /files/** 将会被映射到本地存储目录
            // 访问示例: GET /files/a8f3b2d1.jpg
            registry.addResourceHandler(local.getAccessPath())
                    .addResourceLocations(location);
            // 打印日志以便用户知道访问路径
            log.info("✅ 本地文件存储访问映射已启用， HTTP 访问路径：{}，本地存储路径：{}", local.getAccessPath(), basePath);
        }
    }
}