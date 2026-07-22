package com.scaffold.postgres.starter;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/** Native-image hints for resources bundled with the Geo starter. */
public class GeoRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("scaffold/geo/province-boundaries.csv");
    }
}
