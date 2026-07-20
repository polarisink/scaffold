package com.scaffold.orm.starter;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class MyBatisMapperBeanFactoryPostProcessorTest {

    @Test
    void shouldUseMapperInterfacePropertyInsteadOfStringConstructorArgument() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        RootBeanDefinition mapperDefinition = new RootBeanDefinition(MapperFactoryBean.class);
        mapperDefinition.getConstructorArgumentValues().addGenericArgumentValue(TestMapper.class.getName());
        mapperDefinition.getPropertyValues().add("mapperInterface", TestMapper.class);
        beanFactory.registerBeanDefinition("testMapper", mapperDefinition);

        new MyBatisMapperBeanFactoryPostProcessor().postProcessBeanFactory(beanFactory);

        assertThat(mapperDefinition.getConstructorArgumentValues().isEmpty()).isTrue();
        assertThat(mapperDefinition.getPropertyValues().get("mapperInterface")).isEqualTo(TestMapper.class);
    }

    private interface TestMapper {
    }
}
