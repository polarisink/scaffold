package com.scaffold.redis.messaging;

import com.scaffold.redis.core.RedisListenerAnnotationScanPostProcessor;
import com.scaffold.redis.core.RedisMessageQueueRegister;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@AutoConfiguration(after = RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = "scaffold.redis.messaging", name = "enabled", havingValue = "true")
public class RedisMessagingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static RedisListenerAnnotationScanPostProcessor redisListenerAnnotationScanPostProcessor() {
        return new RedisListenerAnnotationScanPostProcessor();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> redisStreamListenerContainer(
            RedisConnectionFactory connectionFactory) {
        var options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions.<String, ObjectRecord<String, String>>builder()
                .pollTimeout(Duration.ofSeconds(2))
                .targetType(String.class)
                .executor(new SimpleAsyncTaskExecutor("redis-stream-"))
                .build();
        return StreamMessageListenerContainer.create(connectionFactory, options);
    }

    @Bean
    @ConditionalOnMissingBean(RedisMessageQueueRegister.class)
    public RedisMessageQueueRegister redisMessageQueueRegister(
            StreamMessageListenerContainer<String, ObjectRecord<String, String>> container) {
        return new RedisMessageQueueRegister(container);
    }

    @Bean("redisPubSubMessageListenerContainer")
    @ConditionalOnMissingBean(name = "redisPubSubMessageListenerContainer")
    public RedisMessageListenerContainer redisPubSubMessageListenerContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        return container;
    }
}
