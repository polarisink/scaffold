package com.scaffold.geo.starter;

import com.scaffold.geo.GeoCoordinate;
import com.scaffold.geo.region.GeoRegionIndex;
import com.scaffold.geo.terrain.CachingElevationProvider;
import com.scaffold.geo.terrain.DemSourceResolver;
import com.scaffold.geo.terrain.ElevationProvider;
import com.scaffold.geo.terrain.GeoTiffElevationProvider;
import com.scaffold.geo.terrain.TerrainMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.OptionalDouble;

import static org.assertj.core.api.Assertions.assertThat;

class GeoTerrainAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GeoTerrainAutoConfiguration.class));

    @Test
    void createsTerrainBeansWithoutManualResourceLoaderAssembly() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed()
                    .hasSingleBean(GeoTerrainProperties.class)
                    .hasSingleBean(GeoRegionIndex.class)
                    .hasSingleBean(DemSourceResolver.class)
                    .hasSingleBean(GeoTiffElevationProvider.class)
                    .hasSingleBean(CachingElevationProvider.class)
                    .hasSingleBean(TerrainMetrics.class);
            assertThat(context.getBean(ElevationProvider.class))
                    .isInstanceOf(CachingElevationProvider.class);
            assertThat(context.getBean(GeoRegionIndex.class)
                    .find(new GeoCoordinate(39.9, 116.4))).isPresent();
        });
    }

    @Test
    void supportsClasspathDemResourcesAndFileNamePattern() {
        contextRunner.withPropertyValues(
                        "scaffold.geo.province-boundary-location=classpath:/geo/test-regions.csv",
                        "scaffold.geo.dem-base-location=classpath:/geo/dem/",
                        "scaffold.geo.dem-file-name-pattern={id}.tif")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(DemSourceResolver.class)
                            .resolve(new GeoCoordinate(39.9, 116.4)))
                            .isPresent().get().satisfies(source -> {
                                assertThat(source.identifier().toString()).endsWith("/CN-BJ.tif");
                                assertThat(source.localFile()).isRegularFile();
                            });
                });
    }

    @Test
    void doesNotCreateBeansWhenDisabled() {
        contextRunner.withPropertyValues("scaffold.geo.enabled=false")
                .run(context -> assertThat(context)
                        .doesNotHaveBean(GeoRegionIndex.class)
                        .doesNotHaveBean(DemSourceResolver.class)
                        .doesNotHaveBean(ElevationProvider.class)
                        .doesNotHaveBean(TerrainMetrics.class));
    }

    @Test
    void respectsACustomElevationProvider() {
        ElevationProvider customProvider = coordinate -> OptionalDouble.of(88.0);
        contextRunner.withBean(ElevationProvider.class, () -> customProvider)
                .run(context -> {
                    assertThat(context).hasSingleBean(ElevationProvider.class)
                            .doesNotHaveBean(GeoTiffElevationProvider.class)
                            .doesNotHaveBean(CachingElevationProvider.class);
                    assertThat(context.getBean(ElevationProvider.class)).isSameAs(customProvider);
                });
    }

    @Test
    void canDisablePointCacheWithoutDisablingDemLoading() {
        contextRunner.withPropertyValues("scaffold.geo.point-cache-enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(GeoTiffElevationProvider.class)
                            .doesNotHaveBean(CachingElevationProvider.class)
                            .doesNotHaveBean(TerrainMetrics.class);
                    assertThat(context.getBean(ElevationProvider.class))
                            .isInstanceOf(GeoTiffElevationProvider.class);
                });
    }
}
