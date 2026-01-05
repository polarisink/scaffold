// src/main/java/.../service/FileUploadService.java
package com.scaffold.file;

import java.io.InputStream;
import java.util.Optional;

/**
 * 通用的文件上传服务接口
 */
public interface FileUploadService {

    String upload(InputStream inputStream, String originalFilename, String contentType);

    Optional<InputStream> download(String fileKey);

    boolean delete(String fileKey);

    /**
     * 获取当前存储类型的标识（用于日志和区分）
     *
     * @return 存储类型名称
     */
    String getStorageType();
}