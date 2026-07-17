package com.scaffold.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.time.Duration;

@ConfigurationProperties("scaffold.cache")
public record ScaffoldCacheProperties(Mode mode, Provider type, Provider secondary,
                                      Caffeine caffeine, Redis redis, Postgresql postgresql) {

    public ScaffoldCacheProperties {
        mode = mode == null ? Mode.SINGLE : mode;
        type = type == null ? Provider.CAFFEINE : type;
        secondary = secondary == null ? Provider.REDIS : secondary;
        caffeine = caffeine == null ? new Caffeine() : caffeine;
        redis = redis == null ? new Redis() : redis;
        postgresql = postgresql == null ? new Postgresql() : postgresql;
    }


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
        private DataSourceProperties datasource = new DataSourceProperties();
        private String tableName = "scaffold_spring_cache";
        private Duration defaultTtl = Duration.ofDays(3);
        private boolean unlogged = true;
        private boolean initializeSchema = true;
        private boolean cleanupOnStartup = true;
        private boolean scheduledCleanup = true;
        private Duration cleanupInterval = Duration.ofMinutes(5);
    }
}
