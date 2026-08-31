package com.scaffold.postgresql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.cache.support.NullValue;
import org.springframework.util.Assert;

/**
 * Jackson based serializer for PostgreSQL cache values.
 */
public class PostgresqlCacheSerializer {

    private final JsonMapper jsonMapper;

    public PostgresqlCacheSerializer(JsonMapper jsonMapper) {
        Assert.notNull(jsonMapper, "JsonMapper must not be null");
        var typeValidator = BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build();
        this.jsonMapper = jsonMapper.rebuild()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .changeDefaultVisibility(visibility -> visibility
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                        .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.ANY))
                .activateDefaultTyping(typeValidator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
    }

    public byte[] serialize(Object value) {
        return jsonMapper.writeValueAsBytes(value);
    }

    public Object deserialize(byte[] bytes) {
        Object value = jsonMapper.readValue(bytes, Object.class);
        return value == null ? NullValue.INSTANCE : value;
    }

}
