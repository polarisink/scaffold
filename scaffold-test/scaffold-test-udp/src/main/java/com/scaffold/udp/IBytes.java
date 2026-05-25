package com.scaffold.udp;

import io.netty.buffer.ByteBuf;

/**
 * 用于将对象写入ByteBuf转byte数组
 */
public interface IBytes {


    /*
     * 将当前对象写入bytes
     *
     */
    ByteBuf toBuf();

    default String getName() {
        return "bytes";
    }

    /**
     * 消息分类
     * -1 默认
     * 0 欺骗干扰
     * 1 压制干扰
     */
    default int getClassify() {
        return -1;
    }

    /**
     * 消息具体类型
     *
     * @return
     */
    default int getType() {
        return -1;
    }

    /**
     * 是否打印日志
     *
     * @return 是否打印
     */
    default boolean enableLog() {
        return true;
    }
}
