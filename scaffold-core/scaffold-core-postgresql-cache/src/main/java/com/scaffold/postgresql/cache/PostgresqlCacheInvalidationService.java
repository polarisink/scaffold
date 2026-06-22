package com.scaffold.postgresql.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.scheduling.TaskScheduler;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Pattern;

/**
 * 基于 PostgreSQL LISTEN/NOTIFY 的两级缓存 L1 失效广播。
 */
@Slf4j
@RequiredArgsConstructor
public class PostgresqlCacheInvalidationService {

    private static final Pattern CHANNEL_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final ObjectProvider<DataSource> dataSourceProvider;
    private final ObjectProvider<PostgresqlCacheStore> cacheStoreProvider;
    private final TaskScheduler taskScheduler;
    private final ScaffoldCacheProperties properties;
    private final Map<String, Cache> localCaches = new ConcurrentHashMap<>();

    private Connection listenerConnection;
    private PGConnection pgConnection;
    private ScheduledFuture<?> scheduledFuture;

    public void registerLocalCache(String cacheName, Cache cache) {
        localCaches.put(cacheName, cache);
    }

    public void publishEvict(String cacheName, String cacheKey) {
        publish("E|" + encode(cacheName) + "|" + encode(cacheKey));
    }

    public void publishClear(String cacheName) {
        publish("C|" + encode(cacheName));
    }

    public void start() {
        if (!enabled()) {
            return;
        }
        try {
            listenerConnection = requiredDataSource().getConnection();
            pgConnection = listenerConnection.unwrap(PGConnection.class);
            try (Statement statement = listenerConnection.createStatement()) {
                statement.execute("LISTEN " + channel());
            }
            Duration interval = properties.getClusterInvalidation().getPollInterval();
            if (interval == null || interval.isZero() || interval.isNegative()) {
                interval = Duration.ofSeconds(2);
            }
            scheduledFuture = taskScheduler.scheduleWithFixedDelay(this::pollNotifications, Instant.now().plus(interval), interval);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to start PostgreSQL cache invalidation listener", ex);
        }
    }

    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (listenerConnection != null) {
            try {
                listenerConnection.close();
            } catch (SQLException ex) {
                log.warn("close PostgreSQL cache invalidation listener failed: {}", ex.getMessage());
            }
        }
    }

    private void publish(String payload) {
        if (!enabled()) {
            return;
        }
        requiredCacheStore().publishNotify(channel(), payload);
    }

    private void pollNotifications() {
        try (Statement statement = listenerConnection.createStatement()) {
            statement.execute("SELECT 1");
            PGNotification[] notifications = pgConnection.getNotifications();
            if (notifications == null) {
                return;
            }
            for (PGNotification notification : notifications) {
                handle(notification.getParameter());
            }
        } catch (SQLException ex) {
            log.warn("poll PostgreSQL cache invalidation notifications failed: {}", ex.getMessage());
        }
    }

    private void handle(String payload) {
        String[] parts = payload.split("\\|", -1);
        if (parts.length < 2) {
            return;
        }
        String cacheName = decode(parts[1]);
        Cache cache = localCaches.get(cacheName);
        if (cache == null) {
            return;
        }
        if ("C".equals(parts[0])) {
            cache.clear();
            return;
        }
        if ("E".equals(parts[0]) && parts.length == 3) {
            cache.evict(decode(parts[2]));
        }
    }

    private boolean enabled() {
        return properties.getMode() == ScaffoldCacheProperties.CacheMode.TWO_LEVEL
                && properties.getClusterInvalidation().isEnabled();
    }

    private String channel() {
        String channel = properties.getClusterInvalidation().getChannel();
        if (channel == null || !CHANNEL_PATTERN.matcher(channel).matches()) {
            throw new IllegalArgumentException("PostgreSQL cache invalidation channel must be a plain SQL identifier");
        }
        return channel;
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private DataSource requiredDataSource() {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            throw new IllegalStateException("PostgreSQL cache invalidation requires a DataSource bean");
        }
        return dataSource;
    }

    private PostgresqlCacheStore requiredCacheStore() {
        PostgresqlCacheStore cacheStore = cacheStoreProvider.getIfAvailable();
        if (cacheStore == null) {
            throw new IllegalStateException("PostgreSQL cache invalidation requires a PostgresqlCacheStore bean");
        }
        return cacheStore;
    }
}
