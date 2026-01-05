package com.scaffold.base.exception;

import cn.hutool.core.util.ArrayUtil;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言，可使用枚举或静态方法
 *
 * @author aries
 * @date 2022/10/07
 */
public interface Assert extends IResponseEnum {

    /**
     * 断言是真， 如果不是  就报错
     *
     * @param expression       表达式
     * @param errorMsgTemplate 错误味精模板
     * @param args             参数
     */
    static void isTrue(Boolean expression, String errorMsgTemplate, Object... args) {
        if (!Boolean.TRUE.equals(expression)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    /**
     * 断言是假  如果是真就报错
     *
     * @param expression       表达式
     * @param errorMsgTemplate 错误味精模板
     */
    static void isFalse(Boolean expression, String errorMsgTemplate, Object... args) {
        if (!Boolean.FALSE.equals(expression)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    /**
     * 维护非空
     *
     * @param obj obj
     */
    static void notNull(Object obj, String errorMsgTemplate, Object... args) {
        if (null == obj) {
            assertFail(errorMsgTemplate, args);
        }
    }

    /**
     * 维护非空
     *
     * @param obj obj
     */
    static void isNull(Object obj, String errorMsgTemplate, Object... args) {
        if (null != obj) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static void isNotBlank(String str, String errorMsgTemplate, Object... args) {
        if (null == str || str.isBlank()) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static void isBlank(String str, String errorMsgTemplate, Object... args) {
        if (null != str && !str.isBlank()) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static void isNotEmpty(String str, String errorMsgTemplate, Object... args) {
        if (null == str || str.isEmpty()) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static void isEmpty(String str, String errorMsgTemplate, Object... args) {
        if (null != str && !str.isEmpty()) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <T> void contains(Collection<T> coll, T t, String errorMsgTemplate, Object... args) {
        if (coll == null || !coll.contains(t)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <T> void notContains(Collection<T> coll, T t, String errorMsgTemplate, Object... args) {
        if (coll != null && coll.contains(t)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <T> void contains(T[] coll, T t, String errorMsgTemplate, Object... args) {
        if (coll == null || !ArrayUtil.contains(coll, t)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <T> void notContains(T[] coll, T t, String errorMsgTemplate, Object... args) {
        if (coll != null && ArrayUtil.contains(coll, t)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <K, V> void contains(Map<K, V> map, K k, String errorMsgTemplate, Object... args) {
        if (map == null || !map.containsKey(k)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <K, V> void notContains(Map<K, V> map, K k, String errorMsgTemplate, Object... args) {
        if (map != null && map.containsKey(k)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <K, V> void containsValue(Map<K, V> map, V v, String errorMsgTemplate, Object... args) {
        if (map == null || !map.containsValue(v)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static <K, V> void notContainsValue(Map<K, V> map, V v, String errorMsgTemplate, Object... args) {
        if (map != null && map.containsValue(v)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static void equals(Object a, Object b, String errorMsgTemplate, Object... args) {
        if (!Objects.equals(a, b)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    static void notEquals(Object a, Object b, String errorMsgTemplate, Object... args) {
        if (Objects.equals(a, b)) {
            assertFail(errorMsgTemplate, args);
        }
    }

    /**
     * 直接错误处理
     *
     * @param errorMsgTemplate 错误味精模板
     */
    static void isError(String errorMsgTemplate) {
        throw new BaseException(errorMsgTemplate);
    }

    static void assertFail(String errorMsgTemplate, Object... args) {
        String msg = MessageFormat.format(errorMsgTemplate, args);
        throw new BaseException(msg);
    }

    default int getCode() {
        return 400;
    }

    /**
     * 断言是真
     *
     * @param expression 表达式
     * @param args       arg游戏
     */
    default void isTrue(Boolean expression, Object... args) {
        isTrue(expression, getMessage(), args);
    }

    //======================================static方法========================================

    /**
     * 断言是假
     *
     * @param expression 表达式
     * @param args       arg游戏
     */
    default void isFalse(Boolean expression, Object... args) {
        isFalse(expression, getMessage(), args);
    }

    /**
     * 维护非空
     *
     * @param obj obj
     */
    default void notNull(Object obj, Object... args) {
        notNull(obj, getMessage(), args);
    }

    /**
     * 维护非空
     *
     * @param obj obj
     */
    default void isNull(Object obj, Object... args) {
        isNull(obj, getMessage(), args);
    }

    default void isNotBlank(String str, Object... args) {
        isNotBlank(str, getMessage(), args);
    }

    default void isBlank(String str, Object... args) {
        isBlank(str, getMessage(), args);
    }

    default void isNotEmpty(String str, Object... args) {
        isNotEmpty(str, getMessage(), args);
    }

    default void isEmpty(String str, Object... args) {
        isEmpty(str, getMessage(), args);
    }

    default <T> void contains(Collection<T> coll, T t, Object... args) {
        contains(coll, t, getMessage(), args);
    }

    default <T> void notContains(Collection<T> coll, T t, Object... args) {
        notContains(coll, t, getMessage(), args);
    }

    default <T> void contains(T[] coll, T t, Object... args) {
        contains(coll, t, getMessage(), args);
    }

    default <T> void notContains(T[] coll, T t, Object... args) {
        notContains(coll, t, getMessage(), args);
    }

    default <K, V> void contains(Map<K, V> map, K k, Object... args) {
        contains(map, k, getMessage(), args);
    }

    default <K, V> void notContains(Map<K, V> map, K k, Object... args) {
        notContains(map, k, getMessage(), args);
    }

    default <K, V> void containsValue(Map<K, V> map, V v, Object... args) {
        containsValue(map, v, getMessage(), args);
    }

    default <K, V> void notContainsValue(Map<K, V> map, V v, Object... args) {
        notContainsValue(map, v, getMessage(), args);
    }

    default void equals(Object a, Object b, Object... args) {
        equals(a, b, getMessage(), args);
    }

    default void notEquals(Object a, Object b, Object... args) {
        notEquals(a, b, getMessage(), args);
    }

    /**
     * 断言是真
     *
     * @param args arg游戏
     */
    default void assertFail(Object... args) {
        String msg = MessageFormat.format(getMessage(), args);
        throw new BaseException(msg);
    }

    /**
     * <p>直接抛出异常
     */
    default void assertFail() {
        throw new BaseException(getMessage());
    }

    /**
     * 断言失败
     *
     * @param msg 味精
     */
    default void assertFail(String msg) {
        throw new BaseException(msg);
    }

}
