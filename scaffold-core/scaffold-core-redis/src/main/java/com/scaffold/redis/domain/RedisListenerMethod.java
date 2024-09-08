package com.scaffold.redis.domain;

import lombok.Data;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author lqsgo
 */
@Data
public class RedisListenerMethod {
    private Object bean;

    private String beanName;
    /**
     * 目标方法
     */
    private Method targetMethod;

    private String methodParameterClassName;

    private Class<?> parameterClass;

    private Type parameterType;

    /**
     * 是否使用Message包裹
     */
    private Boolean messageFlag;

    /**
     * 获取bean
     * todo 为什么这么做
     *
     * @param applicationContext context
     * @return bean
     */
    public Object getBean(ApplicationContext applicationContext) {
        if (bean == null) {
            synchronized (this) {
                if (bean == null) {
                    bean = applicationContext.getBean(beanName);
                    if (bean == null) {
                        throw new RuntimeException("获取包含@RedisLister[" + targetMethod.getName() + "]方法的Bean实例失败");
                    }
                }
            }
        }
        return bean;
    }
}
