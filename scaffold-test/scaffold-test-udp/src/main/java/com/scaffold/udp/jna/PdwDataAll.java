package com.scaffold.udp.jna;

import com.sun.jna.Structure;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.List;

/**
 * pdw数据
 */
@Structure.FieldOrder({"PdwNum", "Pdwdata"})
public class PdwDataAll extends Structure {
    /**
     *
     */
    @Override
    public void write() {
        super.write();
        for (PdwData pdwdatum : Pdwdata) {
            pdwdatum.write();
        }
    }

    public int PdwNum = 8000;

    /**
     * 数据集合
     */
    public PdwData[] Pdwdata;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("PdwNum", "Pdwdata");
    }

    public PdwDataAll() {
        super(Structure.ALIGN_NONE);
        //这两种写法是一样的
        /*Pdwdata = new PdwData[PDW_NUM_OF_PACK];
        for (int i = 0; i < PDW_NUM_OF_PACK; i++) {
            Pdwdata[i] = new PdwData();
        }*/
        Pdwdata = (PdwData[]) new PdwData().toArray(PdwConstants.PDW_NUM_OF_PACK);
    }

    public void load(ByteBuf buf) {
        PdwNum = new MsgHead(buf).getLength();
        for (int i = 0; i < PdwNum; i++) {
            AdvancedBitStreamReader reader = new AdvancedBitStreamReader(buf.readBytes(22));
            PdwData data = new PdwData();
            reader.skipBits(1);//备用
            data.Fmax = (int) reader.readLong(20);//最大频率
            data.Fmin = (int) reader.readLong(20);//最小频率
            data.Fre = (int) reader.readLong(20);//中心频率
            data.Pw = (int) reader.readLong(24);//脉宽
            data.Amp = (int) reader.readLong(25);//幅度
            data.Toa = reader.readLong(42);//到达时间
            data.Mark = reader.readByte(8);
            data.Angle = reader.readShort(16);
            Pdwdata[i] = data;
        }
    }


}
