package com.scaffold.base.util;


import lombok.Setter;

import java.io.Serializable;

/**
 * 分页查询基础请求类
 *
 * @author miaol
 * @date 2020-04-11 10:44
 */
@Setter
public class PageRequest implements Serializable {

    /**
     * 默认页码，第一页
     */

    private static final int ONE = 1;
    /**
     * 默认分页大小，默认10条记录
     */

    private static final int DEFAULT_PAGE_SIZE = 10;
    /**
     * 页码
     */

    protected Integer pageNo = ONE;
    /**
     * 分页大小
     */
    protected Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 页码
     */
    public Integer getPageNo() {
        return (this.pageNo == null || this.pageNo < ONE) ? ONE : this.pageNo;
    }


    /**
     * 分页大小
     */
    public Integer getPageSize() {
        return this.pageSize == null || this.pageSize < ONE ? ONE : this.pageSize;
    }

}
