package com.scaffold.udp.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface PdwLib extends Library {

    // 假设 Pdw_lib.dll 位于 Java 运行时环境可以找到的路径
    PdwLib INSTANCE = Native.load("Pdw_lib", PdwLib.class);

    /**
     * C++ 函数原型:
     * PROC_API unsigned int PdwParse(Pdw_data_All* pdw_data, PdwPack_All* pdw_pack, SigPack_All* sig_pack,
     * double min_pw, unsigned int max_dtoa, unsigned int min_dtoa);
     * * @param pdw_data 原始PDW数据结构体 (输入)
     *
     * @param pdw_pack 经计算后的脉冲参数 (输出)
     * @param sig_pack 信号分选识别结果 (输出)
     * @param min_pw   最小识别脉宽 (us)
     * @param max_dtoa 最大脉间间隔 (us)
     * @param min_dtoa 最小脉间间隔 (us)
     * @return 识别结果标志或信号个数
     */
    int PdwParse(
            PdwDataAll pdw_data,
            PdwPackAll pdw_pack, // 注意：PdwPack_All 结构体未完全定义在前面，需要自行补全
            SigPackAll sig_pack,
            double min_pw,
            long max_dtoa, // unsigned int 对应 Java 的 int
            long min_dtoa
    );
}