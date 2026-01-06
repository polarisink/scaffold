package com.scaffold.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 只有当启用且配置为 LOCAL 存储类型时，才激活这个 Web 映射配置
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("#{'${file-storage.enabled:false}' == 'true' && '${file-storage.type}' == 'local'}")
public class FileAccessWebConfiguration implements WebMvcConfigurer {

    private final FileStorageProperties properties;

    /**
     * 将本地存储路径映射为 HTTP 访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (properties.getType() == FileStorageProperties.StorageType.LOCAL) {
            FileStorageProperties.Local local = properties.getLocal();
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