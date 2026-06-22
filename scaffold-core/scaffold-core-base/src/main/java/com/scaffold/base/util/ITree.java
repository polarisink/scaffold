package com.scaffold.base.util;

import jakarta.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 树节点协议，提供通用的原地构树能力。
 *
 * @param <T> 节点类型
 * @param <K> 节点 id 类型
 */
public interface ITree<T, K> {

    /**
     * 将实现 {@link ITree} 的节点集合原地组装为树。
     * 该方法会直接调用节点的 {@link #setChildren(List)} 改写 children。
     *
     * @param parentId   根节点 parentId
     * @param coll       数据集
     * @param comparator 同级排序规则，可为空；为空时保持原顺序
     * @param <K>        id 类型
     * @param <T>        节点类型
     * @return 根节点集合；无数据时返回不可变空集合
     */
    static <K, T extends ITree<T, K>> List<T> toTree(K parentId, Collection<T> coll, @Nullable Comparator<T> comparator) {
        if (coll == null || coll.isEmpty()) {
            return List.of();
        }

        Map<Optional<K>, List<T>> grouped = coll.stream().collect(Collectors.groupingBy(node -> Optional.ofNullable(node.getParentId()), Collectors.toCollection(ArrayList::new)));
        List<T> roots = grouped.get(Optional.ofNullable(parentId));
        if (roots == null || roots.isEmpty()) {
            return List.of();
        }

        // 先清空所有节点 children，避免重复构树时残留旧结果。
        for (T node : coll) {
            node.setChildren(new ArrayList<>());
        }

        sortIfNecessary(roots, comparator);
        Queue<T> queue = new ArrayDeque<>(roots);
        Set<K> path = new HashSet<>();
        while (!queue.isEmpty()) {
            T node = queue.poll();
            K nodeId = node.getId();
            if (!path.add(nodeId)) {
                throw new IllegalStateException("Detected cyclic tree relationship for node id: " + nodeId);
            }
            List<T> children = grouped.getOrDefault(Optional.ofNullable(nodeId), List.of());
            List<T> mutableChildren = new ArrayList<>(children);
            mutableChildren.removeIf(child -> Objects.equals(child.getId(), nodeId));
            sortIfNecessary(mutableChildren, comparator);
            node.setChildren(mutableChildren);
            for (T child : mutableChildren) {
                K childId = child.getId();
                if (path.contains(childId)) {
                    throw new IllegalStateException("Detected cyclic tree relationship between node ids: " + nodeId + " and " + childId);
                }
                queue.offer(child);
            }
        }
        return roots;
    }

    private static <T> void sortIfNecessary(List<T> list, @Nullable Comparator<T> comparator) {
        if (comparator != null && list.size() > 1) {
            list.sort(comparator);
        }
    }

    K getId();

    K getParentId();

    List<T> getChildren();

    void setChildren(List<T> children);
}
