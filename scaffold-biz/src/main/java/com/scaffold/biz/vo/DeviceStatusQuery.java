package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class DeviceStatusQuery implements IBytes{
    private MsgHead head;
    //FPGA温度及设备状态查询
    private int param1;
    @Override
    public ByteBuf toBuf() {
        return null;
    }
}
