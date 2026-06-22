package com.scaffold.postgresql.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Scaffold Spring Cache 配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "scaffold.cache")
public class ScaffoldCacheProperties {

    /**
     * 缓存模式：caffeine、postgresql、two-level。
     */
    private CacheMode mode = CacheMode.TWO_LEVEL;

    private Caffeine caffeine = new Caffeine();

    private Postgresql postgresql = new Postgresql();

    private ClusterInvalidation clusterInvalidation = new ClusterInvalidation();

    public enum CacheMode {
        CAFFEINE,
        POSTGRESQL,
        TWO_LEVEL
    }

    @Getter
    @Setter
    public static class Caffeine {

        /**
         * 本地缓存最大容量。
         */
        private long maximumSize = 10_000;

        /**
         * 本地缓存写入后过期时间。为 null 或非正数时不过期。
         */
        private Duration expireAfterWrite = Duration.ofMinutes(10);
    }

    @Getter
    @Setter
    public static class Postgresql {

        /**
         * 缓存表名，支持 schema.table。
         */
        private String tableName = "scaffold_spring_cache";

        /**
         * 默认缓存过期时间。为 null 或非正数时不过期。
         */
        private Duration defaultTtl = Duration.ofDays(3);

        /**
         * 是否使用 UNLOGGED 表。缓存数据无需 WAL，数据库崩溃恢复后会被 PostgreSQL 自动清空。
         */
        private boolean unlogged = true;

        /**
         * 启动时自动创建缓存表和索引。
         */
        private boolean initializeSchema = true;

        /**
         * 启动时清理已过期缓存。
         */
        private boolean cleanupOnStartup = true;

        /**
         * 是否开启定时清理已过期缓存。
         */
        private boolean scheduledCleanup = true;

        /**
         * 定时清理间隔。
         */
        private Duration cleanupInterval = Duration.ofMinutes(5);
    }

    @Getter
    @Setter
    public static class ClusterInvalidation {

        /**
         * two-level 模式下是否用 PostgreSQL LISTEN/NOTIFY 广播本地缓存失效。
         */
        private boolean enabled = true;

        /**
         * PostgreSQL 通知频道。
         */
        private String channel = "scaffold_cache_invalidate";

        /**
         * LISTEN 连接唤醒间隔。
         */
        private Duration pollInterval = Duration.ofSeconds(2);
    }
}
