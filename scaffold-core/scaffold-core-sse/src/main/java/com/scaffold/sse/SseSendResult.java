package com.scaffold.sse;

/**
 * 消息 Broker 的统一提交结果。
 *
 * @param messageId           全链路消息 ID
 * @param accepted            Broker 是否接受该消息
 * @param enqueuedConnections 本地模式下成功入队的连接数；远程 Broker 无法同步统计时为 {@code null}
 */
public record SseSendResult(String messageId, boolean accepted, Integer enqueuedConnections) {

    public static SseSendResult local(String messageId, int connections) {
        return new SseSendResult(messageId, true, connections);
    }

    public static SseSendResult accepted(String messageId) {
        return new SseSendResult(messageId, true, null);
    }
}
