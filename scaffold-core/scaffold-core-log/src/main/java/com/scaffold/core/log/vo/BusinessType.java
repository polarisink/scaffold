package com.scaffold.core.log.vo;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务操作类型
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
public enum BusinessType {

    OTHER("其他"),

    INSERT("新增"),

    UPDATE("修改"),

    DELETE("删除"),

    EXPORT("导出"),

    IMPORT("导入"),

    FORCE("强退"),

    CLEAN("清空数据");

    public static final Map<Integer, String> map = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, e -> e.message));
    private final String message;

    public static String find(int code) {
        return map.getOrDefault(code, OTHER.message);
    }
}
