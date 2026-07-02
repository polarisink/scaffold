package com.scaffold.geo.starter;

import com.scaffold.geo.GeoCoordinate;
import com.scaffold.geo.region.GeoRegion;
import com.scaffold.geo.region.GeoRegionIndex;
import com.scaffold.geo.terrain.DemSource;
import com.scaffold.geo.terrain.DemSourceResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** 使用 Spring 资源协议定位区域 DEM 文件。 */
public final class SpringResourceDemSourceResolver implements DemSourceResolver, AutoCloseable {

    private final GeoRegionIndex regions;
    private final ResourceLoader resourceLoader;
    private final String baseLocation;
    private final Function<GeoRegion, String> fileNameResolver;
    private final Map<URI, Path> temporaryFiles = new LinkedHashMap<>();

    public SpringResourceDemSourceResolver(GeoRegionIndex regions,
                                           ResourceLoader resourceLoader,
                                           String baseLocation,
                                           Function<GeoRegion, String> fileNameResolver) {
        this.regions = Objects.requireNonNull(regions, "regions");
        this.resourceLoader = Objects.requireNonNull(resourceLoader, "resourceLoader");
        Objects.requireNonNull(baseLocation, "baseLocation");
        this.baseLocation = baseLocation.endsWith("/") ? baseLocation : baseLocation + "/";
        this.fileNameResolver = Objects.requireNonNull(fileNameResolver, "fileNameResolver");
    }

    @Override
    public Optional<DemSource> resolve(GeoCoordinate coordinate) {
        return regions.find(coordinate).flatMap(region -> resolveRegion(region));
    }

    private Optional<DemSource> resolveRegion(GeoRegion region) {
        String fileName = fileNameResolver.apply(region);
        if (!isSafeRelativeResourceName(fileName)) return Optional.empty();
        Resource resource = resourceLoader.getResource(baseLocation + fileName);
        if (!resource.exists() || !resource.isReadable()) return Optional.empty();
        try {
            URI identifier = resource.getURI().normalize();
            Path localFile = resource.isFile()
                    ? resource.getFile().toPath()
                    : materializeResource(identifier, resource);
            return Optional.of(new DemSource(identifier, localFile));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    private Path materializeResource(URI identifier, Resource resource) throws IOException {
        synchronized (temporaryFiles) {
            Path existing = temporaryFiles.get(identifier);
            if (existing != null && Files.isRegularFile(existing)) return existing;
            Path temporaryFile = Files.createTempFile("scaffold-dem-", ".tif");
            try (var input = resource.getInputStream(); var output = Files.newOutputStream(temporaryFile)) {
                input.transferTo(output);
            } catch (IOException exception) {
                Files.deleteIfExists(temporaryFile);
                throw exception;
            }
            temporaryFiles.put(identifier, temporaryFile);
            return temporaryFile;
        }
    }

    private boolean isSafeRelativeResourceName(String fileName) {
        if (fileName == null || fileName.isBlank()) return false;
        String normalized = fileName.replace('\\', '/');
        if (normalized.startsWith("/")) return false;
        for (String segment : normalized.split("/")) {
            if (segment.equals("..")) return false;
        }
        return true;
    }

    @Override
    public void close() {
        synchronized (temporaryFiles) {
            for (Path temporaryFile : temporaryFiles.values()) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    temporaryFile.toFile().deleteOnExit();
                }
            }
            temporaryFiles.clear();
        }
    }
}
