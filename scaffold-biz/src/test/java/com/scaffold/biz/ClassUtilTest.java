package com.scaffold.biz;

import cn.hutool.core.util.ClassUtil;
import com.scaffold.core.base.exception.IResponseEnum;
import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

public class ClassUtilTest {
    @Test
    void a() {
        Class<?>[] allInterfacesForClass = ClassUtils.getAllInterfacesForClass(IResponseEnum.class);
        Class<?>[] classes = ClassUtil.getClasses(IResponseEnum.class);
        System.out.println();
    }
}
