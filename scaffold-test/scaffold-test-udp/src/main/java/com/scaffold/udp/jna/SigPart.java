package com.scaffold.udp.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

// 对应 C struct SigPart (信号特征结构体，用于解析结果)
@Structure.FieldOrder({"mark", "CWFlag", "pri_num", "pri", "pw_num", "pw", "fre_num", "fre", "BW_num", "BW", "angle", "amp"})
public class SigPart extends Structure {
    public byte mark;
    public byte CWFlag;
    public byte pri_num;
    public int[] pri;
    public byte pw_num;
    public int[] pw;
    public byte fre_num;
    public int[] fre;
    public byte BW_num;
    public int[] BW;
    public short angle;
    public int amp;

    public SigPart() {
        super(Structure.ALIGN_NONE);
        pri = new int[5];
        pw = new int[5];
        fre = new int[5];
        BW = new int[3];
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("mark", "CWFlag", "pri_num", "pri", "pw_num", "pw", "fre_num", "fre", "BW_num", "BW", "angle", "amp");
    }

}
