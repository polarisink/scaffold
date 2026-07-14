package com.scaffold.rbac.components;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RbacProperties.class)
public class RbacPropertiesConfiguration {
}
