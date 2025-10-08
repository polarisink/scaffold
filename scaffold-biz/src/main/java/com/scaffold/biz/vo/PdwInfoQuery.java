package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

@Data
public class PdwInfoQuery implements IBytes{
    private MsgHead head;
    //每次默认发2000条PDW数据，分40个UDP包，每个UDP包50条PDW数据。
    //PDW信息申请开关
    private byte[] param1 = new byte[8];
    @Override
    public ByteBuf toBuf() {
        ByteBuf buf = head.toBuf();
        buf.writeBytes(param1);
        return buf;
    }
}
