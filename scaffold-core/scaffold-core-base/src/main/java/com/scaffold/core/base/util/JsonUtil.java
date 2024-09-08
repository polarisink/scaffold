package com.scaffold.core.base.util;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.core.base.exception.BaseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Json工具类，使用的是Jackson
 * @author lqsgo
 */
@Slf4j
@Component
public class JsonUtil {
    @Getter
    private static final ObjectMapper mapper = SpringUtil.getBean(ObjectMapper.class);
    private static final ObjectMapper redisMapper = SpringUtil.getBean("redisObjectMapper",ObjectMapper.class);

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
            log.error(e.getMessage());
            throw new BaseException(exMsg);
        }
    }

    @FunctionalInterface
    private interface ThrowsExFunction<R> {
        R apply() throws Exception;
    }


}
