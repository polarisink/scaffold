package com.scaffold.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文件访问配置")
public record FileAccessInfo(
        @Schema(description = "存储类型") String storageType,
        @Schema(description = "文件公开访问前缀") String accessPrefix) {
}
