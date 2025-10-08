package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 干扰匹配
 */
@Data
public class InterferenceMatch implements IBytes{
    private MsgHead head;
    //通道类型
    private byte channel;
    //干扰跟踪参数设置
    private byte interferenceTrackingParamSetting;

    @Override
    public ByteBuf toBuf() {
        return null;
    }
}
