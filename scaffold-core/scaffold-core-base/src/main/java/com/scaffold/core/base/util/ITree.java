package com.scaffold.core.base.util;


import jakarta.annotation.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * tree工具类，通用将list拼装成树的方法
 *
 * @author lqsgo
 * @param <T> 实体类型
 * @param <K> id和parentId的类型
 */
public interface ITree<T, K> {
    /**
     * 将实现ITree的实体列表转为树结构
     *
     * @param parentId   父节点id
     * @param coll       数据集
     * @param comparator 比较器，用于同级之间的排序,可为空
     * @param <K>        id类型
     * @param <T>        实体类型
     * @return 不可变树
     */
    static <K, T extends ITree<T, K>> List<T> toTree(K parentId, Collection<T> coll, @Nullable Comparator<T> comparator) {
        if (coll == null || coll.isEmpty()) {
            return List.of();
        }
        //收集为map，空间换时间
        //处理根节点的parentId可能为空的情况
        Map<Optional<K>, List<T>> map = coll.stream().collect(Collectors.groupingBy(t -> Optional.ofNullable(t.getParentId())));
        List<T> tmp = map.get(Optional.ofNullable(parentId));
        if (tmp == null) {
            //不让返回空值，而是返回空集合
            return List.of();
        }
        //迭代生成树
        while (!tmp.isEmpty()) {
            List<T> childrenList = new ArrayList<>();
            for (T forces : tmp) {
                List<T> child = map.getOrDefault(Optional.ofNullable(forces.getId()), new ArrayList<>());
                //排序
                child.sort(comparator);
                forces.setChildren(child);
                childrenList.addAll(child);
            }
            tmp = childrenList;
        }
        return map.get(Optional.ofNullable(parentId));
    }

    /**
     * 通过大树更新某id下面的子树
     * <p>
     * 适合广度不大深度很大的总树
     *
     * @param totalTree 大树
     * @param subTree   子树
     * @param id        id
     * @param <K>       id泛型
     * @param <T>       实体泛型
     */
    static <K, T extends ITree<T, K>> void updateSubTree(List<T> totalTree, List<T> subTree, K id) {
        List<T> tmp = totalTree;
        while (!tmp.isEmpty()) {
            List<T> tmpChildren = new ArrayList<>();
            //广度遍历，找对应的id，找到之后设置
            for (T f : totalTree) {
                if (Objects.equals(f.getId(), id)) {
                    f.setChildren(subTree);
                    //设置完成后，工作完成，推出
                    return;
                }
                //没找到就把children加入进去，继续找
                tmpChildren.addAll(f.getChildren());
            }
            tmp = tmpChildren;
        }
    }

    /**
     * 根据需要更新数据id节点往上的祖先id列表更新
     * <p>
     * 通过idList快速定位，适合树的广度很大但深度不大的情况，节省性能
     *
     * @param totalTree         总树
     * @param subTree           子树
     * @param foreFathersIdList id集合,从最早祖先到自己的id集合，最后一个元素为需要更新子树对应节点的id
     * @param <K>               id泛型
     * @param <T>               实体泛型
     */
    static <K, T extends ITree<T, K>> void updateSubTree(List<T> totalTree, List<T> subTree, List<K> foreFathersIdList) {
        if (totalTree == null || totalTree.isEmpty() || foreFathersIdList == null || foreFathersIdList.isEmpty()) {
            return;
        }
        int depth = foreFathersIdList.size();
        AtomicInteger index = new AtomicInteger();
        List<T> total = totalTree;
        T tmp;
        //层数不越界且当层包含对应节点的，就可以继续找下一层
        while (index.get() < depth && (tmp = total.stream().filter(t -> Objects.equals(t.getId(), foreFathersIdList.get(index.get()))).findFirst().orElse(null)) != null) {
            //说明找到了最后一个节点对应的,设置，完成工作退出
            if (index.get() == depth - 1) {
                tmp.setChildren(subTree);
                break;
            }
            //下级children为空，就找不到了，退出
            List<T> children = tmp.getChildren();
            if (children == null || children.isEmpty()) {
                break;
            }
            //遍历下一层，id也取下一层的
            total = children;
            index.incrementAndGet();
        }
    }

    /**
     * 通过单个元素迭代，结果不包含自己
     *
     * @param addMyself 是否添加初始元素
     * @param item      单个元素
     * @param function  通过单个元素查询的函数
     * @return 迭代结果
     */
    static <T> Set<T> iterate(boolean addMyself, T item, Function<T, T> function) {
        Set<T> res = new HashSet<>();
        if (item == null) {
            return res;
        }
        T tmp = item;
        // 如果需要添加自己，就加一下
        if (addMyself) {
            res.add(tmp);
        }
        // 迭代查询并添加
        while ((tmp = function.apply(tmp)) != null) {
            res.add(tmp);
        }
        return res;
    }

    /**
     * 通过元素集合批量迭代,结果不包含自己
     *
     * @param addMyself 是否添加初始集合
     * @param coll      项目集合
     * @param function  迭代查询项目集合
     * @return 迭代结果
     */
    static <T> Set<T> iterate(boolean addMyself, Collection<T> coll, Function<Collection<T>, Collection<T>> function) {
        Set<T> res = new HashSet<>();
        if (coll == null || coll.isEmpty()) {
            return res;
        }
        Collection<T> tmp = coll;
        // 如果需要添加自己，就加一下
        if (addMyself) {
            res.addAll(tmp);
        }
        // 迭代查询并添加
        while (isNotEmpty(tmp = function.apply(tmp))) {
            res.addAll(tmp);
        }
        return res;
    }

    private static boolean isNotEmpty(Collection<?> coll) {
        return coll != null && !coll.isEmpty();
    }

    /**
     * 获取id
     *
     * @return id
     */
    K getId();

    /**
     * 获取父id
     *
     * @return 父id
     */
    K getParentId();

    /**
     * 获取children
     *
     * @return children
     */
    List<T> getChildren();

    /**
     * 设置下级children
     *
     * @param children children
     */
    void setChildren(List<T> children);
}
