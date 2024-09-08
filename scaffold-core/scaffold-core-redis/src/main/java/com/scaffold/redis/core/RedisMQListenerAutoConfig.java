package com.scaffold.redis.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

/**
 * @author aries
 * @since 2024-07-19
 */
@Configuration
//@ConditionalOnBean(RedisConnectionFactory.class)
//@ConditionalOnProperty(prefix = "redis.queue.listener", name = "enable", havingValue = "true", matchIfMissing = true)
public class RedisMQListenerAutoConfig {

    @Bean
    public RedisListenerAnnotationScanPostProcessor redisListenerAnnotationScanPostProcessor() {
        return new RedisListenerAnnotationScanPostProcessor();
    }

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2L))
                .targetType(String.class)
                .executor(new SimpleAsyncTaskExecutor())
                .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> stringMapRecordStreamMessageListenerContainer =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);
        stringMapRecordStreamMessageListenerContainer.start();
        return stringMapRecordStreamMessageListenerContainer;
    }
}
