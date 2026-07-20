package com.scaffold.orm.starter;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * Makes MyBatis mapper definitions safe for Spring AOT code generation.
 *
 * <p>MyBatis registers the mapper interface twice: as a String constructor argument and as the
 * {@code mapperInterface} property. Spring AOT cannot bind that String to the generated
 * {@code MapperFactoryBean(Class)} instance supplier and treats {@code Class<?>} as an autowired
 * dependency at runtime. Using the no-arg constructor and the existing property avoids that
 * ambiguity.</p>
 */
final class MyBatisMapperBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (isMapperFactoryBean(beanDefinition)
                    && beanDefinition.getPropertyValues().contains("mapperInterface")) {
                beanDefinition.getConstructorArgumentValues().clear();
            }
        }
    }

    private boolean isMapperFactoryBean(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof AbstractBeanDefinition abstractBeanDefinition
                && abstractBeanDefinition.hasBeanClass()) {
            return MapperFactoryBean.class.isAssignableFrom(abstractBeanDefinition.getBeanClass());
        }
        return MapperFactoryBean.class.getName().equals(beanDefinition.getBeanClassName());
    }
}
