package com.scaffold.postgresql;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * PostgreSQL Spring Cache configuration.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "scaffold.cache.postgresql")
public class PostgresqlCacheProperties {

    /**
     * Cache table name. A schema-qualified name is supported.
     */
    private String tableName = "scaffold_spring_cache";

    /**
     * Default cache entry time-to-live. A null or non-positive value means no expiration.
     */
    private Duration defaultTtl = Duration.ofDays(3);

    /**
     * Use an UNLOGGED table. PostgreSQL clears it automatically after crash recovery.
     */
    private boolean unlogged = true;

    /**
     * Create the cache table and index when the cache manager is constructed.
     */
    private boolean initializeSchema = true;

    /**
     * Delete expired entries when the cache manager is constructed.
     */
    private boolean cleanupOnStartup = true;

    /**
     * Periodically delete expired cache entries.
     */
    private boolean scheduledCleanup = true;

    /**
     * Interval between scheduled cleanup runs.
     */
    private Duration cleanupInterval = Duration.ofMinutes(5);
}
