package com.scaffold.postgresql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.support.NullValue;
import org.springframework.util.Assert;

/**
 * Jackson based serializer for PostgreSQL cache values.
 */
public class PostgresqlCacheSerializer {

    private final ObjectMapper objectMapper;

    public PostgresqlCacheSerializer(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper.copy();
        configureCacheTyping(this.objectMapper);
    }

    public byte[] serialize(Object value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize PostgreSQL cache value with Jackson", ex);
        }
    }

    public Object deserialize(byte[] bytes) {
        try {
            Object value = objectMapper.readValue(bytes, Object.class);
            return value == null ? NullValue.INSTANCE : value;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize PostgreSQL cache value with Jackson", ex);
        }
    }

    private void configureCacheTyping(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        mapper.setDefaultTyping(new StdTypeResolverBuilder()
                .init(JsonTypeInfo.Id.CLASS, null)
                .inclusion(JsonTypeInfo.As.PROPERTY));
    }
}
