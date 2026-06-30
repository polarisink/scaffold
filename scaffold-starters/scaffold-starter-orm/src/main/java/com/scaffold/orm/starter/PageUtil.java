package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scaffold.base.util.PageResponse;

public class PageUtil {
    public static <T> PageResponse<T> of(IPage<T> page) {
        return new PageResponse<>(page.getRecords(), page.getPages(), page.getCurrent(), page.getTotal(), page.getSize());
    }
}
