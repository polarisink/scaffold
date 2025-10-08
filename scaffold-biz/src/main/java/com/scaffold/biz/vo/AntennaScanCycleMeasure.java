package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 天线扫描周期测量
 */
@Data
public class AntennaScanCycleMeasure implements IBytes{

    private MsgHead head;
    //方位
    private int azimuth;
    //幅度
    private long amplitude;
    //时间
    private long time;
    @Override
    public ByteBuf toBuf() {
        ByteBuf buf = head.toBuf();
        buf.writeInt(azimuth);
        buf.writeLong(amplitude);
        buf.writeLong(time);
        return buf;
    }
}
