package com.scaffold.base.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;
import com.scaffold.base.constant.GlobalConstant;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Json工具类，使用的是Jackson
 *
 * @author lqsgo
 */
@Component
public class JsonUtil {
    @Getter
    private static final JsonMapper mapper = jsonMapper();
    private static final JsonMapper redisMapper = createRedisJsonMapper();

    /**
     * java8 时间模块
     *
     * @return 模块
     */
    public static SimpleModule javaTimeModule() {
        SimpleModule javaTimeModule = new SimpleModule("scaffold-java-time");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(GlobalConstant.DEFAULT_TIME_FORMAT)));
        return javaTimeModule;
    }

    /**
     * 针对JDK 1.8的日期时间格式特殊处理
     *
     * @return JsonMapper
     */
    public static JsonMapper jsonMapper() {
        SimpleModule simpleModule = new SimpleModule();
        // long序列化为字符串，避免前端js精度不对报错
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return JsonMapper.builder()
                .addModules(javaTimeModule(), simpleModule)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    /**
     * Creates the standard cache JsonMapper without requiring this configuration
     * class to be registered in the application context.
     */
    public static JsonMapper createRedisJsonMapper() {
        var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        return JsonMapper.builder()
                .addModule(javaTimeModule())
                .activateDefaultTyping(typeValidator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .changeDefaultVisibility(visibility -> visibility.withVisibility(PropertyAccessor.ALL,
                        JsonAutoDetect.Visibility.ANY))
                .build();
    }

    /**
     * 将对象转为json字符串
     *
     * @param obj 对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return mapper.writeValueAsString(obj);
    }

    public static String toRedisJson(Object obj) {
        return redisMapper.writeValueAsString(obj);
    }

    /**
     * 从javaType解析对象
     *
     * @param json     json
     * @param javaType java类型
     * @param <T>      泛型
     * @return 对象
     */
    public static <T> T read(String json, JavaType javaType) {
        return mapper.readValue(json, javaType);
    }

    /**
     * 将json字符串转为对象
     *
     * @param json  json字符串
     * @param clazz class
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T read(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }

    public static <T> T read(InputStream json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }

    public static <T> T read(URL url, Class<T> clazz) {
        try (InputStream input = url.openStream()) {
            return mapper.readValue(input, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(URL url, JavaType javaType) {
        try (InputStream input = url.openStream()) {
            return mapper.readValue(input, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(URL url, TypeReference<T> typeReference) {
        try (InputStream input = url.openStream()) {
            return mapper.readValue(input, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(byte[] json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }

    public static <T> T read(byte[] json, TypeReference<T> typeReference) {
        return mapper.readValue(json, typeReference);
    }

    public static <T> T redisRead(String json, Class<T> clazz) {
        return redisMapper.readValue(json, clazz);
    }

    /**
     * 将对象转为指定类型
     *
     * @param object        对象
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 结果
     */
    public static <T> T convert(Object object, TypeReference<T> typeReference) {
        return mapper.convertValue(object, typeReference);
    }

    /**
     * 将对象转为指定类型
     *
     * @param object 对象
     * @param aClass 类型
     * @param <T>    泛型
     * @return 结果
     */
    public static <T> T convert(Object object, Class<T> aClass) {
        return mapper.convertValue(object, aClass);
    }

    /**
     * 将对象转为byte数组
     *
     * @param a 对象
     * @return byte数组
     */
    public static byte[] writeBytes(Object a) {
        return mapper.writeValueAsBytes(a);
    }

    public static JsonNode readTree(String json) {
        return mapper.readTree(json);
    }

    public static JsonNode readTree(byte[] bytes) {
        return mapper.readTree(bytes);
    }

    public static JsonNode valueToTree(Object o) {
        return mapper.valueToTree(o);
    }

    /**
     * 从typeReference转对象
     *
     * @param json          json
     * @param typeReference reference
     * @param <T>           泛型
     * @return 对象
     */
    public static <T> T read(String json, TypeReference<T> typeReference) {
        return mapper.readValue(json, typeReference);
    }

    public static <T> T redisRead(String json, TypeReference<T> typeReference) {
        return redisMapper.readValue(json, typeReference);
    }


}
