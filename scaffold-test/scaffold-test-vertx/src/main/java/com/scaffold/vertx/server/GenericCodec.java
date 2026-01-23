package com.scaffold.vertx.server;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

/**
 * 通用 Codec
 *
 * @param <T>
 */
public class GenericCodec<T> implements MessageCodec<T, T> {
    private final Class<T> clazz;

    public GenericCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void encodeToWire(Buffer buffer, T t) {
        // 将对象转为 JSON 字符串写入 Buffer
        String json = Json.encode(t);
        buffer.appendInt(json.length());
        buffer.appendString(json);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        String json = buffer.getString(pos + 4, pos + 4 + length);
        return Json.decodeValue(json, clazz);
    }

    @Override
    public T transform(T t) {
        // 在本地多实例间传递时，直接返回对象引用（性能最高）
        return t;
    }

    @Override
    public String name() {
        return clazz.getSimpleName() + "Codec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    } // 标识为自定义 Codec
}