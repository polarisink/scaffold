package com.scaffold.web.config;

import com.mzt.logapi.starter.support.aop.LogRecordInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class BizLogRuntimeHintsTest {

    @Test
    void registersLogRecordInterceptorConstructor() {
        RuntimeHints hints = new RuntimeHints();

        new BizLogRuntimeHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection()
                .onType(LogRecordInterceptor.class)
                .withMemberCategory(org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .test(hints)).isTrue();
    }
}
