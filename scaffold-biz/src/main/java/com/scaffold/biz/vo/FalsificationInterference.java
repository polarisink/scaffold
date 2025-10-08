package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 欺骗干扰
 */
@Data
public class FalsificationInterference implements IBytes{
    //频率段干扰开关
    private int frequencyInterferenceSwitch;

    @Override
    public ByteBuf toBuf() {
        return null;
    }
}
