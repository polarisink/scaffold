package com.scaffold.udp.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

// 对应 C struct PdwPack_All
/*数据包中所有的脉冲字直接解包后存放的结构体 */
@Structure.FieldOrder({"PdwNum", "PdwParseUnit"})
public class PdwPackAll extends Structure {
    public int PdwNum;    // 脉冲字个数 (uint32_t 对应 int)
    public PdwPack[] PdwParseUnit; // 数组包含 PdwPack_All 个元素

    // 强制 1 字节对齐
    /*public PdwPackAll() {
        super(Structure.ALIGN_NONE);
        // JNA 数组字段的初始化方式
        for (int i = 0; i < PdwConstants.PDW_NUM_OF_PACK; i++) {
            PdwParseUnit[i] = new PdwPack();
        }
    }*/

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("PdwNum", "PdwParseUnit");
    }

    /**
     *
     */
    @Override
    public void read() {
        super.read();
        for (PdwPack pdwPack : PdwParseUnit) {
            pdwPack.read();
        }
    }

    public PdwPackAll() {
        super(Structure.ALIGN_NONE);
        /*PdwParseUnit = new PdwPack[PDW_NUM_OF_PACK];
        for (int i = 0; i < PDW_NUM_OF_PACK; i++) {
            PdwParseUnit[i] = new PdwPack();
        }*/
        PdwParseUnit = (PdwPack[]) new PdwPack().toArray(PdwConstants.PDW_NUM_OF_PACK);
    }

}