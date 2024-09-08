package com.scaffold.redis.core;

import com.scaffold.redis.annotations.RedisStreamListener;
import com.scaffold.redis.domain.RedisListenerMethod;
import com.scaffold.redis.domain.RedisMessage;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 注册redis消息队列处理方法，@RedisListener注解扫描器
 *
 * @author lqsgo
 */
public class RedisListenerAnnotationScanPostProcessor implements BeanPostProcessor {

    @Getter
    private static final Map<String, List<RedisListenerMethod>> candidates = new HashMap<>();

    /**
     * 生成RedisListenerMethod对象，用于后续查询
     *
     * @param beanName beanName
     * @param method   方法
     * @return RedisListenerMethod
     */
    private static RedisListenerMethod generateRedisListenerMethod(String beanName, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new RuntimeException("有@RedisListener注解的方法有且仅能包含一个参数");
        }
        RedisListenerMethod rlm = new RedisListenerMethod();
        rlm.setBeanName(beanName);
        rlm.setTargetMethod(method);
        Class<?> parameterType = parameterTypes[0];
        String methodParameterClassName = parameterType.getName();
        rlm.setMethodParameterClassName(methodParameterClassName);
        rlm.setParameterClass(parameterType);
        rlm.setParameterType(method.getGenericParameterTypes()[0]);
        rlm.setMessageFlag(methodParameterClassName.equals(RedisMessage.class.getName()));
        return rlm;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        for (Method method : methods) {
            AnnotationAttributes annotationAttributes = AnnotatedElementUtils
                    .findMergedAnnotationAttributes(method, RedisStreamListener.class, false, false);
            if (null == annotationAttributes) {
                continue;
            }
            String queueName = (String) annotationAttributes.get("queueName");
            String group = (String) annotationAttributes.get("group");
            String consumer = (String) annotationAttributes.get("consumer");
            RedisListenerMethod rlm = generateRedisListenerMethod(beanName, method);
            String key = queueName + "-" + group + "-" + consumer;
            if (!candidates.containsKey(key)) {
                candidates.put(key, new LinkedList<>());
            }
            candidates.get(key).add(rlm);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
