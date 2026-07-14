package com.scaffold.sse;

/** 默认单机消息 Broker，直接将业务消息投递到当前节点连接。 */
final class LocalSseMessageBroker implements SseMessageBroker {

    private final SseLocalDispatcher dispatcher;

    LocalSseMessageBroker(SseLocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public SseSendResult publish(SseMessage message) {
        return SseSendResult.local(message.messageId(), dispatcher.dispatch(message));
    }
}
