package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;

/**
 * 用于将对象写入ByteBuf转byte数组
 */
public interface IBytes {

    //获取buf中可用bytes并释放buf
    static byte[] buf2bytes(ByteBuf buf) {
        try {
            byte[] ret = new byte[buf.readableBytes()];
            buf.readBytes(ret);
            return ret;
        } finally {
            buf.release();
        }
    }

    /*
     * 将当前对象写入bytes
     *
     */
    ByteBuf toBuf();
}
