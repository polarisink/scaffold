package com.scaffold.udp.jna;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JnaService {
    public void parsePdwData(ByteBuf pdwBuf) {
        //处理pdw数据
        //到指定长度了，可以进行解析了
        PdwDataAll pdwDataAll = new PdwDataAll();
        pdwDataAll.load(pdwBuf);
        // 将 Java 结构体的更新同步回 Native Memory，供 DLL 访问
        pdwDataAll.write();
        // 准备输出结构体
        PdwPackAll pdwPackAll = new PdwPackAll();
        SigPackAll sigPackAll = new SigPackAll();
        // 调用 DLL
        int libFlag = PdwLib.INSTANCE.PdwParse(
                pdwDataAll,
                pdwPackAll,
                sigPackAll,
                1.0,
                50000,
                5
        );
        if (libFlag <= 0) {
            log.error("解析失败...");
            return;
        }
        pdwPackAll.read();
        sigPackAll.read();
        // 从 Native Memory 读取结果
    }
}
