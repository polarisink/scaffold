package com.scaffold.geo.terrain;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

/**
 * 可供 GeoTools 读取的 DEM 数据源。
 *
 * @param identifier 数据源稳定标识，用作缓存键
 * @param localFile  GeoTIFF 本地文件；非文件资源由上层适配器先物化为临时文件
 */
public record DemSource(URI identifier, Path localFile) {

    public DemSource {
        identifier = Objects.requireNonNull(identifier, "identifier").normalize();
        localFile = Objects.requireNonNull(localFile, "localFile").toAbsolutePath().normalize();
    }
}
