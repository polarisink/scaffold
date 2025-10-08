package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 设备状态应答
 */
@Data
public class DeviceStatusRes implements IBytes{
    private MsgHead head;
    //FPGA温度及设备状态查询
    private int fpgaTemperature;
    //仅对带有干扰功能的板卡有效，否则不使用的话可忽略。
    //0/1未锁定（工作异常）/已锁定（工作正常）
    private byte adSyncLocalSignal;
    //0.2~2g功放状态，0/1:关/异常，开/正常
    private byte amplifierStatus1;
    //2~6g功放状态，0/1:关/异常，开/正常
    private byte amplifierStatus2;
    //6~18g功放状态，0/1:关/异常，开/正常
    private byte amplifierStatus3;
    //备用
    private byte[] reserved = new byte[4];

    @Override
    public ByteBuf toBuf() {
        return null;
    }
}
