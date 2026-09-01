package com.scaffold.postgresql;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresqlCacheTest {

    @Test
    void evictsUnreadableEntryAndTreatsItAsCacheMiss() {
        PostgresqlCacheStore store = mock(PostgresqlCacheStore.class);
        PostgresqlCacheSerializer serializer = mock(PostgresqlCacheSerializer.class);
        byte[] staleValue = "stale-cache-value".getBytes(StandardCharsets.UTF_8);
        when(store.findValue("cache_table", "userTree", "7"))
                .thenReturn(List.of(staleValue));
        when(serializer.deserialize(staleValue))
                .thenThrow(new IllegalStateException("incompatible cache format"));
        Cache cache = new PostgresqlCache(
                "userTree", store, "cache_table", Duration.ofHours(1), true, serializer);

        assertThat(cache.get(7L)).isNull();
        verify(store).evict("cache_table", "userTree", "7");
    }
}
