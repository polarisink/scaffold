package com.scaffold.postgres.starter;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class GeoRuntimeHintsTest {

    @Test
    void registersDefaultProvinceBoundaryResource() {
        RuntimeHints hints = new RuntimeHints();

        new GeoRuntimeHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.resource()
                .forResource("scaffold/geo/province-boundaries.csv")
                .test(hints)).isTrue();
    }
}
