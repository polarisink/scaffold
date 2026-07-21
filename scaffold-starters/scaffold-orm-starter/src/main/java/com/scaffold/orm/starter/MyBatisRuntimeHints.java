package com.scaffold.orm.starter;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/**
 * Focused MyBatis hints used instead of MyBatis-Plus' broad Graal classpath scanner.
 */
public class MyBatisRuntimeHints implements RuntimeHintsRegistrar {

    private static final String[] PLUGIN_INTERFACES = {
            "org.apache.ibatis.executor.Executor",
            "org.apache.ibatis.executor.parameter.ParameterHandler",
            "org.apache.ibatis.executor.resultset.ResultSetHandler",
            "org.apache.ibatis.executor.statement.StatementHandler"
    };

    private static final String[] REFLECTIVE_TYPES = {
            "org.apache.ibatis.scripting.defaults.RawLanguageDriver",
            "org.apache.ibatis.scripting.xmltags.XMLLanguageDriver",
            "org.apache.ibatis.javassist.util.proxy.RuntimeSupport",
            "org.apache.ibatis.javassist.util.proxy.ProxyFactory",
            "org.apache.ibatis.logging.slf4j.Slf4jImpl",
            "org.apache.ibatis.logging.Log",
            "org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl",
            "org.apache.ibatis.logging.log4j2.Log4j2Impl",
            "org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl",
            "org.apache.ibatis.logging.stdout.StdOutImpl",
            "org.apache.ibatis.logging.nologging.NoLoggingImpl",
            "org.apache.ibatis.session.SqlSessionFactory",
            "org.apache.ibatis.executor.Executor",
            "org.apache.ibatis.executor.parameter.ParameterHandler",
            "org.apache.ibatis.executor.resultset.ResultSetHandler",
            "org.apache.ibatis.executor.statement.StatementHandler",
            "org.apache.ibatis.cache.impl.PerpetualCache",
            "org.apache.ibatis.cache.decorators.FifoCache",
            "org.apache.ibatis.cache.decorators.LruCache",
            "org.apache.ibatis.cache.decorators.SoftCache",
            "org.apache.ibatis.cache.decorators.WeakCache",
            "org.mybatis.spring.SqlSessionFactoryBean",
            // MyBatis-Plus resolves the factory from SqlSessionTemplate through MetaObject.
            // Registering the type here covers every BaseMapper; applications do not need
            // mapper-specific native hints.
            "org.mybatis.spring.SqlSessionTemplate",
            "com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver",
            "com.baomidou.mybatisplus.core.conditions.ISqlSegment",
            "com.baomidou.mybatisplus.core.conditions.Wrapper",
            "com.baomidou.mybatisplus.core.conditions.AbstractWrapper",
            "com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper",
            "com.baomidou.mybatisplus.core.conditions.query.QueryWrapper",
            "com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper",
            "com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper",
            "com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper",
            "com.baomidou.mybatisplus.core.override.MybatisMapperProxy",
            "java.util.ArrayList",
            "java.util.HashMap",
            "java.util.TreeSet",
            "java.util.HashSet",
            "org.apache.ibatis.mapping.BoundSql"
    };

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (String typeName : REFLECTIVE_TYPES) {
            hints.reflection().registerType(
                    TypeReference.of(typeName),
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS);
        }
        hints.serialization().registerType(
                TypeReference.of("com.baomidou.mybatisplus.core.toolkit.support.SFunction"));
        hints.serialization().registerType(TypeReference.of("java.lang.invoke.SerializedLambda"));
        for (String pluginInterface : PLUGIN_INTERFACES) {
            hints.proxies().registerJdkProxy(TypeReference.of(pluginInterface));
        }
    }
}
