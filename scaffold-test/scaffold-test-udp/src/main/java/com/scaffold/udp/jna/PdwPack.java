package com.scaffold.udp.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

// 对应 C struct PdwPack
/*经计算后的脉冲参数 */
@Structure.FieldOrder({"mark", "CWFlag", "toa", "ampp", "pw", "fre", "fmin", "fmax", "fre_cent", "BW", "angle"})
public class PdwPack extends Structure {
    public byte mark;
    public byte CWFlag;
    public long toa; // uint64_t 对应 long
    public int ampp; // uint32_t 对应 int
    public int pw;
    public int fre;
    public int fmin;
    public int fmax;
    public int fre_cent;
    public int BW;
    public short angle; // uint16_t 对应 short

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("mark", "CWFlag", "toa", "ampp", "pw", "fre", "fmin", "fmax", "fre_cent", "BW", "angle");
    }

    // 强制 1 字节对齐
    public PdwPack() {
        super(Structure.ALIGN_NONE);
    }
}