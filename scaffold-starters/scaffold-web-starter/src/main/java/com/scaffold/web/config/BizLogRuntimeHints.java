package com.scaffold.web.config;

import com.mzt.logapi.starter.support.aop.LogRecordInterceptor;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Native-image hints missing from bizlog-sdk 3.0.5.
 */
public class BizLogRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(
                LogRecordInterceptor.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    }
}
