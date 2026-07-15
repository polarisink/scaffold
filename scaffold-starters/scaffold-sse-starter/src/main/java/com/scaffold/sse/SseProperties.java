package com.scaffold.sse;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.UUID;

/** scaffold SSE 配置。 */
@ConfigurationProperties("scaffold.sse")
public record SseProperties(
        Broker broker,
        Duration connectionTimeout,
        Duration heartbeatInterval,
        int queueCapacity,
        Redis redis,
        Kafka kafka) {

    public SseProperties {
        broker = broker == null ? Broker.LOCAL : broker;
        connectionTimeout = connectionTimeout == null ? Duration.ofMinutes(30) : connectionTimeout;
        heartbeatInterval = heartbeatInterval == null ? Duration.ofSeconds(25) : heartbeatInterval;
        queueCapacity = queueCapacity <= 0 ? 100 : queueCapacity;
        redis = redis == null ? new Redis(null) : redis;
        kafka = kafka == null ? new Kafka(null, null) : kafka;
    }

    public enum Broker { LOCAL, REDIS, KAFKA }

    public record Redis(String channel) {
        public Redis {
            channel = channel == null || channel.isBlank() ? "scaffold:sse:messages" : channel;
        }
    }

    public record Kafka(String topic, String groupId) {
        public Kafka {
            topic = topic == null || topic.isBlank() ? "scaffold-sse-messages" : topic;
            groupId = groupId == null || groupId.isBlank() ? "scaffold-sse-" + UUID.randomUUID() : groupId;
        }
    }
}
