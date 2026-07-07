package com.scaffold.postgresql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresqlCacheSerializerTest {

    private final PostgresqlCacheSerializer serializer = new PostgresqlCacheSerializer(new ObjectMapper());

    @Test
    void roundTripsTypedValueWithJavaTime() {
        CacheUser user = new CacheUser("lqs", 11, Instant.parse("2026-07-07T09:00:00Z"));

        Object deserialized = serializer.deserialize(serializer.serialize(user));

        assertThat(deserialized).isEqualTo(user);
    }

    record CacheUser(String username, int age, Instant createdAt) {
    }
}
