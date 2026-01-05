package com.scaffold.udp.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * pdw具体数据,字段顺序需要保持一致
 */
@Structure.FieldOrder({"Mark", "Toa", "Amp", "Pw", "Fre", "Fmin", "Fmax", "Angle"})
public class PdwData extends Structure {
    public byte Mark;
    //到达时间
    public long Toa;
    //幅度
    public int Amp;
    //脉宽
    public int Pw;
    //频率
    public int Fre;
    //最小频率
    public int Fmin;
    //最大频率
    public int Fmax;
    //方位
    public short Angle;

    public PdwData() {
        //内存对齐
        super(Structure.ALIGN_NONE);
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("Mark", "Toa", "Amp", "Pw", "Fre", "Fmin", "Fmax", "Angle");
    }

}
