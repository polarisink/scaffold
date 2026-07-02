package com.scaffold.base.constant;

/**
 * @author miaol
 * @date 2020-05-14 9:52
 */
public interface GlobalConstant {

    /**
     * 树根节点的父id
     */
    Long ROOT_PARENT_ID = 0L;

    /**
     * 树根节点的父id
     */
    String ROOT_PARENT_ID_STR = "0";

    /**
     * 错误码
     */
    int GLOBAL_ERROR_CODE = 600;

    /**
     * Header Bearer token常量
     */
    String AUTHORIZATION_TOKEN_BEARER = "Bearer ";

    /**
     * 默认日期时间格式
     */
    String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    String DEFAULT_TIME_FORMAT = "HH:mm:ss";


    /*
     * 用于跟踪
     */
    String TRACE_ID = "traceId";


}
