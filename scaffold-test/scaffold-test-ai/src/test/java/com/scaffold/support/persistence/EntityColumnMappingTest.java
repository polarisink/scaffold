package com.scaffold.support.persistence;

import com.scaffold.support.conversation.SupportMessageEntity;
import com.scaffold.support.knowledge.persistence.KnowledgeDocumentEntity;
import com.scaffold.support.order.model.DemoLogistics;
import com.scaffold.support.order.model.DemoOrder;
import com.scaffold.support.refund.PendingActionEntity;
import com.scaffold.support.refund.RefundAuditEntity;
import com.scaffold.support.suggestion.HandlingSuggestionEntity;
import com.scaffold.support.workorder.WorkOrderEntity;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 校验实体列映射只保留无法由 Spring 命名策略推导的特殊列名，避免重复维护字段名。
 */
class EntityColumnMappingTest {

    private static final List<Class<?>> ENTITY_TYPES = List.of(
            SupportMessageEntity.class,
            KnowledgeDocumentEntity.class,
            DemoLogistics.class,
            DemoOrder.class,
            PendingActionEntity.class,
            RefundAuditEntity.class,
            HandlingSuggestionEntity.class,
            WorkOrderEntity.class);

    /** 普通驼峰字段应交由统一命名策略转换为下划线列名。 */
    @Test
    void shouldOnlyKeepExplicitNamesForSpecialColumnMappings() {
        List<String> explicitMappings = ENTITY_TYPES.stream()
                .flatMap(type -> Arrays.stream(type.getDeclaredFields()))
                .filter(field -> field.isAnnotationPresent(Column.class))
                .filter(field -> !field.getAnnotation(Column.class).name().isBlank())
                .map(EntityColumnMappingTest::mappingDescription)
                .sorted()
                .toList();

        assertThat(explicitMappings).containsExactly(
                "HandlingSuggestionEntity.recommendedActions=action",
                "SupportMessageEntity.sequence=message_sequence");
    }

    private static String mappingDescription(Field field) {
        return field.getDeclaringClass().getSimpleName() + "." + field.getName() + "="
                + field.getAnnotation(Column.class).name();
    }
}
