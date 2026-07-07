package com.scaffold.geo.starter;

import com.scaffold.geo.region.GeoRegionIndex;
import com.scaffold.geo.terrain.*;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * 地形组件自动配置。
 */
@AutoConfiguration
@EnableConfigurationProperties(GeoTerrainProperties.class)
@ConditionalOnProperty(prefix = GeoTerrainProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GeoTerrainAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GeoRegionIndex geoRegionIndex(ResourceLoader resourceLoader,
                                         GeoTerrainProperties properties) {
        String location = properties.getProvinceBoundaryLocation();
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists() || !resource.isReadable()) {
            throw new BeanCreationException("无法读取省份边界资源: " + location);
        }
        try {
            return GeoRegionIndex.fromCsv(resource.getInputStream());
        } catch (IOException exception) {
            throw new BeanCreationException("加载省份边界资源失败: " + location, exception);
        }
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public DemSourceResolver demSourceResolver(GeoRegionIndex regions,
                                               ResourceLoader resourceLoader,
                                               GeoTerrainProperties properties) {
        return new SpringResourceDemSourceResolver(
                regions, resourceLoader, properties.getDemBaseLocation(), properties::resolveDemFileName);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(ElevationProvider.class)
    public GeoTiffElevationProvider geoTiffElevationProvider(DemSourceResolver sourceResolver,
                                                             GeoTerrainProperties properties) {
        return new GeoTiffElevationProvider(sourceResolver, properties.getMaximumCachedSources());
    }

    @Bean
    @Primary
    @ConditionalOnBean(GeoTiffElevationProvider.class)
    @ConditionalOnMissingBean(CachingElevationProvider.class)
    @ConditionalOnProperty(prefix = GeoTerrainProperties.PREFIX, name = "point-cache-enabled",
            havingValue = "true", matchIfMissing = true)
    public CachingElevationProvider cachingElevationProvider(
            GeoTiffElevationProvider geoTiffElevationProvider,
            GeoTerrainProperties properties) {
        return new CachingElevationProvider(
                geoTiffElevationProvider,
                properties.getMaximumCachedPoints(),
                properties.getCoordinateDecimalPlaces(),
                properties.isCacheMissingElevations());
    }

    @Bean
    @ConditionalOnBean(CachingElevationProvider.class)
    @ConditionalOnMissingBean
    public TerrainMetrics terrainMetrics(CachingElevationProvider cachingElevationProvider,
                                         GeoTiffElevationProvider geoTiffElevationProvider) {
        return new TerrainMetrics(cachingElevationProvider, geoTiffElevationProvider);
    }
}
