package com.scaffold.core.base.convert;

import com.scaffold.core.base.exception.IResponseEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

public class StringToEnumConverter<T extends IResponseEnum> implements Converter<String, T> {
    private final Map<String, T> enumMap = new HashMap<>();

    public StringToEnumConverter(Class<T> clazz) {
        for (T enumConstant : clazz.getEnumConstants()) {
            //通过code进行转换
            enumMap.put(String.valueOf(enumConstant.getCode()), enumConstant);
        }

    }

    @Override
    public T convert(String source) {
        T t = enumMap.get(source);
        if (t == null) {
            throw new IllegalArgumentException("不合法的枚举类型");
        }
        return t;
    }
}
