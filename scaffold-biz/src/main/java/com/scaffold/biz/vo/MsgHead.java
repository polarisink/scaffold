package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

@Data
public class MsgHead implements IBytes {
    private int head;
    private int length;

    public MsgHead(ByteBuf buf) {
        head = buf.readInt();
        length = buf.readInt();
    }

    @Override
    public ByteBuf toBuf() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(head);
        buf.writeInt(length);
        return buf;
    }
}
