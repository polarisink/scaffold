package com.scaffold.base.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
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
import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.exception.BaseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Slf4j
@Component
public class JsonUtil {
    @Getter
    private static final ObjectMapper mapper = objectMapper();
    private static final ObjectMapper redisMapper = createRedisObjectMapper();

    /**
     * java8 时间模块
     *
     * @return 模块
     */
    public static JavaTimeModule getJavaTimeModule() {
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
    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = getJavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        SimpleModule simpleModule = new SimpleModule();
        // long序列化为字符串，避免前端js精度不对报错
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        // 忽略json字符串中不识别的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略无法转换的对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    /**
     * Creates the standard cache ObjectMapper without requiring this configuration
     * class to be registered in the application context.
     */
    public static ObjectMapper createRedisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(getJavaTimeModule());
        // 缓存类型,上面配置不生效，使用下面的
        mapper.setDefaultTyping(new StdTypeResolverBuilder().init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY));
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    /**
     * 将对象转为json字符串
     *
     * @param obj 对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return execute(() -> mapper.writeValueAsString(obj), "toJson error");
    }

    public static String toRedisJson(Object obj) {
        return execute(() -> redisMapper.writeValueAsString(obj), "toJson error");
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
        return execute(() -> mapper.readValue(json, javaType), "readValue error");
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
        return execute(() -> mapper.readValue(json, clazz), "readValue error");
    }

    public static <T> T read(InputStream json, Class<T> clazz) {
        return execute(() -> mapper.readValue(json, clazz), "readValue error");
    }

    public static <T> T read(URL url, Class<T> clazz) {
        return execute(() -> mapper.readValue(url, clazz), "readValue error");
    }

    public static <T> T read(URL url, JavaType javaType) {
        return execute(() -> mapper.readValue(url, javaType), "readValue error");
    }

    public static <T> T read(URL url, TypeReference<T> typeReference) {
        return execute(() -> mapper.readValue(url, typeReference), "readValue error");
    }

    public static <T> T read(byte[] json, Class<T> clazz) {
        return execute(() -> mapper.readValue(json, clazz), "readValue error");
    }

    public static <T> T read(byte[] json, TypeReference<T> typeReference) {
        return execute(() -> mapper.readValue(json, typeReference), "readValue error");
    }

    public static <T> T redisRead(String json, Class<T> clazz) {
        return execute(() -> redisMapper.readValue(json, clazz), "readValue error");
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
        return execute(() -> mapper.convertValue(object, typeReference), "convertValue error");
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
        return execute(() -> mapper.convertValue(object, aClass), "convertValue error");
    }

    /**
     * 将对象转为byte数组
     *
     * @param a 对象
     * @return byte数组
     */
    public static byte[] writeBytes(Object a) {
        return execute(() -> mapper.writeValueAsBytes(a), "readValue error");
    }

    public static JsonNode readTree(String json) {
        return execute(() -> mapper.readTree(json), "readTree error");
    }

    public static JsonNode readTree(byte[] bytes) {
        return execute(() -> mapper.readTree(bytes), "readTree error");
    }

    public static JsonNode valueToTree(Object o) {
        return execute(() -> mapper.valueToTree(o), "valueToTree error");
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
        return execute(() -> mapper.readValue(json, typeReference), "readValue error");
    }

    public static <T> T redisRead(String json, TypeReference<T> typeReference) {
        return execute(() -> redisMapper.readValue(json, typeReference), "readValue error");
    }

    private static ObjectMapper createDefaultMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * 执行器
     *
     * @param function 函数
     * @param exMsg    错误信息
     * @param <T>      泛型
     * @return 结果
     */
    private static <T> T execute(ThrowsExFunction<T> function, String exMsg) {
        try {
            return function.apply();
        } catch (Exception e) {
            log.error(exMsg, e);
            throw new BaseException(exMsg, e);
        }
    }

    @FunctionalInterface
    private interface ThrowsExFunction<R> {
        R apply() throws Exception;
    }


}
