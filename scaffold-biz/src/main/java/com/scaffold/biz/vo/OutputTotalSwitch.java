package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 输出总开关
 * <p>
 * 本版本为一收一发，只使用通道一输出，通道二开关请发0
 */
@Data
public class OutputTotalSwitch implements IBytes {
    private MsgHead head;
    //通道1,1-输出开,0-待机,初始默认为：0- 待机
    private byte channel1;
    //通道2
    private byte channel2;

    @Override
    public ByteBuf toBuf() {
        ByteBuf buf = head.toBuf();
        buf.writeByte(channel1);
        buf.writeByte(channel2);
        return buf;
    }
}
