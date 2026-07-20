package com.scaffold.orm.starter;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class MyBatisRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(
            RuntimeHints hints,
            ClassLoader classLoader
    ) {
        hints.reflection().registerType(
                Slf4jImpl.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );
    }
}