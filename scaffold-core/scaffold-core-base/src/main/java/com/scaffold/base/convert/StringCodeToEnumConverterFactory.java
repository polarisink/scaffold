package com.scaffold.base.convert;

import com.scaffold.base.exception.IResponseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

public class StringCodeToEnumConverterFactory implements ConverterFactory<String, IResponseEnum> {
    private static final Map<Class, Converter> converterMap = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IResponseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, T> converter = (Converter<String, T>) converterMap.get(targetType);
        if (converter == null) {
            converter = new StringToEnumConverter<>(targetType);
            converterMap.put(targetType, converter);
        }
        return converter;
    }
}
