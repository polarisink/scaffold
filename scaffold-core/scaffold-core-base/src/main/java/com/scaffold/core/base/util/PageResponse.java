package com.scaffold.core.base.util;

import java.util.Collection;

/**
 * 分页查询返回对象
 *
 * @param records 数据记录
 * @param pages   总页数
 * @param current 当前页
 * @param total   总行数
 * @param size    每页显示条数
 * @author aries
 * @since 2020-04-14 16:49
 */
public record PageResponse<T>(
        Collection<T> records,
        long pages,
        long current,
        long total,
        long size
) {
}
