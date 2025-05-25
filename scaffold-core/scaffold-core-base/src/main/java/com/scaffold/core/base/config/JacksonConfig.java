package com.scaffold.core.base.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.scaffold.core.base.constant.GlobalConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * Jackson配置
 *
 * @author aries
 * @date 2022/07/28
 */
@Configuration
public class JacksonConfig {

    /**
     * java8 时间模块
     *
     * @return 模块
     */
    private static JavaTimeModule getJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_TIME_FORMAT)));
        // javaTimeModule只能手动注册，参考https://github.com/FasterXML/jackson-modules-java8
        return javaTimeModule;
    }

    /**
     * 针对JDK 1.8的日期时间格式特殊处理
     *
     * @return ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = getJavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigDecimal.class, BigDecimalToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        // 忽略json字符串中不识别的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略无法转换的对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    /**
     * 给redis使用的objectMapper，缓存需要保存字段的全类名,不能使用通用的objectMapper
     */
    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(getJavaTimeModule());
        //缓存类型,上面配置不生效，使用下面的
        mapper.setDefaultTyping(new StdTypeResolverBuilder().init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY));
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    @JacksonStdImpl
    static class BigDecimalToStringSerializer extends ToStringSerializer {
        final static BigDecimalToStringSerializer instance = new BigDecimalToStringSerializer();

        BigDecimalToStringSerializer() {
            super(Object.class);
        }

        @Override
        public boolean isEmpty(SerializerProvider prov, Object value) {
            if (value == null) {
                return true;
            }
            String str = ((BigDecimal) value).stripTrailingZeros().toPlainString();
            return str.isEmpty();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(((BigDecimal) value).stripTrailingZeros().toPlainString());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("string", true);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen,
                                      SerializerProvider provider, TypeSerializer typeSer)
                throws IOException {
            // no type info, just regular serialization
            serialize(value, gen, provider);
        }
    }
}
