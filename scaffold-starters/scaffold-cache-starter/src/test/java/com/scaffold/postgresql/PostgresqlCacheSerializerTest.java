package com.scaffold.postgresql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.base.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresqlCacheSerializerTest {

    private final PostgresqlCacheSerializer serializer = new PostgresqlCacheSerializer(new ObjectMapper());

    @Test
    void roundTripsTypedValueWithJavaTime() {
        CacheUser user = new CacheUser("lqs", 11, Instant.parse("2026-07-07T09:00:00Z"));

        Object deserialized = serializer.deserialize(serializer.serialize(user));

        assertThat(deserialized).isEqualTo(user);
    }

    @Test
    void roundTripsNestedListWithLocalDateTime() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 1, 10, 45, 21);
        List<CacheMenu> menus = List.of(
                new CacheMenu("system", createdAt,
                        List.of(new CacheMenu("user", createdAt, List.of()))));

        Object deserialized = serializer.deserialize(serializer.serialize(menus));

        assertThat(deserialized).isEqualTo(menus);
    }

    @Test
    void roundTripsLocalDateTimeWithApplicationCacheObjectMapper() {
        PostgresqlCacheSerializer applicationSerializer =
                new PostgresqlCacheSerializer(JsonUtil.createRedisObjectMapper());
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 1, 10, 45, 21);
        List<CacheMenu> menus = List.of(new CacheMenu("system", createdAt, List.of()));

        Object deserialized = applicationSerializer.deserialize(applicationSerializer.serialize(menus));

        assertThat(deserialized).isEqualTo(menus);
    }

    record CacheUser(String username, int age, Instant createdAt) {
    }

    record CacheMenu(String name, LocalDateTime createdAt, List<CacheMenu> children) {
    }
}
