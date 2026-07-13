package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scaffold.base.util.PageResponse;
import org.springframework.data.domain.Page;

/**
 * 分页工具类
 */
public class PageUtils {
    public static <T> PageResponse<T> of(IPage<T> page) {
        return new PageResponse<>(page.getRecords(), page.getPages(), page.getCurrent(), page.getTotal(), page.getSize());
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.toList(), page.getTotalPages(), page.getNumber() + 1, page.getTotalElements(), page.getSize());
    }
}
