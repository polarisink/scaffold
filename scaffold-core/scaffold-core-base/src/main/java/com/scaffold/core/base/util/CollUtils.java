package com.scaffold.core.base.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合类工具类
 */
public class CollUtils {
    public static <T, R> List<R> toList(Collection<T> coll, Predicate<T> predicate, Function<? super T, ? extends R> mapper) {
        return stream(coll, predicate).map(mapper).collect(Collectors.toList());
    }

    public static <T, R> List<R> toList(Collection<T> coll, Function<? super T, ? extends R> mapper) {
        return stream(coll).map(mapper).collect(Collectors.toList());
    }

    public static <T, R> Set<R> toSet(Collection<T> coll, Predicate<T> predicate, Function<? super T, ? extends R> mapper) {
        return stream(coll, predicate).map(mapper).collect(Collectors.toSet());
    }

    public static <T, R> Set<R> toSet(Collection<T> coll, Function<? super T, ? extends R> mapper) {
        return stream(coll).map(mapper).collect(Collectors.toSet());
    }

    public static <T, R> Map<R, List<T>> groupBy(Collection<T> coll, Predicate<T> predicate, Function<? super T, ? extends R> mapper) {
        return stream(coll, predicate).collect(Collectors.groupingBy(mapper));
    }

    public static <T, R> Map<R, List<T>> groupBy(Collection<T> coll, Function<? super T, ? extends R> mapper) {
        return stream(coll).collect(Collectors.groupingBy(mapper));
    }

    public static <T, K, V> Map<? extends K, ? extends V> toMap(Collection<T> coll, Predicate<T> predicate, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return stream(coll, predicate).collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <T, K, V> Map<? extends K, ? extends V> toMap(Collection<T> coll, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return stream(coll).collect(Collectors.toMap(keyMapper, valueMapper));
    }


    public static <T> Stream<T> stream(Collection<T> coll, Predicate<T> predicate) {
        return coll.stream().filter(predicate);
    }

    public static <T> Stream<T> stream(Collection<T> coll) {
        return coll.stream();
    }

}
