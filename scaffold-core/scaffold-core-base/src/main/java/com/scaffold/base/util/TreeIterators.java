package com.scaffold.base.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * 树及层级结构的通用迭代工具。
 */
public final class TreeIterators {

    private TreeIterators() {
    }

    /**
     * 通过单个元素迭代。
     *
     * @param addMyself 是否包含初始元素
     * @param item      初始元素
     * @param function  下一层查询函数
     * @return 迭代结果
     */
    public static <T> Set<T> iterate(boolean addMyself, T item, Function<T, T> function) {
        Set<T> result = new HashSet<>();
        if (item == null) {
            return result;
        }
        T current = item;
        if (addMyself) {
            result.add(current);
        }
        while ((current = function.apply(current)) != null) {
            result.add(current);
        }
        return result;
    }

    /**
     * 通过元素集合批量迭代。
     *
     * @param addMyself 是否包含初始集合
     * @param coll      初始集合
     * @param function  下一层查询函数
     * @return 迭代结果
     */
    public static <T> Set<T> iterate(boolean addMyself, Collection<T> coll, Function<Collection<T>, Collection<T>> function) {
        Set<T> result = new HashSet<>();
        if (coll == null || coll.isEmpty()) {
            return result;
        }
        Collection<T> current = coll;
        if (addMyself) {
            result.addAll(current);
        }
        while (current != null && !current.isEmpty()) {
            current = function.apply(current);
            if (current != null && !current.isEmpty()) {
                result.addAll(current);
            }
        }
        return result;
    }
}
