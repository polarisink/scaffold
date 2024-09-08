package com.scaffold.redis.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 消息监听器注解
 *
 * @author aries
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisStreamListener {

    @AliasFor("queueName")
    String value() default "default_queue";

    @AliasFor("value")
    String queueName() default "default_queue";

    String group() default "default_group";

    String consumer() default "default_consumer";

}
