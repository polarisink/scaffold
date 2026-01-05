package com.scaffold.udp.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

// 对应 C struct SigPack_All (信号分选识别结果)
@Structure.FieldOrder({"SigNum", "Sig_Pack"})
public class SigPackAll extends Structure {
    public int SigNum;
    public SigPack[] Sig_Pack;

    public SigPackAll() {
        super(Structure.ALIGN_NONE);
       /* Sig_Pack = new SigPack[MAX_SIG_PACKS];
        for (int i = 0; i < MAX_SIG_PACKS; i++) {
            Sig_Pack[i] = new SigPack();
        }*/
        Sig_Pack = (SigPack[]) new SigPack().toArray(PdwConstants.MAX_SIG_PACKS);
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("SigNum", "Sig_Pack");
    }

    /**
     *
     */
    @Override
    public void read() {
        super.read();
        for (SigPack sigPack : Sig_Pack) {
            sigPack.read();
        }
    }

    /*public SigPackAll() {
        super(Structure.ALIGN_NONE);
        // 初始化数组元素
        for (int i = 0; i < PdwConstants.MAX_SIG_PACKS; i++) {
            Sig_Pack[i] = new SigPack();
        }
    }*/

}