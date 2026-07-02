package com.scaffold.geo.starter;

import com.scaffold.geo.GeoCoordinate;
import com.scaffold.geo.region.GeoRegion;
import com.scaffold.geo.region.GeoRegionIndex;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpringResourceDemSourceResolverTest {

    @Test
    void materializesAndCleansANonFileClasspathResource() throws Exception {
        byte[] content = "test-dem".getBytes();
        Resource resource = new ByteArrayResource(content) {
            @Override
            public URI getURI() {
                return URI.create("memory:/dem/CN-BJ.tif");
            }

            @Override
            public String getFilename() {
                return "CN-BJ.tif";
            }
        };
        ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return resource;
            }

            @Override
            public ClassLoader getClassLoader() {
                return getClass().getClassLoader();
            }
        };
        GeoRegionIndex regions = new GeoRegionIndex(List.of(
                new GeoRegion("CN-BJ", "北京市", 116.1, 39.7, 116.6, 40.2)));
        SpringResourceDemSourceResolver resolver = new SpringResourceDemSourceResolver(
                regions, resourceLoader, "classpath:/dem/", region -> region.id() + ".tif");

        Path temporaryFile = resolver.resolve(new GeoCoordinate(39.9, 116.4))
                .orElseThrow().localFile();

        assertThat(temporaryFile).isRegularFile();
        assertThat(Files.readAllBytes(temporaryFile)).containsExactly(content);
        resolver.close();
        assertThat(temporaryFile).doesNotExist();
    }
}
