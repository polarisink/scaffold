package com.scaffold.redis.core;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.scaffold.core.base.util.JsonUtil;
import com.scaffold.redis.domain.RedisListenerMethod;
import com.scaffold.redis.domain.RedisMessage;
import com.scaffold.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author lqsgo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageQueueRegister implements ApplicationRunner {


    private final StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer;
    private final TypeFactory factory = JsonUtil.getMapper().getTypeFactory();

    @Override
    public void run(ApplicationArguments args) {
        // 启动redis消息队列监听器
        Map<String, List<RedisListenerMethod>> candidates = RedisListenerAnnotationScanPostProcessor.getCandidates();
        for (Map.Entry<String, List<RedisListenerMethod>> entry : candidates.entrySet()) {
            String queueKey = entry.getKey();
            List<RedisListenerMethod> redisListenerMethodList = entry.getValue();
            String[] split = queueKey.split("-");
            String streamKey = split[0];
            String consumerGroupName = split[1];
            String consumerName = split[2];
            List<String> groupName = null;
            try {
                //没这个会报错，因此处理一下
                groupName = RedisUtils.groups(streamKey).stream().map(StreamInfo.XInfoGroup::groupName).toList();
            } catch (Exception e) {
                log.error("groups error:{}", e.getMessage());
            }
            //如果没这个streamKey或streamKey下没对应group，就创建
            if (groupName == null || !groupName.contains(consumerGroupName)) {
                RedisUtils.createGroup(streamKey, consumerGroupName);
            }
            streamMessageListenerContainer.receive(
                    Consumer.from(consumerGroupName, consumerName),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                    message -> {
                        log.debug("stream message. messageId={}, stream={}, body={}",
                                message.getId(), message.getStream(), message.getValue());
                        String messageJson = message.getValue();
                        for (RedisListenerMethod rlm : redisListenerMethodList) {
                            Method targetMethod = rlm.getTargetMethod();
                            RedisMessage<?> redisMessage;
                            //是否是使用Message进行包装
                            Boolean messageFlag = rlm.getMessageFlag();
                            try {
                                if (messageFlag) {
                                    redisMessage = JsonUtil.read(messageJson, RedisMessage.class);
                                } else {
                                    JavaType javaType = factory.constructParametricType(RedisMessage.class, rlm.getParameterClass());
                                    redisMessage = JsonUtil.read(messageJson, javaType);
                                }
                            } catch (Exception e) {
                                log.error("Jackson error:{}", e.getMessage());
                                continue;
                            }
                            try {
                                targetMethod.invoke(SpringUtil.getBean(rlm.getBeanName()), messageFlag ? redisMessage : redisMessage.data());
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                log.error("targetMethod {} invoke error: {}", targetMethod, e.getMessage());
                            }
                        }
                        RedisUtils.acknowledge(consumerGroupName, message);
                    });
            log.info("start message queue listeners：{}.{}", streamKey, consumerGroupName);
        }
    }
}
