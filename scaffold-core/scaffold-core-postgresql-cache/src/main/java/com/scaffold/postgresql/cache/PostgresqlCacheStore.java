package com.scaffold.postgresql.cache;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 使用 JdbcTemplate 访问 PostgreSQL 缓存表。
 */
public class PostgresqlCacheStore {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile(
            "[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?"
    );

    private final JdbcTemplate jdbcTemplate;

    public PostgresqlCacheStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String validateTableName(String tableName) {
        Assert.hasText(tableName, "PostgreSQL cache table name must not be empty");
        Assert.isTrue(TABLE_NAME_PATTERN.matcher(tableName).matches(),
                "PostgreSQL cache table name must be a plain SQL identifier or schema-qualified identifier");
        return tableName;
    }

    public void initializeSchema(String tableName, boolean unlogged) {
        String validatedTableName = validateTableName(tableName);
        jdbcTemplate.execute("""
                CREATE %s TABLE IF NOT EXISTS %s (
                    cache_name TEXT NOT NULL,
                    cache_key TEXT NOT NULL,
                    cache_value BYTEA NOT NULL,
                    expires_at TIMESTAMPTZ,
                    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (cache_name, cache_key)
                )
                """.formatted(unlogged ? "UNLOGGED" : "", validatedTableName));
        jdbcTemplate.execute(
                "CREATE INDEX IF NOT EXISTS " + indexName(validatedTableName)
                        + " ON " + validatedTableName + " (expires_at)"
        );
    }

    public List<byte[]> findValue(String tableName, String cacheName, String cacheKey) {
        String validatedTableName = validateTableName(tableName);
        return jdbcTemplate.query(
                "SELECT cache_value FROM " + validatedTableName
                        + " WHERE cache_name = ? AND cache_key = ?"
                        + " AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)"
                        + " LIMIT 1",
                (rs, rowNum) -> rs.getBytes("cache_value"),
                cacheName,
                cacheKey
        );
    }

    public void put(String tableName, String cacheName, String cacheKey, byte[] cacheValue, Timestamp expiresAt) {
        String validatedTableName = validateTableName(tableName);
        jdbcTemplate.update(
                "INSERT INTO " + validatedTableName
                        + " (cache_name, cache_key, cache_value, expires_at, created_at, updated_at)"
                        + " VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                        + " ON CONFLICT (cache_name, cache_key) DO UPDATE SET"
                        + " cache_value = EXCLUDED.cache_value,"
                        + " expires_at = EXCLUDED.expires_at,"
                        + " updated_at = CURRENT_TIMESTAMP",
                cacheName,
                cacheKey,
                cacheValue,
                expiresAt
        );
    }

    public int putIfAbsent(String tableName, String cacheName, String cacheKey, byte[] cacheValue, Timestamp expiresAt) {
        String validatedTableName = validateTableName(tableName);
        return jdbcTemplate.update(
                "INSERT INTO " + validatedTableName
                        + " (cache_name, cache_key, cache_value, expires_at, created_at, updated_at)"
                        + " VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                        + " ON CONFLICT (cache_name, cache_key) DO NOTHING",
                cacheName,
                cacheKey,
                cacheValue,
                expiresAt
        );
    }

    public void evict(String tableName, String cacheName, String cacheKey) {
        String validatedTableName = validateTableName(tableName);
        jdbcTemplate.update(
                "DELETE FROM " + validatedTableName + " WHERE cache_name = ? AND cache_key = ?",
                cacheName,
                cacheKey
        );
    }

    public void clear(String tableName, String cacheName) {
        String validatedTableName = validateTableName(tableName);
        jdbcTemplate.update("DELETE FROM " + validatedTableName + " WHERE cache_name = ?", cacheName);
    }

    public int evictExpired(String tableName) {
        String validatedTableName = validateTableName(tableName);
        return jdbcTemplate.update(
                "DELETE FROM " + validatedTableName + " WHERE expires_at IS NOT NULL AND expires_at <= CURRENT_TIMESTAMP"
        );
    }

    public void evictExpired(String tableName, String cacheName, String cacheKey) {
        String validatedTableName = validateTableName(tableName);
        jdbcTemplate.update(
                "DELETE FROM " + validatedTableName
                        + " WHERE cache_name = ? AND cache_key = ?"
                        + " AND expires_at IS NOT NULL AND expires_at <= CURRENT_TIMESTAMP",
                cacheName,
                cacheKey
        );
    }

    public void publishNotify(String channel, String payload) {
        jdbcTemplate.query("SELECT pg_notify(?, ?)", rs -> null, channel, payload);
    }

    private String indexName(String tableName) {
        return tableName.replace('.', '_') + "_expires_at_idx";
    }
}
