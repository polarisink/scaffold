package com.scaffold.core.orm.extension;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * 自定义增强mapper
 *
 * @param <T> 实体泛型
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {
    /**
     * 批量插入
     *
     * @param list list
     * @return 成功条数
     */
    int insertBatchSomeColumn(@Param("list") Collection<T> list);

    /**
     * 批量插入或更新（当数据库id存在时更新）
     *
     * @param list list
     * @return 成功条数
     */
    int insertOrUpdateBatch(@Param("list") Collection<T> list);
}
