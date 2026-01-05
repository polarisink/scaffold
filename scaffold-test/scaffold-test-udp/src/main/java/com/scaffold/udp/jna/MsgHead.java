package com.scaffold.udp.jna;

import com.scaffold.udp.IBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgHead implements IBytes {
    private int head;
    private short length;

    public MsgHead(ByteBuf buf) {
        head = buf.readInt();
        length = buf.readShort();
    }


    @Override
    public ByteBuf toBuf() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(head);
        buf.writeShort(length);
        return buf;
    }
}
