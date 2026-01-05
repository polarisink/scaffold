package com.scaffold.udp;

import io.netty.buffer.ByteBuf;

/**
 * 用于将对象写入ByteBuf
 */
public interface IBytes {

    /*
     * 将当前对象写入bytes
     *
     */
    ByteBuf toBuf();

    /**
     * 名字，用于方便日志打印的，默认不用实现
     *
     * @return
     */
    default String getName() {
        return "bytes";
    }
}
