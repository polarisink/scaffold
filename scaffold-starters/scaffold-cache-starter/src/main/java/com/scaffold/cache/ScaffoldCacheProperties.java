package com.scaffold.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties("scaffold.cache")
public class ScaffoldCacheProperties {

    private Mode mode = Mode.SINGLE;
    private Provider provider = Provider.CAFFEINE;
    private Provider secondary = Provider.REDIS;
    private final Caffeine caffeine = new Caffeine();
    private final Redis redis = new Redis();
    private final Postgresql postgresql = new Postgresql();

    public enum Mode { SINGLE, TWO_LEVEL }

    public enum Provider { CAFFEINE, REDIS, POSTGRESQL }

    @Getter
    @Setter
    public static class Caffeine {
        private String spec = "maximumSize=10000,expireAfterWrite=30m";
    }

    @Getter
    @Setter
    public static class Redis {
        private Duration timeToLive = Duration.ofDays(3);
        private boolean cacheNullValues;
        private String keyPrefix = "scaffold:cache:";
    }

    /** PostgreSQL cache and optional dedicated datasource settings. */
    @Getter
    @Setter
    public static class Postgresql {
        private final DataSourceProperties datasource = new DataSourceProperties();
        private String tableName = "scaffold_spring_cache";
        private Duration defaultTtl = Duration.ofDays(3);
        private boolean unlogged = true;
        private boolean initializeSchema = true;
        private boolean cleanupOnStartup = true;
        private boolean scheduledCleanup = true;
        private Duration cleanupInterval = Duration.ofMinutes(5);
    }
}
