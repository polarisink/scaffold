package com.scaffold.cache;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class TwoLevelCacheTest {

    @Test
    void promotesSecondLevelHitToFirstLevel() {
        Cache first = new ConcurrentMapCache("users");
        Cache second = new ConcurrentMapCache("users");
        second.put("42", "Aries");

        TwoLevelCache cache = new TwoLevelCache(first, second);

        assertThat(cache.get("42", String.class)).isEqualTo("Aries");
        assertThat(first.get("42", String.class)).isEqualTo("Aries");
    }

    @Test
    void loadsOnceAndWritesBothLevels() {
        Cache first = new ConcurrentMapCache("users");
        Cache second = new ConcurrentMapCache("users");
        TwoLevelCache cache = new TwoLevelCache(first, second);
        AtomicInteger loads = new AtomicInteger();

        assertThat(cache.get("42", () -> "value-" + loads.incrementAndGet())).isEqualTo("value-1");
        assertThat(cache.get("42", () -> "value-" + loads.incrementAndGet())).isEqualTo("value-1");
        assertThat(loads).hasValue(1);
        assertThat(first.get("42")).isNotNull();
        assertThat(second.get("42")).isNotNull();
    }
}
