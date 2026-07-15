package com.scaffold.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

/** Kafka Broker 与本节点独立消费组监听器配置。 */
@AutoConfiguration(after = SseAutoConfiguration.class)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(prefix = "scaffold.sse", name = "broker", havingValue = "kafka")
public class SseKafkaBrokerConfiguration {

    @Bean
    SseMessageBroker kafkaSseMessageBroker(KafkaTemplate<Object, Object> kafkaTemplate,
                                           ObjectMapper objectMapper,
                                           SseProperties properties) {
        return new KafkaSseMessageBroker(kafkaTemplate, objectMapper, properties.kafka().topic());
    }

    @Bean
    KafkaMessageListenerContainer<Object, Object> sseKafkaMessageListenerContainer(
            ConsumerFactory<Object, Object> consumerFactory,
            ObjectMapper objectMapper,
            SseLocalDispatcher dispatcher,
            SseProperties properties) {
        ContainerProperties containerProperties = new ContainerProperties(properties.kafka().topic());
        containerProperties.setGroupId(properties.kafka().groupId());
        containerProperties.setMessageListener((MessageListener<Object, Object>) record ->
                consume(record, objectMapper, dispatcher));
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    private static void consume(ConsumerRecord<Object, Object> record,
                                ObjectMapper objectMapper,
                                SseLocalDispatcher dispatcher) {
        try {
            SseMessage message = objectMapper.readValue(String.valueOf(record.value()), SseMessage.class);
            dispatcher.dispatch(message);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to consume Kafka SSE message", ex);
        }
    }
}
