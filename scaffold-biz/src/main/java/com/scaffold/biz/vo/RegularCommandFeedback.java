package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 常规命令反馈
 */
@Data
public class RegularCommandFeedback implements IBytes {
    private MsgHead head;
    //固定反馈
    private int param1;

     RegularCommandFeedback(MsgHead head,ByteBuf buf){
        this.head = head;
        this.param1 = buf.readInt();
    }

    @Override
    public ByteBuf toBuf() {
        return null;
    }
}
