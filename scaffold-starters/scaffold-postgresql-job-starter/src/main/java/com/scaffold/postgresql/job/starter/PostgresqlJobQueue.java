package com.scaffold.postgresql.job.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

@RequiredArgsConstructor
public class PostgresqlJobQueue {

    private final PostgresqlJobStore jobStore;
    private final ObjectMapper objectMapper;
    private final ScaffoldJobProperties properties;

    public long enqueue(String queueName, Object payload) {
        return enqueue(queueName, payload, 0, properties.getDefaultMaxAttempts());
    }

    public long enqueue(String queueName, Object payload, int priority) {
        return enqueue(queueName, payload, priority, properties.getDefaultMaxAttempts());
    }

    public long enqueue(String queueName, Object payload, int priority, int maxAttempts) {
        Assert.hasText(queueName, "PostgreSQL job queue name must not be empty");
        Assert.notNull(payload, "PostgreSQL job payload must not be null");
        return jobStore.enqueue(queueName, serialize(payload), priority, Math.max(1, maxAttempts));
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Serialize PostgreSQL job payload failed", ex);
        }
    }
}
