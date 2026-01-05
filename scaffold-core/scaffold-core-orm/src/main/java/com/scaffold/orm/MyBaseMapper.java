package com.scaffold.orm;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 自定义增强Mapper
 *
 * @param <T> 实体泛型
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {
    /**
     * 批量插入
     *
     * @param list list
     */
    void insertBatchSomeColumn(@Param("list") Collection<T> list);

    /**
     * 分组批量插入
     *
     * @param list      被插入数据
     * @param batchSize 批次大小
     * @param parallel  是否并行执行
     */
    default void insertOrUpdateBatch(@Param("list") Collection<T> list, int batchSize, boolean parallel) {
        List<List<T>> split = CollUtil.split(list, batchSize);
        if (parallel) {
            split.parallelStream().forEach(this::insertBatchSomeColumn);
        } else {
            split.forEach(this::insertBatchSomeColumn);
        }
    }
}
