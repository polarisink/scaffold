package com.scaffold.orm.starter;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class MyBatisRuntimeHintsTest {

    @Test
    void registersSqlSessionTemplateMethodsForAllBaseMappers() {
        RuntimeHints hints = new RuntimeHints();

        new MyBatisRuntimeHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection()
                .onType(SqlSessionTemplate.class)
                .withMemberCategory(MemberCategory.INVOKE_DECLARED_METHODS)
                .test(hints)).isTrue();
    }
}
