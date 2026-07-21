package com.scaffold.web.config;

import com.mzt.logapi.starter.configuration.LogRecordProperties;
import com.mzt.logapi.starter.annotation.EnableLogRecord;
import com.mzt.logapi.starter.support.aop.BeanFactoryLogRecordAdvisor;
import com.mzt.logapi.starter.support.aop.LogRecordInterceptor;
import com.mzt.logapi.starter.support.aop.LogRecordOperationSource;
import com.mzt.logapi.service.ILogRecordPerformanceMonitor;
import org.springframework.aot.AotDetector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

/**
 * Adapts the bizlog-sdk 3.0.5 interceptor bean definition for Spring AOT.
 */
final class BizLogAotBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final String INTERCEPTOR_BEAN_NAME = "logRecordInterceptor";
    private static final String ADVISOR_BEAN_NAME = "logRecordAdvisor";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!AotDetector.useGeneratedArtifacts() || !beanFactory.containsBeanDefinition(INTERCEPTOR_BEAN_NAME)) {
            return;
        }

        EnableLogRecord settings = resolveSettings(beanFactory);
        LogRecordProperties properties = beanFactory.getBean(LogRecordProperties.class);
        LogRecordOperationSource operationSource = beanFactory.getBean(LogRecordOperationSource.class);

        LogRecordInterceptor interceptor = new LogRecordInterceptor();
        interceptor.setLogRecordOperationSource(operationSource);
        interceptor.setTenant(settings.tenant());
        interceptor.setJoinTransaction(settings.joinTransaction());
        interceptor.setDiffLog(properties.getDiffLog());
        interceptor.setLogRecordPerformanceMonitor(beanFactory.getBean(ILogRecordPerformanceMonitor.class));

        BeanFactoryLogRecordAdvisor advisor = new BeanFactoryLogRecordAdvisor();
        advisor.setLogRecordOperationSource(operationSource);
        advisor.setAdvice(interceptor);
        advisor.setOrder(settings.order());

        LogRecordInterceptor initializedInterceptor = (LogRecordInterceptor)
                beanFactory.initializeBean(interceptor, INTERCEPTOR_BEAN_NAME);
        advisor.setAdvice(initializedInterceptor);
        BeanFactoryLogRecordAdvisor initializedAdvisor = (BeanFactoryLogRecordAdvisor)
                beanFactory.initializeBean(advisor, ADVISOR_BEAN_NAME);

        SingletonBeanRegistry registry = (SingletonBeanRegistry) beanFactory;
        registry.registerSingleton(INTERCEPTOR_BEAN_NAME, initializedInterceptor);
        registry.registerSingleton(ADVISOR_BEAN_NAME, initializedAdvisor);
    }

    private EnableLogRecord resolveSettings(ConfigurableListableBeanFactory beanFactory) {
        for (String beanName : beanFactory.getBeanNamesForAnnotation(EnableLogRecord.class)) {
            Class<?> beanType = beanFactory.getType(beanName, false);
            if (beanType != null) {
                EnableLogRecord annotation = beanType.getAnnotation(EnableLogRecord.class);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        throw new IllegalStateException("Cannot resolve @EnableLogRecord settings for AOT runtime");
    }
}
